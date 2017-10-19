package org.gms.bogavante.connector.http;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Test;

public class HttpHeaderTest {

	@Test
	public void headerSimpleTest() throws IOException{
		String request = "ContentType: text/html";
		HttpHeader headerLine = new HttpHeader();
		
		headerLine.parseLine(request.toCharArray(), request.length());;
		
		assertThat(headerLine.getHeader_name(),is("ContentType"));
		assertThat(headerLine.getHeader_value(),is("text/html"));
		
	}
	
	@Test
	public void headerOnlyNameTest() throws IOException{
		String request = "ContentType: ";
		HttpHeader headerLine = new HttpHeader();
		
		headerLine.parseLine(request.toCharArray(), request.length());
		
		assertThat(headerLine.getHeader_name(),is("ContentType"));
		assertThat(headerLine.getHeader_value(),is(""));
		
	}
	
	@Test
	public void headerDobleColonTest() throws IOException{
		String request = "ContentType: text/html:text/plain ";
		HttpHeader headerLine = new HttpHeader();
		
		headerLine.parseLine(request.toCharArray(), request.length());
		
		assertThat(headerLine.getHeader_name(),is("ContentType"));
		assertThat(headerLine.getHeader_value(),is("text/html:text/plain"));
		
	}
	
	@Test
	public void headerBlankColonTest() throws IOException{
		String request = "ContentType : text/html:text/plain";
		HttpHeader headerLine = new HttpHeader();
		
		try{
			headerLine.parseLine(request.toCharArray(), request.length());
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(),is(400));
		}
	}
	
}
