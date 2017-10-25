package org.gms.bogavante.connector.http.encoding;

import java.io.IOException;
import java.io.InputStream;

import org.gms.bogavante.connector.http.processor.HttpRequest;

public interface TransferDecoderBody {

	public void decodeBody(InputStream input, HttpRequest request) throws IOException;
	public void nextEncoding(TransferDecoderBody next);
}
