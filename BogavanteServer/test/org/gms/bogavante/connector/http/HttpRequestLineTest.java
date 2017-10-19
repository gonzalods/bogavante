package org.gms.bogavante.connector.http;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public class HttpRequestLineTest {

	//@Test
	public void testRequestEmpty() throws IOException{
		String request = "";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(in, 64);
		HttpRequestLine requestLine = new HttpRequestLine();
		int code = 200;
		String text = "OK";
		try{
			sis.readRequestLine(requestLine);
		}catch(HttpRequestParseException e){
			code = e.getCodeError();
			text = e.getMessage();
		}
		assertThat(code, is(400));
		assertThat(text, is("Bad Request"));
		sis.close();
	}
	
	
//	@Test
	public void testRequestRCLFOnly() throws IOException{
		String request = "\r\n";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(input, 1024);
		HttpRequestLine requestLine = new HttpRequestLine();
		int code = 200;
		String text = "OK";
		try {
			sis.readRequestLine(requestLine);
		} catch (HttpRequestParseException e) {
			code = e.getCodeError();
			text = e.getMessage();
		}
		
		assertThat(code, is(400));
		assertThat(text, is("Bad Request"));
		sis.close();
	}

//	@Test
	public void testRequestLineWithoutRCLF() throws IOException{
		String request = "\r\n GET /path";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(input, 1024);
		HttpRequestLine requestLine = new HttpRequestLine();
		int code = 200;
		String text = "OK";
		try {
			sis.readRequestLine(requestLine);
		} catch (HttpRequestParseException e) {
			code = e.getCodeError();
			text = e.getMessage();
		}
		assertThat(code, is(400));
		assertThat(text, is("Bad Request"));
		sis.close();
	}
	
	@Test
	public void testRequestLineIncomplete() throws IOException{
		String request = "GET /path";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(input, 1024);
		HttpRequestLine requestLine = new HttpRequestLine();
		int code = 200;
		String text = "OK";
		try {
			sis.readRequestLine(requestLine);
		} catch (HttpRequestParseException e) {
			code = e.getCodeError();
			text = e.getMessage();
		}
		assertThat(code, is(400));
		assertThat(text, is("Bad Request"));
		sis.close();
	}

	
	@Test
	public void testRequestLineURIMalFormed() throws IOException{
		String request = "GET /path uno/otro HTTP/1.1 ";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(input, 1024);
		HttpRequestLine requestLine = new HttpRequestLine();
		int code = 200;
		String text = "OK";
		try {
			sis.readRequestLine(requestLine);
		} catch (HttpRequestParseException e) {
			code = e.getCodeError();
			text = e.getMessage();
		}
		assertThat(code, is(400));
		assertThat(text, is("Bad Request"));
		sis.close();
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
		String request = "\r\nGET /path\t HTTP/1.1 \r\n";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(input, 1024);
		HttpRequestLine requestLine = new HttpRequestLine();

		sis.readRequestLine(requestLine);

		assertThat(requestLine.getMethod(), is("GET"));
		assertThat(requestLine.getRequest_target(), is("/path"));
		assertThat(requestLine.getHTTP_version(), is("HTTP/1.1"));
		sis.close();
	}
}
