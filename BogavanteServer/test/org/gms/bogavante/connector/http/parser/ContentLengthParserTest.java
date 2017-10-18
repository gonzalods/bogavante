package org.gms.bogavante.connector.http.parser;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;
import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.header.parser.ContentLengthHeaderParser;
import org.gms.bogavante.connector.http.header.parser.HeaderParserChain;
import org.junit.Test;

import net.bytebuddy.implementation.bind.MethodDelegationBinder.ParameterBinding.Anonymous;

public class ContentLengthParserTest {

	@Test
	public void testContentLength() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("Content-Length");
		when(header.getHeader_value()).thenReturn("24586");
		when(request.getHeader("Content-Length")).thenReturn(null);
		
		ContentLengthHeaderParser parser = new ContentLengthHeaderParser();
		parser.parse(header, request);
		
		verify(request).setContentLength(24586L);
		verify(request).setHeader("Content-Length", "24586");
	}

	@Test
	public void testContentLengthTooLong() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("Content-Length");
		when(header.getHeader_value()).thenReturn("9223372036854775808");
		when(request.getHeader("Content-Length")).thenReturn(null);
		
		ContentLengthHeaderParser parser = new ContentLengthHeaderParser();
		parser.parse(header, request);
		
		verify(request).setContentLength(-1L);
		verify(request).setHeader("Content-Length", "9223372036854775808");
	}
	
	@Test
	public void testContentLengthListValuesEquals() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("Content-Length");
		when(header.getHeader_value()).thenReturn("24586,24586");
		when(request.getHeader("Content-Length")).thenReturn(null);
		
		ContentLengthHeaderParser parser = new ContentLengthHeaderParser();
		parser.parse(header, request);
		
		verify(request).setContentLength(24586L);
		verify(request).setHeader("Content-Length", "24586");
	}
	
	@Test
	public void testContentLengthListValuesDiff() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("Content-Length");
		when(header.getHeader_value()).thenReturn("24586,24585");
		when(request.getHeader("Content-Length")).thenReturn(null);
		
		ContentLengthHeaderParser parser = new ContentLengthHeaderParser();
		try{
			parser.parse(header, request);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(),is(400));
		}
		
		verify(request,never()).setContentLength(anyLong());
		verify(request,never()).setHeader(anyString(), anyString());
	}
	
	@Test
	public void testContentLengthDuplicateEqual() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("Content-Length");
		when(header.getHeader_value()).thenReturn("24586,24586");
		when(request.getHeader("Content-Length")).thenReturn("24586");
		
		ContentLengthHeaderParser parser = new ContentLengthHeaderParser();
		parser.parse(header, request);
		
		verify(request,never()).setContentLength(anyLong());
		verify(request,never()).setHeader(anyString(), anyString());
	}
	
	@Test
	public void testContentLengthDuplicateDiff() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("Content-Length");
		when(header.getHeader_value()).thenReturn("24586");
		when(request.getHeader("Content-Length")).thenReturn("24585");
		
		ContentLengthHeaderParser parser = new ContentLengthHeaderParser();
		try{
			parser.parse(header, request);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(),is(400));
		}
		
		verify(request,never()).setContentLength(anyLong());
		verify(request,never()).setHeader(anyString(), anyString());
	}
	
	@Test
	public void testContentLengthEmpty(){
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("Content-Length");
		when(header.getHeader_value()).thenReturn("");
		
		ContentLengthHeaderParser parser = new ContentLengthHeaderParser();
		try{
			parser.parse(header, request);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(),is(400));
		}
		
		verify(request,never()).setContentLength(anyLong());
		verify(request,never()).setHeader(anyString(), anyString());
	}
	
	@Test
	public void testContentLengthNotNumeric(){
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("Content-Length");
		when(header.getHeader_value()).thenReturn("for");
		
		ContentLengthHeaderParser parser = new ContentLengthHeaderParser();
		try{
			parser.parse(header, request);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(),is(400));
		}
		
		verify(request,never()).setContentLength(anyLong());
		verify(request,never()).setHeader(anyString(), anyString());
	}
	
	@Test
	public void testNextParser(){
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		HeaderParserChain nextParser = mock(HeaderParserChain.class);
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("gms.es");
		
		ContentLengthHeaderParser parser = new ContentLengthHeaderParser();
		parser.nextParseHeader(nextParser);
		parser.parse(header, request);
		
		verify(nextParser,times(1)).parse(header, request);
		
	}
}
