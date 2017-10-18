package org.gms.bogavante.connector.http.parser;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;
import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.header.parser.HeaderParserChain;
import org.gms.bogavante.connector.http.header.parser.HostHeaderParser;
import org.junit.Test;

public class HostHeaderParserTest {

	@Test
	public void testDoubleHostHeader(){
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("www.gms.es");
		when(request.getHeader("Host")).thenReturn("algo");
		
		HostHeaderParser parser = new HostHeaderParser();
		try{
			parser.parse(header, request);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(),is(400));
		}
		verify(request,never()).setHeader(anyString(), anyString());
	}
	
	@Test
	public void testValidateKOHostHeaderValue() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		// Host header alwasys required, with or without fied-value
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("www.gms.es/");
		
		HostHeaderParser parser = new HostHeaderParser();
		try{
			parser.parse(header, request);
		}catch(HttpRequestParseException e){
			assertThat(e.getCodeError(),is(400));
		}
		verify(request,never()).setHeader(anyString(), anyString());
		
	}
	@Test
	public void testAbsoluteFormURI() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		// Host header alwasys required, with or without fied-value
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("www.gms.es:8080");
		
		when(request.getRequestURI()).thenReturn("http://www.gms.es/casa/hab%02gonzalo.html");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://www.gms.es/casa/hab%02gonzalo.html");
		verify(request).setHeader("Host", "www.gms.es:8080");
		
	}
	
	@Test
	public void testOriginFormURIWithAuthConfInServer() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		// Host header alwasys required, with or without fied-value
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("");
		
		when(request.getScheme()).thenReturn("http");
		when(request.getAuthority()).thenReturn("gms.es:8080");
		when(request.getRequestURI()).thenReturn("/casa/hab%02gonzalo.html?cadena=consulta");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://gms.es:8080/casa/hab%02gonzalo.html?cadena=consulta");
		verify(request).setHeader("Host", "");
	}
	
	@Test
	public void testOriginFormURIWitouthAuthConfInServerAndWithHostHeader() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("www.gms.es:8080");
		
		when(request.getScheme()).thenReturn("http");
		when(request.getRequestURI()).thenReturn("/casa/hab%02gonzalo.html?cadena=consulta");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://www.gms.es:8080/casa/hab%02gonzalo.html?cadena=consulta");
		verify(request).setHeader("Host", "www.gms.es:8080");
	}
	
	@Test
	public void testOriginFormURIWitouthAuthConfInServerAndWithoutHostHeader() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("");
		
		when(request.getServerName()).thenReturn("server-name");
		when(request.getScheme()).thenReturn("http");
		when(request.getLocalPort()).thenReturn(80);
		when(request.getRequestURI()).thenReturn("/casa/hab%02gonzalo.html?cadena=consulta");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://server-name/casa/hab%02gonzalo.html?cadena=consulta");
		verify(request).setHeader("Host", "");
	}
	
	@Test
	public void testAuthorityFormURIWitAuthConfInServer() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("");
		
		when(request.getScheme()).thenReturn("http");
		when(request.getAuthority()).thenReturn("gms.es:8080");
		when(request.getRequestURI()).thenReturn("gms.es");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://gms.es:8080");
		verify(request).setHeader("Host", "");
	}
	
	@Test
	public void testAuthorityFormWithoutPortURIWithoutAuthConfInServer() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("");
		when(request.getScheme()).thenReturn("http");
		when(request.getRequestURI()).thenReturn("gms.es");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://gms.es");
		verify(request).setHeader("Host", "");
	}
	
	@Test
	public void testAuthorityFormWithPortURIWithoutAuthConfInServer() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("www.gms.es");
		when(request.getScheme()).thenReturn("http");
		when(request.getRequestURI()).thenReturn("gms.es:8080");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://gms.es:8080");
		verify(request).setHeader("Host", "www.gms.es");
	}
	
	@Test
	public void testAbsolutePathWithoutAuthConfInServerWithHostHeaderValue() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("www.gms.es");
		when(request.getScheme()).thenReturn("http");
		when(request.getRequestURI()).thenReturn("/casa/hab%02gonzalo.html");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://www.gms.es/casa/hab%02gonzalo.html");
		verify(request).setHeader("Host", "www.gms.es");
	}
	
	@Test
	public void testAbsolutePathWithoutAuthConfInServerWithoutHostHeaderValue() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("");
		when(request.getScheme()).thenReturn("http");
		when(request.getServerName()).thenReturn("server-name");
		when(request.getLocalPort()).thenReturn(10039);
		when(request.getRequestURI()).thenReturn("/casa/hab%02gonzalo.html");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://server-name:10039/casa/hab%02gonzalo.html");
		verify(request).setHeader("Host", "");
	}

	@Test
	public void testAsteriskFormURIWithAuthConfInServer() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);

		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("www.gms.es");
		
		when(request.getScheme()).thenReturn("http");

		when(request.getRequestURI()).thenReturn("*");
		when(request.getAuthority()).thenReturn("gms.es:8080");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://gms.es:8080");
		verify(request).setHeader("Host", "www.gms.es");
	}
	
	@Test
	public void testAsteriskFormURIWithoutAuthConfigInServerWhithHostHeaderValue() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("www.gms.es");

		when(request.getScheme()).thenReturn("http");
		when(request.getRequestURI()).thenReturn("*");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://www.gms.es");
		verify(request).setHeader("Host", "www.gms.es");
	}
	
	@Test
	public void testAsteriskFormURIWithoutAuthConfigInServerWhithoutHostHeaderValue() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("");

		when(request.getScheme()).thenReturn("https");
		when(request.getServerName()).thenReturn("server-name");
		when(request.getLocalPort()).thenReturn(10042);
		
		when(request.getRequestURI()).thenReturn("*");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("https://server-name:10042");
		verify(request).setHeader("Host", "");
	}
	
	@Test
	public void testValidateHostWithIPv4PortDefault() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		// Host header alwasys required, with or without fied-value
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("123.125.25.3");
		
		when(request.getScheme()).thenReturn("http");
		when(request.getAuthority()).thenReturn("gms.es:8080");
		when(request.getRequestURI()).thenReturn("/casa/hab%02gonzalo.html?cadena=consulta");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://gms.es:8080/casa/hab%02gonzalo.html?cadena=consulta");
		verify(request).setHeader("Host", "123.125.25.3");
	}
	
	@Test
	public void testValidateHostWithIPv4Port8080t() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		// Host header alwasys required, with or without fied-value
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("123.125.25.3:8080");
		
		when(request.getScheme()).thenReturn("http");
		when(request.getAuthority()).thenReturn("gms.es:8080");
		when(request.getRequestURI()).thenReturn("/casa/hab%02gonzalo.html?cadena=consulta");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://gms.es:8080/casa/hab%02gonzalo.html?cadena=consulta");
		verify(request).setHeader("Host", "123.125.25.3:8080");
	}
	
	@Test
	public void testValidateHostWithIPv6PortDefault() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		// Host header alwasys required, with or without fied-value
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("45fd:47::25");
		
		when(request.getScheme()).thenReturn("http");
		when(request.getAuthority()).thenReturn("gms.es:8080");
		when(request.getRequestURI()).thenReturn("/casa/hab%02gonzalo.html?cadena=consulta");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://gms.es:8080/casa/hab%02gonzalo.html?cadena=consulta");
		verify(request).setHeader("Host", "45fd:47::25");
	}
	
	@Test
	public void testValidateHostWithIPv6Port8080() {
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		
		// Host header alwasys required, with or without fied-value
		when(header.getHeader_name()).thenReturn("Host");
		when(header.getHeader_value()).thenReturn("[45fd:47::25]:8080");
		
		when(request.getScheme()).thenReturn("http");
		when(request.getAuthority()).thenReturn("gms.es:8080");
		when(request.getRequestURI()).thenReturn("/casa/hab%02gonzalo.html?cadena=consulta");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.parse(header, request);
		
		verify(request).setEffectiveRequestURI("http://gms.es:8080/casa/hab%02gonzalo.html?cadena=consulta");
		verify(request).setHeader("Host", "[45fd:47::25]:8080");
	}
	
	@Test
	public void testNextParser(){
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest request = mock(HttpRequest.class);
		HeaderParserChain nextParser = mock(HeaderParserChain.class);
		when(header.getHeader_name()).thenReturn("Content-Length");
		when(header.getHeader_value()).thenReturn("5498");
		
		HostHeaderParser parser = new HostHeaderParser();
		parser.nextParseHeader(nextParser);
		parser.parse(header, request);
		
		verify(nextParser,times(1)).parse(header, request);
		
	}
}
