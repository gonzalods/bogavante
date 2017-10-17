package org.gms.bogavante.connector.http;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public class HttpHeaderTest {

	@Test
	public void headerEmptyTest() throws IOException{
		String request = "";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(in, 64);
		HttpHeader headerLine = new HttpHeader();
		int code = 200;
		String text = "OK";

		try{
			sis.readHeader(headerLine);
		}catch(HttpRequestParseException e){
			code = e.getCodeError();
			text = e.getMessage();
		}
		assertThat(code, is(400));
		assertThat(text, is("Bad Request"));
		sis.close();
	}
	
	@Test
	public void headerFinalTest() throws IOException{
		String request = "\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(in, 64);
		HttpHeader headerLine = new HttpHeader();

		sis.readHeader(headerLine);
		
		assertNull(headerLine.getHeader_name());
		sis.close();
	}

	@Test
	public void headerSimpleTest() throws IOException{
		String request = "ContentType: text/html\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(in, 64);
		HttpHeader headerLine = new HttpHeader();
		
		sis.readHeader(headerLine);
		
		assertThat(headerLine.getHeader_name(),is("ContentType"));
		assertThat(headerLine.isObs_fold(),is(false));
		assertThat(headerLine.getHeader_value(),is("text/html"));
		
		sis.close();
	}
	
	@Test
	public void headerOnlyNameTest() throws IOException{
		String request = "ContentType: \r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(in, 64);
		HttpHeader headerLine = new HttpHeader();
		
		sis.readHeader(headerLine);
		
		assertThat(headerLine.getHeader_name(),is("ContentType"));
		assertThat(headerLine.isObs_fold(),is(false));
		assertThat(headerLine.getHeader_value(),is(""));
		
		sis.close();
	}
	
	@Test
	public void headerDobleColonTest() throws IOException{
		String request = "ContentType: text/html:text/plain\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(in, 64);
		HttpHeader headerLine = new HttpHeader();
		
		sis.readHeader(headerLine);
		
		assertThat(headerLine.getHeader_name(),is("ContentType"));
		assertThat(headerLine.isObs_fold(),is(false));
		assertThat(headerLine.getHeader_value(),is("text/html:text/plain"));
		
		sis.close();
	}
	
	@Test
	public void headerObsFoldTest() throws IOException{
		String request = "ContentType: text/html,\r\n\t*/*\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(in, 64);
		HttpHeader headerLine = new HttpHeader();
		
		sis.readHeader(headerLine);
		
		assertThat(headerLine.getHeader_name(),is("ContentType"));
		assertThat(headerLine.isObs_fold(),is(true));
		assertThat(headerLine.getHeader_value(),is("text/html, */*"));
		
		sis.close();
	}
	
	@Test
	public void headerSimpleWithEndTest() throws IOException{
		String request = "ContentType: text/html\r\n\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(in, 64);
		HttpHeader headerLine = new HttpHeader();
		
		sis.readHeader(headerLine);
		
		assertThat(headerLine.getHeader_name(),is("ContentType"));
		assertThat(headerLine.isObs_fold(),is(false));
		assertThat(headerLine.getHeader_value(),is("text/html"));
		
		headerLine = new HttpHeader();
		
		sis.readHeader(headerLine);
		
		assertNull(headerLine.getHeader_name());
		
		sis.close();
	}
	@Test
	public void twoHeaderSimpleTest() throws IOException{
		
		String request = "ContentType: text/html\r\nHost:www.google.es\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(in, 64);
		HttpHeader headerLine = new HttpHeader();
		
		sis.readHeader(headerLine);
		
		assertThat(headerLine.getHeader_name(),is("ContentType"));
		assertThat(headerLine.isObs_fold(),is(false));
		assertThat(headerLine.getHeader_value(),is("text/html"));
		
		headerLine = new HttpHeader();
		
		sis.readHeader(headerLine);
		
		assertThat(headerLine.getHeader_name(),is("Host"));
		assertThat(headerLine.isObs_fold(),is(false));
		assertThat(headerLine.getHeader_value(),is("www.google.es"));
		
		sis.close();
	}
	
	@Test
	public void twoHeaderFirstObsFoldTest() throws IOException{
		
		String request = "ContentType: text/html;\r\n */*\r\nHost:www.google.es\r\n";
		ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
		SocketInputStream sis = new SocketInputStream(in, 64);
		HttpHeader headerLine = new HttpHeader();
		
		sis.readHeader(headerLine);
		
		assertThat(headerLine.getHeader_name(),is("ContentType"));
		assertThat(headerLine.isObs_fold(),is(true));
		assertThat(headerLine.getHeader_value(),is("text/html; */*"));
		
		headerLine = new HttpHeader();
		
		sis.readHeader(headerLine);
		
		assertThat(headerLine.getHeader_name(),is("Host"));
		assertThat(headerLine.isObs_fold(),is(false));
		assertThat(headerLine.getHeader_value(),is("www.google.es"));
		
		sis.close();
	}
}