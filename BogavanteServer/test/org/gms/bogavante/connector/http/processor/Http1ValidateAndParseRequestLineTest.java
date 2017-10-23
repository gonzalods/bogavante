package org.gms.bogavante.connector.http.processor;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.gms.bogavante.connector.http.HttpRequestLine;
import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.junit.Test;

public class Http1ValidateAndParseRequestLineTest {

	@Test
	public void testErrorURI() {
		HttpRequest request = mock(HttpRequest.class);
		HttpRequestLine requestLine = mock(HttpRequestLine.class);
		
		when(requestLine.getMethod()).thenReturn("GET");
		when(requestLine.getRequest_target()).thenReturn("http://\\authority/path?query");
		
		Http1RequestLineValidatorAndParser validatorAndParser = new Http1RequestLineValidatorAndParser();
		int code = 200;
		try{
			validatorAndParser.validateAndParse(request, requestLine);
		}catch(HttpRequestParseException e){
			code = e.getCodeError();
		}
		
		assertThat(code, is(400));
		verify(request, never()).setAuthority(anyString());
	}
	
	@Test
	public void testNoHostURI(){
		HttpRequest request = mock(HttpRequest.class);
		HttpRequestLine requestLine = mock(HttpRequestLine.class);
		
		when(requestLine.getMethod()).thenReturn("GET");
		when(requestLine.getRequest_target()).thenReturn("http:///path?query");
		
		Http1RequestLineValidatorAndParser validatorAndParser = new Http1RequestLineValidatorAndParser();
		int code = 200;
		try{
			validatorAndParser.validateAndParse(request, requestLine);
		}catch(HttpRequestParseException e){
			code = e.getCodeError();
		}
		
		assertThat(code, is(400));
		verify(request, never()).setAuthority(anyString());
	}
	
	@Test
	public void testURIWhitUserInfo(){
		HttpRequest request = mock(HttpRequest.class);
		HttpRequestLine requestLine = mock(HttpRequestLine.class);
		
		when(requestLine.getMethod()).thenReturn("GET");
		when(requestLine.getRequest_target()).thenReturn("http://user:pass@host:5485/path?query");
		
		Http1RequestLineValidatorAndParser validatorAndParser = new Http1RequestLineValidatorAndParser();
		int code = 200;
		try{
			validatorAndParser.validateAndParse(request, requestLine);
		}catch(HttpRequestParseException e){
			code = e.getCodeError();
		}
		
		assertThat(code, is(400));
		verify(request, never()).setAuthority(anyString());
		
	}
	
	@Test
	public void testErrorMethod(){
		HttpRequest request = mock(HttpRequest.class);
		HttpRequestLine requestLine = mock(HttpRequestLine.class);
		
		when(requestLine.getMethod()).thenReturn("OTRACOSA");
		when(requestLine.getRequest_target()).thenReturn("http://host:5485/path?query");
		
		Http1RequestLineValidatorAndParser validatorAndParser = new Http1RequestLineValidatorAndParser();
		int code = 200;
		try{
			validatorAndParser.validateAndParse(request, requestLine);
		}catch(HttpRequestParseException e){
			code = e.getCodeError();
		}
		
		assertThat(code, is(400));
		verify(request, never()).setMethod(anyString());
	}
	
	@Test
	public void testAbsolutePath(){
		fail("Not implemented yet");
	}

}
