package org.gms.bogavante.connector.http.encoding;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.SocketInputStream;
import org.gms.bogavante.connector.http.processor.HttpRequest;
import org.junit.Test;

import de.svenjacobs.loremipsum.LoremIpsum;

public class ChunkedDecodingBodyTest {

	@Test
	public void testDecodingSimple() throws IOException{
		String text = new LoremIpsum().getWords(75);
		String text2 = new LoremIpsum().getWords(55);
		StringBuilder body = new StringBuilder(Integer.toHexString(text.length()))
				.append("\r\n").append(text).append("\r\n")
				.append(Integer.toHexString(text2.length())).append("\r\n").append(text2)
				.append("\r\n").append(0).append("\r\n").append("\r\n");
		
		ByteArrayInputStream bis = new ByteArrayInputStream(body.toString().getBytes());
		SocketInputStream sis = new SocketInputStream(bis, 500);
		
		HttpRequest request = new HttpRequest(null);
		HttpRequest spyRequest = spy(request);
		
		when(spyRequest.getHeaderValues("Trailer")).thenReturn(null);
		
		ChunkedDecoderBody decoding = new ChunkedDecoderBody();
		decoding.decodeBody(sis, spyRequest);
		
		long contentLength = spyRequest.getContentLength(); 
		InputStream newInput = spyRequest.getInputStream();
		byte[] buff = new byte[(int)contentLength];
		newInput.read(buff);
		String payload = new String(buff);

		assertThat((int)contentLength, is(text.length() + text2.length()));
		assertThat(payload, is(text + text2));
		
	}
	
	@Test
	public void testDecodingWithExtensions() throws IOException{
		String text = new LoremIpsum().getWords(75);
		String text2 = new LoremIpsum().getWords(55);
		StringBuilder body = new StringBuilder(Integer.toHexString(text.length()))
				.append(";par1=val1;par2=val2")
				.append("\r\n").append(text).append("\r\n")
				.append(Integer.toHexString(text2.length())).append("\r\n").append(text2)
				.append("\r\n").append(0).append("\r\n").append("\r\n");
		
		ByteArrayInputStream bis = new ByteArrayInputStream(body.toString().getBytes());
		SocketInputStream sis = new SocketInputStream(bis, 500);
		
		HttpRequest request = new HttpRequest(null);
		HttpRequest spyRequest = spy(request);
		
		when(spyRequest.getHeaderValues("Trailer")).thenReturn(null);
		
		ChunkedDecoderBody decoding = new ChunkedDecoderBody();
		decoding.decodeBody(sis, spyRequest);
		
		long contentLength = spyRequest.getContentLength(); 
		InputStream newInput = spyRequest.getInputStream();
		byte[] buff = new byte[(int)contentLength];
		newInput.read(buff);
		String payload = new String(buff);

		assertThat((int)contentLength, is(text.length() + text2.length()));
		assertThat(payload, is(text + text2));
		
	}

	@Test
	public void testDecodingWithTrailer() throws IOException{
		String text = new LoremIpsum().getWords(75);
		String text2 = new LoremIpsum().getWords(55);
		StringBuilder body = new StringBuilder(Integer.toHexString(text.length()))
				.append(";par1=val1;par2=val2")
				.append("\r\n").append(text).append("\r\n")
				.append(Integer.toHexString(text2.length())).append("\r\n").append(text2)
				.append("\r\n").append(0).append("\r\n")
				.append("cabecera1: valor11;valor12").append("\r\n")
				.append("cabecera2: valor21").append("\r\n")
				.append("\r\n");
		
		
		ByteArrayInputStream bis = new ByteArrayInputStream(body.toString().getBytes());
		SocketInputStream sis = new SocketInputStream(bis, 500);
		
		HttpRequest request = new HttpRequest(null);
		HttpRequest spyRequest = spy(request);
		
		TrailerHeadersValidatorAndParser trailerParser = mock(TrailerHeadersValidatorAndParser.class);
		
		when(spyRequest.getHeaderValues("Trailer")).thenReturn(Arrays.asList("cabecera1","cabecera2"));
		when(trailerParser.isForbidden(anyString())).thenReturn(false);
		
		ChunkedDecoderBody decoding = new ChunkedDecoderBody();
		decoding.setTrailerHeadersParser(trailerParser);
		decoding.decodeBody(sis, spyRequest);
		
		long contentLength = spyRequest.getContentLength(); 
		InputStream newInput = spyRequest.getInputStream();
		byte[] buff = new byte[(int)contentLength];
		newInput.read(buff);
		String payload = new String(buff);
		
		assertThat((int)contentLength, is(text.length() + text2.length()));
		assertThat(payload, is(text + text2));
	}
	
	@Test
	public void testForbiddenHeaderTrailer() throws IOException{
		String text = new LoremIpsum().getWords(75);
		String text2 = new LoremIpsum().getWords(55);
		StringBuilder body = new StringBuilder(Integer.toHexString(text.length()))
				.append(";par1=val1;par2=val2")
				.append("\r\n").append(text).append("\r\n")
				.append(Integer.toHexString(text2.length())).append("\r\n").append(text2)
				.append("\r\n").append(0).append("\r\n")
				.append("cabecera1: valor11;valor12").append("\r\n")
				.append("cabecera2: valor21").append("\r\n")
				.append("\r\n");
		
		
		ByteArrayInputStream bis = new ByteArrayInputStream(body.toString().getBytes());
		SocketInputStream sis = new SocketInputStream(bis, 500);
		
		HttpRequest request = mock(HttpRequest.class);
		TrailerHeadersValidatorAndParser trailerParser = mock(TrailerHeadersValidatorAndParser.class);
		
		when(request.getHeaderValues("Trailer")).thenReturn(Arrays.asList("cabecera1","cabecera2"));
		when(trailerParser.isForbidden(anyString())).thenReturn(false,true);
		
		ChunkedDecoderBody decoding = new ChunkedDecoderBody();
		decoding.setTrailerHeadersParser(trailerParser);
		int code = 200;
		try{
			decoding.decodeBody(sis, request);
		}catch(HttpRequestParseException e){
			code = e.getCodeError();
		}
		
		assertThat(code,is(400));
		verify(trailerParser,times(1)).validateAndParse(any(), any());
	}
}
