package org.gms.bogavante.connector.http;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class SoketInputStreamTest {

	@Test
	public void testReadRequestLineSimple() throws IOException{
		String request = "\r\nGET /path\t HTTP/1.1 \r\n";
 
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		HttpRequestLine requestLine = mock(HttpRequestLine.class);
		
		SocketInputStream sis = new SocketInputStream(input, 1024);
		sis.readRequestLine(requestLine);

		char[] res = "GET /path\t HTTP/1.1 ".toCharArray();
		char[] buff = Arrays.copyOf(res, HttpRequestLine.INITIAL_LENGTH_LINE);
		verify(requestLine).parseLine(buff, res.length);
		
		sis.close();
	}
	
	@Test
	public void testReadRequestLineWithHeaders() throws IOException{
		String request = "\r\nGET /path\t HTTP/1.1 \r\n"
				+ "header-name: header-value\r\n";
 
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		HttpRequestLine requestLine = mock(HttpRequestLine.class);
		
		SocketInputStream sis = new SocketInputStream(input, 1024);
		sis.readRequestLine(requestLine);

		char[] res = "GET /path\t HTTP/1.1 ".toCharArray();
		char[] buff = Arrays.copyOf(res, HttpRequestLine.INITIAL_LENGTH_LINE);
		verify(requestLine).parseLine(buff, res.length);
		assertThat(sis.available(),is(27));
		
		sis.close();
	}

	@Test
	public void testRequestEmpty() throws IOException{
		String request = "";
		 
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		HttpRequestLine requestLine = mock(HttpRequestLine.class);
		
		SocketInputStream sis = new SocketInputStream(input, 1024);
		try{
			sis.readRequestLine(requestLine);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(), is(400));
		}
		verify(requestLine,never()).parseLine(any(), anyInt());
		sis.close();
	}
	
	@Test
	public void testRequestRCLFOnly() throws IOException{
		String request = "\r\n";
		
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		HttpRequestLine requestLine = mock(HttpRequestLine.class);
		
		SocketInputStream sis = new SocketInputStream(input, 1024);
		try{
			sis.readRequestLine(requestLine);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(), is(400));
		}
		verify(requestLine,never()).parseLine(any(), anyInt());
		sis.close();
	}
	
	@Test
	public void testRequestIncomplete() throws IOException{
		String request = "\r\n GET /path";
		
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		HttpRequestLine requestLine = mock(HttpRequestLine.class);
		
		SocketInputStream sis = new SocketInputStream(input, 1024);
		try{
			sis.readRequestLine(requestLine);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(), is(400));
		}
		verify(requestLine,never()).parseLine(any(), anyInt());
		sis.close();
	}
	
	
	@Test
	public void testReadOneHeader() throws IOException{
		String request = "header-name: header-value\r\n";
		
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		HttpHeader headerLine = mock(HttpHeader.class);
		
		SocketInputStream sis = new SocketInputStream(input, 1024);
		sis.readHeader(headerLine);
		
		char[] res = "header-name: header-value".toCharArray();
		char[] buff = Arrays.copyOf(res, HttpRequestLine.INITIAL_LENGTH_LINE);
		verify(headerLine).parseLine(buff, res.length);
		
		sis.close();
	}
	@Test
	public void testReadTwoHeaders() throws IOException{
		String request = "header-name: header-value; more things\r\n"
				+ "other-header: other-value; and more\r\n";
		
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		HttpHeader headerLine = mock(HttpHeader.class);
		
		SocketInputStream sis = new SocketInputStream(input, 1024);
		sis.readHeader(headerLine);
		
		char[] res = "header-name: header-value; more things".toCharArray();
		char[] buff = Arrays.copyOf(res, HttpRequestLine.INITIAL_LENGTH_LINE);
		verify(headerLine).parseLine(buff, res.length);
		assertThat(sis.available(),is("other-header: other-value; and more\r\n".length()));
		
		sis.close();
	}
	
	@Test
	public void testHeaderEmpty() throws IOException{
		String request = "";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		HttpHeader headerLine = mock(HttpHeader.class);
		
		SocketInputStream sis = new SocketInputStream(input, 1024);
		try{
			sis.readHeader(headerLine);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(), is(400));
		}
		verify(headerLine,never()).parseLine(any(), anyInt());
		sis.close();
	}
	
	@Test
	public void testHeaderFinal() throws IOException{
		String request = "\r\n";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		HttpHeader headerLine = mock(HttpHeader.class);

		SocketInputStream sis = new SocketInputStream(input, 1024);
		sis.readHeader(headerLine);
		
		verify(headerLine,never()).parseLine(any(), anyInt());
		
		sis.close();
	}

	@Test
	public void testHeaderObsFold() throws IOException{
		String request = "ContentType: text/html,\r\n\t*/*\r\nContent-length: 54\r\n";
		ByteArrayInputStream input = new ByteArrayInputStream(request.getBytes());
		HttpHeader headerLine = mock(HttpHeader.class);

		SocketInputStream sis = new SocketInputStream(input, 1024);
		sis.readHeader(headerLine);
		
		char[] res = "ContentType: text/html, */*".toCharArray();
		char[] buff = Arrays.copyOf(res, HttpRequestLine.INITIAL_LENGTH_LINE);
		verify(headerLine).parseLine(buff, res.length);
		
		assertThat(sis.available(),is("Content-length: 54\r\n".length()));
		
		sis.close();
	}
}
