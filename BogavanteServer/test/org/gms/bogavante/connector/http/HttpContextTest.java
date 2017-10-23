package org.gms.bogavante.connector.http;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.gms.bogavante.connector.http.processor.Http1RequestProcessor;
import org.gms.bogavante.connector.http.processor.HttpRequestProcessor;
import org.junit.Test;

public class HttpContextTest {

	@Test
	public void testHttp1_1_Request() throws IOException{
		String request = "GET / HTTP/1.1\r\n";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		
		HttpContext context = new HttpContext("http");
		HttpRequestProcessor reqProcessor = context.getRequestProcesor(new SocketInputStream(input, 1024), new ByteArrayOutputStream());
		
		assertThat(reqProcessor, instanceOf(Http1RequestProcessor.class));
	}
	
	@Test
	public void testHttp1_0_Request() throws IOException{
		String request = "GET / HTTP/1.0\r\n";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		
		HttpContext context = new HttpContext("http");
		HttpRequestProcessor reqProcessor = context.getRequestProcesor(new SocketInputStream(input, 1024), new ByteArrayOutputStream());
		
		assertThat(reqProcessor, instanceOf(Http1RequestProcessor.class));
	}
	
	@Test
	public void testHttp2_0_Request() throws IOException{
		String request = "GET / HTTP/2.0\r\n";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		
		int code = -1;
		
		HttpContext context = new HttpContext("http");
		try {
			context.getRequestProcesor(new SocketInputStream(input, 1024), new ByteArrayOutputStream());
		}catch (HttpRequestParseException e) {
			code = e.getCodeError();
		}
		
		assertThat(code, is(505));
	}
	

}
