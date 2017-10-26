package org.gms.bogavante.connector.http.parser;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.header.parser.TEHeaderParser;
import org.gms.bogavante.connector.http.processor.HttpRequest;
import org.junit.Test;

public class TEHeaderParserTest {

	@Test
	public void testSingleTE() {
		
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest spyRequest = spy(new HttpRequest(null));
		
		when(header.getHeader_name()).thenReturn("TE");
		when(header.getHeader_value()).thenReturn("chunked");
		when(spyRequest.getHeaderValues("TE")).thenReturn(null).thenCallRealMethod();
		
		TEHeaderParser parser = new TEHeaderParser();
		parser.parse(header, spyRequest);
		
		assertThat(spyRequest.getHeaderValues("TE"), hasItem("chunked"));
		
	}

	@Test
	public void testSingleTEWithParams() {
		
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest spyRequest = spy(new HttpRequest(null));
		
		when(header.getHeader_name()).thenReturn("TE");
		when(header.getHeader_value()).thenReturn("chunked; par1=value1");
		when(spyRequest.getHeaderValues("TE")).thenReturn(null).thenCallRealMethod();
		
		TEHeaderParser parser = new TEHeaderParser();
		parser.parse(header, spyRequest);
		
		assertThat(spyRequest.getHeaderValues("TE"), hasItem("chunked; par1=value1"));
		
	}
	
	@Test
	public void testSingleTEWithRank() {
		
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest spyRequest = spy(new HttpRequest(null));
		
		when(header.getHeader_name()).thenReturn("TE");
		when(header.getHeader_value()).thenReturn("chunked;q=0.5");
		when(spyRequest.getHeaderValues("TE")).thenReturn(null).thenCallRealMethod();
		
		TEHeaderParser parser = new TEHeaderParser();
		parser.parse(header, spyRequest);
		
		assertThat(spyRequest.getHeaderValues("TE"), hasItem("chunked;q=0.5"));
		
	}
	
	@Test
	public void testListTE() {
		
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest spyRequest = spy(new HttpRequest(null));
		
		when(header.getHeader_name()).thenReturn("TE");
		when(header.getHeader_value()).thenReturn("gzip, chunked");
		when(spyRequest.getHeaderValues("TE")).thenReturn(null).thenCallRealMethod();
		
		TEHeaderParser parser = new TEHeaderParser();
		parser.parse(header, spyRequest);
		
		
		assertThat(spyRequest.getHeaderValues("TE"), equalTo(Arrays.asList("gzip","chunked")));
		
	}
	
	@Test
	public void testListTEWithParam() {
		
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest spyRequest = spy(new HttpRequest(null));
		
		when(header.getHeader_name()).thenReturn("TE");
		when(header.getHeader_value()).thenReturn("gzip; par1=val1, chunked");
		when(spyRequest.getHeaderValues("TE")).thenReturn(null).thenCallRealMethod();
		
		TEHeaderParser parser = new TEHeaderParser();
		parser.parse(header, spyRequest);
		
		
		assertThat(spyRequest.getHeaderValues("TE"), equalTo(Arrays.asList("gzip; par1=val1","chunked")));
		
	}
	
	@Test
	public void testListTEWithParamAndRank() {
		
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest spyRequest = spy(new HttpRequest(null));
		
		when(header.getHeader_name()).thenReturn("TE");
		when(header.getHeader_value()).thenReturn("gzip; par1=val1, chunked;q=0.8");
		when(spyRequest.getHeaderValues("TE")).thenReturn(null).thenCallRealMethod();
		
		TEHeaderParser parser = new TEHeaderParser();
		parser.parse(header, spyRequest);
		
		
		assertThat(spyRequest.getHeaderValues("TE"), equalTo(Arrays.asList("gzip; par1=val1","chunked;q=0.8")));
		
	}
	
	@Test
	public void testMergeTE() {
		
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest spyRequest = spy(new HttpRequest(null));
		
		when(header.getHeader_name()).thenReturn("TE");
		when(header.getHeader_value()).thenReturn("gzip; par1=val1, chunked;q=0.8");
		when(spyRequest.getHeaderValues("TE")).thenReturn(Arrays.asList("gzip")).thenCallRealMethod();
		
		TEHeaderParser parser = new TEHeaderParser();
		parser.parse(header, spyRequest);
		
		
		assertThat(spyRequest.getHeaderValues("TE"), equalTo(Arrays.asList("gzip; par1=val1","chunked;q=0.8")));
		
	}
	
	@Test
	public void testMerge2TE() {
		
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest spyRequest = spy(new HttpRequest(null));
		
		when(header.getHeader_name()).thenReturn("TE");
		when(header.getHeader_value()).thenReturn("gzip; par1=val1;q=0.5, chunked;q=0.8");
		when(spyRequest.getHeaderValues("TE")).thenReturn(Arrays.asList("gzip")).thenCallRealMethod();
		
		TEHeaderParser parser = new TEHeaderParser();
		parser.parse(header, spyRequest);
		
		
		assertThat(spyRequest.getHeaderValues("TE"), equalTo(Arrays.asList("gzip","chunked;q=0.8")));
		
	}
	
	@Test
	public void testMerge3TE() {
		
		HttpHeader header = mock(HttpHeader.class);
		HttpRequest spyRequest = spy(new HttpRequest(null));
		
		when(header.getHeader_name()).thenReturn("TE");
		when(header.getHeader_value()).thenReturn("gzip; par1=val1;q=0.5, chunked;q=0.8");
		when(spyRequest.getHeaderValues("TE")).thenReturn(Arrays.asList("gzip;p=0.8")).thenCallRealMethod();
		
		TEHeaderParser parser = new TEHeaderParser();
		parser.parse(header, spyRequest);
		
		
		assertThat(spyRequest.getHeaderValues("TE"), equalTo(Arrays.asList("gzip;p=0.8","chunked;q=0.8")));
		
	}
}
