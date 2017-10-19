package org.gms.bogavante.connector.http;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.gms.bogavante.connector.http.processor.HttpProcessor;
import org.junit.Test;

public class HttpProcessorTest {

	@Test
	public void testEmptyRequest() throws IOException{
		HttpProcessor processor = new HttpProcessor(null);
		String request = "";
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
	
	@Test
	public void testBadRequestLine() throws IOException{
		HttpProcessor processor = new HttpProcessor(null);
		String request = "GET /\r\n";
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

	@Test
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
	
	@Test
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
	
	@Test
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
	
	@Test
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
	
	@Test
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
	
	@Test
	public void testTwoHeaderWithObsFold() throws IOException{
		HttpProcessor processor = new HttpProcessor(null);
		String request = "GET / HTTP/1.1\r\nContent-Type: text/html,\r\n */*\r\nHost: www.google.es\r\n\r\n";
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
