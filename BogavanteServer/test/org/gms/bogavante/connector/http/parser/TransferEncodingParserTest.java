package org.gms.bogavante.connector.http.parser;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;
import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.header.parser.TransferEncodingHeaderParser;
import org.junit.Test;

public class TransferEncodingParserTest {

	@Test
	public void testCorrectSupportedCodings() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("transfer-encoding");
		when(header.getHeader_value()).thenReturn("deflate, chunked");
		
		TransferEncodingHeaderParser parser = new TransferEncodingHeaderParser();
		parser.parse(header, request);
		
		verify(request).setHeader("transfer-encoding", new String[]{"deflate","chunked"});
	}
	
	@Test
	public void testInCorrectOrderSupportedCodings() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("transfer-encoding");
		when(header.getHeader_value()).thenReturn("chunked, deflate");
		
		TransferEncodingHeaderParser parser = new TransferEncodingHeaderParser();
		try{
			parser.parse(header, request);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(), is(400));
		}
		
		verify(request,never()).setHeader(anyString(), any());
	}
	
	@Test
	public void testInCorrectDefSupportedCodings() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("transfer-encoding");
		when(header.getHeader_value()).thenReturn("deflate, chunked; par=val");
		
		TransferEncodingHeaderParser parser = new TransferEncodingHeaderParser();
		try{
			parser.parse(header, request);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(), is(400));
		}
		
		verify(request,never()).setHeader(anyString(), any());
	}
	
	@Test
	public void testCorrectUnSupportedCodings() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("transfer-encoding");
		when(header.getHeader_value()).thenReturn("gzip, chunked");
		
		TransferEncodingHeaderParser parser = new TransferEncodingHeaderParser();
		
		try{
			parser.parse(header, request);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(), is(501));
		}
		
		verify(request,never()).setHeader(anyString(), any());
	}
	
	@Test
	public void testCorrectExtensionCodings() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("transfer-encoding");
		when(header.getHeader_value()).thenReturn("7z, chunked");
		
		TransferEncodingHeaderParser parser = new TransferEncodingHeaderParser();
		parser.parse(header, request);
		
		verify(request).setHeader("transfer-encoding", new String[]{"7z","chunked"});
	}
	
	@Test
	public void testCorrectExtensionCodingsWithParameters() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("transfer-encoding");
		when(header.getHeader_value()).thenReturn("7z ;param1= value1; param2= value2, chunked");
		
		TransferEncodingHeaderParser parser = new TransferEncodingHeaderParser();
		parser.parse(header, request);
		
		verify(request).setHeader("transfer-encoding", new String[]{"7z ;param1= value1; param2= value2","chunked"});
	}
	
	@Test
	public void testCorrectExtensionCodingsWithIncorrectParameters() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("transfer-encoding");
		when(header.getHeader_value()).thenReturn("7z ;param1= value1; param2 value2, chunked");
		
		TransferEncodingHeaderParser parser = new TransferEncodingHeaderParser();
		try{
			parser.parse(header, request);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(), is(400));
		}
		
		verify(request,never()).setHeader(anyString(), any());
	}

}
