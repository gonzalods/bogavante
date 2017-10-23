package org.gms.bogavante.connector.http;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public class HttpRequestLineTest {

	@Test
	public void testRequestLineIncomplete() throws IOException{
		String request = "GET /path";
		HttpRequestLine requestLine = new HttpRequestLine();
		int code = 200;
		String text = "OK";
		try {
			requestLine.parseLine(request.toCharArray(), request.length());
		} catch (HttpRequestParseException e) {
			code = e.getCodeError();
			text = e.getMessage();
		}
		assertThat(code, is(400));
		assertThat(text, is("Bad Request"));
	}

	
	@Test
	public void testRequestLineURIMalFormed() throws IOException{
		String request = "GET /path uno/otro HTTP/1.1 ";
		HttpRequestLine requestLine = new HttpRequestLine();
		int code = 200;
		String text = "OK";
		try {
			requestLine.parseLine(request.toCharArray(), request.length());
		} catch (HttpRequestParseException e) {
			code = e.getCodeError();
			text = e.getMessage();
		}
		assertThat(code, is(400));
		assertThat(text, is("Bad Request"));
	}
	
	@Test 
	public void testInvalidHttpVersion(){
		String request ="GET / http/8";
		HttpRequestLine requestLine = new HttpRequestLine();
		int code = 200;
		String text = "OK";
		try{
			requestLine.parseLine(request.toCharArray(), request.length());
		}catch(HttpRequestParseException e){
			code = e.getCodeError();
			text = e.getMessage();			
		}
		assertThat(code, is(400));
		assertThat(text, is("Bad Request"));
	}
	
	@Test
	public void testRequestLine() throws IOException{
		String request = "GET /path\t HTTP/1.1 ";
		HttpRequestLine requestLine = new HttpRequestLine();

		requestLine.parseLine(request.toCharArray(), request.length());

		assertThat(requestLine.getMethod(), is("GET"));
		assertThat(requestLine.getRequest_target(), is("/path"));
		assertThat(requestLine.getHTTP_version(), is("HTTP/1.1"));

	}
}
