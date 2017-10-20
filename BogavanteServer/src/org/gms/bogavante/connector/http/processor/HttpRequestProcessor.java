package org.gms.bogavante.connector.http.processor;

import java.io.IOException;
import java.io.OutputStream;

import org.gms.bogavante.connector.http.SocketInputStream;

public interface HttpRequestProcessor {

	public void process() throws IOException;
	public void setInputStream(SocketInputStream input);
	public void setOutputStream(OutputStream output);
	
}
