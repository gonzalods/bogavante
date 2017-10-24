package org.gms.bogavante.connector.http.encoding;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.gms.bogavante.connector.http.SocketInputStream;
import org.gms.bogavante.connector.http.processor.HttpRequest;
import org.junit.Test;

import de.svenjacobs.loremipsum.LoremIpsum;

public class ChunkedDecodingBodyTest {

	@Test
	public void testDecodingSimple() throws IOException{
		String text = new LoremIpsum().getWords(75);
		String text2 = new LoremIpsum().getWords(55);
		StringBuilder payload = new StringBuilder(Integer.toHexString(text.length()))
				.append("\r\n").append(text).append("\r\n")
				.append(Integer.toHexString(text2.length())).append("\r\n").append(text2)
				.append("\r\n").append(0).append("\r\n").append("\r\n");
		
		ByteArrayInputStream bis = new ByteArrayInputStream(payload.toString().getBytes());
		SocketInputStream sis = new SocketInputStream(bis, 500);
		
		HttpRequest request = mock(HttpRequest.class);
		
		when(request.getHeaderValues("Trailer")).thenReturn(null);
		
		ChunkedDecodingBody decoding = new ChunkedDecodingBody();
		decoding.decodeBody(sis, request);
		
		verify(request).setContentLength(text.length() + text2.length());
		
	}
	
	@Test
	public void testDecodingWithExtensions() throws IOException{
		String text = new LoremIpsum().getWords(75);
		String text2 = new LoremIpsum().getWords(55);
		StringBuilder payload = new StringBuilder(Integer.toHexString(text.length()))
				.append(";par1=val1;par2=val2")
				.append("\r\n").append(text).append("\r\n")
				.append(Integer.toHexString(text2.length())).append("\r\n").append(text2)
				.append("\r\n").append(0).append("\r\n").append("\r\n");
		
		ByteArrayInputStream bis = new ByteArrayInputStream(payload.toString().getBytes());
		SocketInputStream sis = new SocketInputStream(bis, 500);
		
		HttpRequest request = mock(HttpRequest.class);
		
		when(request.getHeaderValues("Trailer")).thenReturn(null);
		
		ChunkedDecodingBody decoding = new ChunkedDecodingBody();
		decoding.decodeBody(sis, request);
		
		verify(request).setContentLength(text.length() + text2.length());
		
	}

}
