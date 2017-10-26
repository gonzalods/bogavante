package org.gms.bogavante.connector.http.parser;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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
		
		
		
		fail("Not yet implemented");
	}

}
