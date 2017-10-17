package org.gms.bogavante.connector.http;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public class HttpRequestLineTest {

	@Test
	public void requestEmptyTest() throws IOException{
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
	
	
	@Test
	public void requestRCLFOnlyTest() throws IOException{
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

	@Test
	public void requestLineWithoutRCLFTest() throws IOException{
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
	public void requestLineIncomplete1Test() throws IOException{
		String request = "\r\n \u000B\rGET /path\r\n";
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
	public void requestLineIncomplete2Test() throws IOException{
		String request = "\r\nGET /path \r\n";
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
	public void requestLineURIMalFormedTest() throws IOException{
		String request = "GET /path uno/otro path \r\n";
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
	public void requestLineTest() throws IOException{
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
