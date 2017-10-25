package org.gms.bogavante.connector.http.encoding;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPOutputStream;

import org.gms.bogavante.connector.http.processor.HttpRequest;
import org.junit.Test;

import de.svenjacobs.loremipsum.LoremIpsum;

public class GZipDecoderBodyTest {

	@Test
	public void testGZipDecode() throws IOException{
		String text = new LoremIpsum().getWords(130);

		ByteArrayInputStream bais = new ByteArrayInputStream(text.getBytes());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);
		byte[] body = new byte[130];
		int len = 0;
		while((len = bais.read(body)) != -1) {
			gzos.write(body,0,len);
		}
		
		gzos.close();
		baos.close();
		bais.close();
		
		body = baos.toByteArray();
	
		bais = new ByteArrayInputStream(body);
		
		HttpRequest spyRequest = spy(new HttpRequest(null));
		
		when(spyRequest.getContentLength()).thenReturn(209L)
			.thenCallRealMethod();

		GZipDecoderBody decoder = new GZipDecoderBody();
		decoder.decodeBody(bais, spyRequest);

		long contentLength = spyRequest.getContentLength(); 
		InputStream newInput = spyRequest.getInputStream();
		byte[] buff = new byte[(int)contentLength];
		newInput.read(buff);
		String payload = new String(buff);

		assertThat((int)contentLength, is(text.length()));
		assertThat(payload, is(text));
		
		bais.close();
		
	}

}
