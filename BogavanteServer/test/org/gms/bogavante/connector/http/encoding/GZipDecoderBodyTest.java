package org.gms.bogavante.connector.http.encoding;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;

import de.svenjacobs.loremipsum.LoremIpsum;

public class GZipDecoderBodyTest {

	@Test
	public void test() throws IOException{
		String text = new LoremIpsum().getWords(130);
		System.out.println(text);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(baos);
		gzos.write(text.getBytes()); 
		
		byte[] body = baos.toByteArray();

		String compressed = new String(body);
		
		System.out.println(compressed);
		System.out.println(body.length);
		
		ByteArrayInputStream bais = new ByteArrayInputStream(body);
		GZIPInputStream gzis = new GZIPInputStream(bais);
		
		body = new byte[130];
		gzis.read(body,0,body.length);
		
		compressed = new String(body);
		System.out.println(compressed);
		System.out.println(body.length);
		
		
	}

}
