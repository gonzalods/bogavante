package org.gms.bogavante.connector.http.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.gms.bogavante.connector.http.processor.HttpRequest;

public class GZipDecoderBody implements TransferDecoderBody {

	private TransferDecoderBody nextDecoder;
	
	@Override
	public void decodeBody(InputStream input, HttpRequest request) throws IOException {
		
		int contentLength = (int)request.getContentLength();
		byte[] payload = new byte[(int)(contentLength * 2.5)];
		GZIPInputStream gzis = new GZIPInputStream(input);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int payloadSize = 0;
		int reads = 0;
		while((reads = gzis.read(payload)) != -1){
			baos.write(payload, 0, reads);
		}

		payload = baos.toByteArray();
		baos.close();
		gzis.close();
		
		ByteArrayInputStream newInput = new ByteArrayInputStream(payload);
		if(nextDecoder != null){
			nextDecoder.decodeBody(newInput, request);
		}else {
			request.setContentLength(payload.length);
			request.setHeader("Content-Length", String.valueOf(payloadSize));
			request.setInputStream(newInput);
		}
	}

	@Override
	public void nextEncoding(TransferDecoderBody next) {
		this.nextDecoder = next;

	}

}
