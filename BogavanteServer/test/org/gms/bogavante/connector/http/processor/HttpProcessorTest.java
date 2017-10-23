package org.gms.bogavante.connector.http.processor;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.gms.bogavante.connector.http.HttpContext;
import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.processor.HttpProcessor;
import org.junit.Test;

public class HttpProcessorTest {

	@Test
	public void testHttpRequestParseException() throws IOException{
		
		String request = "";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		
		Socket socket = mock(Socket.class);
		HttpContext context = mock(HttpContext.class);
		HttpRequestProcessor requestProcessor = mock(Http1RequestProcessor.class);
		
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		when(context.getRequestProcesor(any(), any())).thenReturn(requestProcessor);
		doThrow(new HttpRequestParseException(400, "Bad Request")).when(requestProcessor).process();
		
		HttpProcessor processor = new HttpProcessor(context);
		processor.process(socket);
		
		ByteArrayOutputStream out = (ByteArrayOutputStream)socket.getOutputStream();
		
		String responseStatus = new String(out.toByteArray());
		
		assertThat(responseStatus, is("HTTP/1.1 400 Bad Request\r\nConnection: close\r\n\r\n"));
		
		out.write("a".getBytes());
		in.close();
		
	}
	
	@Test
	public void testHttpServerError() throws IOException{
		
		String request = "";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		
		Socket socket = mock(Socket.class);
		HttpContext context = mock(HttpContext.class);
		HttpRequestProcessor requestProcessor = mock(Http1RequestProcessor.class);
		
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		when(context.getRequestProcesor(any(), any())).thenReturn(requestProcessor);
		doThrow(new IOException()).when(requestProcessor).process();
		
		HttpProcessor processor = new HttpProcessor(context);
		processor.process(socket);
		
		ByteArrayOutputStream out = (ByteArrayOutputStream)socket.getOutputStream();
		
		String responseStatus = new String(out.toByteArray());
		
		assertThat(responseStatus, is("HTTP/1.1 500 Server Error\r\nConnection: close\r\n\r\n"));
		
		out.write("a".getBytes());
		in.close();
		
	}
	
	@Test
	public void testKeepAlive() throws IOException{
		String request = "Cualquier entrada con más entrada";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		
		
		Socket socket = mock(Socket.class);
		HttpContext context = mock(HttpContext.class);
		HttpRequestProcessor requestProcessor = mock(Http1RequestProcessor.class);
		
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		when(context.getRequestProcesor(any(), any())).thenReturn(requestProcessor);
		when(requestProcessor.isKeepAlive()).thenReturn(true, false);
		
		HttpProcessor processor = new HttpProcessor(context);
		processor.process(socket);
		
		verify(requestProcessor,times(2)).process();
	}

//	@Test
	public void testBadOnlyRequestLine() throws IOException{
		HttpProcessor processor = new HttpProcessor(null);
		String request = "GET / HTTP/1.1\r\n";
		Socket socket = mock(Socket.class);
		
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		
		processor.process(socket);
		
		ByteArrayOutputStream out = (ByteArrayOutputStream)socket.getOutputStream();
		
		String responseStatus = new String(out.toByteArray());
		
		assertThat(responseStatus, is("HTTP/1.1 400 Bad Request\r\n"));
		
		out.close();
		in.close();
		
	}
	
//	@Test
	public void testOnlyRequestLine() throws IOException{
		HttpProcessor processor = new HttpProcessor(null);
		String request = "GET / HTTP/1.1\r\n\r\n";
		Socket socket = mock(Socket.class);
		
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		
		processor.process(socket);
		
		ByteArrayOutputStream out = (ByteArrayOutputStream)socket.getOutputStream();
		
		String responseStatus = new String(out.toByteArray());
		
		assertThat(responseStatus, is("HTTP/1.1 200 OK\r\n"));
		
		out.close();
		in.close();
		
	}
	
//	@Test
	public void testBadHeader() throws IOException{
		HttpProcessor processor = new HttpProcessor(null);
		String request = "GET / HTTP/1.1\r\nContent-Type : text/html\r\n\r\n";
		Socket socket = mock(Socket.class);
		
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		
		processor.process(socket);
		
		ByteArrayOutputStream out = (ByteArrayOutputStream)socket.getOutputStream();
		
		String responseStatus = new String(out.toByteArray());
		
		assertThat(responseStatus, is("HTTP/1.1 400 Bad Request\r\n"));
		
		out.close();
		in.close();
		
	}
	
//	@Test
	public void testOneHeader() throws IOException{
		HttpProcessor processor = new HttpProcessor(null);
		String request = "GET / HTTP/1.1\r\nContent-Type: text/html\r\n\r\n";
		Socket socket = mock(Socket.class);
		
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		
		processor.process(socket);
		
		ByteArrayOutputStream out = (ByteArrayOutputStream)socket.getOutputStream();
		
		String responseStatus = new String(out.toByteArray());
		
		assertThat(responseStatus, is("HTTP/1.1 200 OK\r\n"));
		
		out.close();
		in.close();
		
	}
	
//	@Test
	public void testTwoHeader() throws IOException{
		HttpProcessor processor = new HttpProcessor(null);
		String request = "GET / HTTP/1.1\r\nContent-Type: text/html\r\nHost: www.google.es\r\n\r\n";
		Socket socket = mock(Socket.class);
		
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		when(socket.getInputStream()).thenReturn(in);
		when(socket.getOutputStream()).thenReturn(new ByteArrayOutputStream());
		
		processor.process(socket);
		
		ByteArrayOutputStream out = (ByteArrayOutputStream)socket.getOutputStream();
		
		String responseStatus = new String(out.toByteArray());
		
		assertThat(responseStatus, is("HTTP/1.1 200 OK\r\n"));
		
		out.close();
		in.close();
		
	}
	
}
