package org.gms.bogavante.connector.http.processor;

import java.io.OutputStream;

public class HttpResponse {

	private OutputStream output;
	private HttpRequest request;
	
	public HttpResponse(OutputStream output, HttpRequest request) {
		super();
		this.output = output;
		this.request = request;
	}
	
}
