package org.gms.bogavante.connector.http.encoding;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

import org.gms.bogavante.connector.http.processor.HttpRequest;

public class GZipDecoderBody implements TransferDecoderBody {

	private TransferDecoderBody nextDecoder;
	
	@Override
	public void decodeBody(InputStream input, HttpRequest request) throws IOException {
		
		int contentLength = (int)request.getContentLength();
		byte[] payload = new byte[(int)(contentLength * 1.5)];
		GZIPInputStream gzis = new GZIPInputStream(input);
		int payloadSize = 0;
		int reads = 0;
		while((reads = gzis.read(payload, payloadSize, payload.length)) != -1){
			if(reads == payload.length){
				payload = Arrays.copyOf(payload, (int)(payload.length * 1.2));
			}
			payloadSize += reads;
		}

		payload = Arrays.copyOf(payload, payloadSize);
		ByteArrayInputStream newInput = new ByteArrayInputStream(payload);
		if(nextDecoder != null){
			nextDecoder.decodeBody(newInput, request);
		}else {
			request.setContentLength(payloadSize);
			request.setHeader("Content-Length", String.valueOf(payloadSize));
			request.setInputStream(newInput);
		}
	}

	@Override
	public void nextEncoding(TransferDecoderBody next) {
		this.nextDecoder = next;

	}

}
