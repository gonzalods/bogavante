package org.gms.bogavante.connector.http.processor;

import java.io.InputStream;
import java.io.OutputStream;

import org.gms.bogavante.connector.http.HttpRequestLine;

public interface RequestProcessor {

	public void process(InputStream input, OutputStream output);
	public void setHttpRequestLine(HttpRequestLine requestLine);
}
