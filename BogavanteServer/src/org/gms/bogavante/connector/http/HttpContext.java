package org.gms.bogavante.connector.http;

import java.io.IOException;
import java.io.OutputStream;

import org.gms.bogavante.StaticResourceProcessor;
import org.gms.bogavante.connector.http.processor.Http1MessageBodyProcessor;
import org.gms.bogavante.connector.http.processor.Http1RequestProcessor;
import org.gms.bogavante.connector.http.processor.HttpRequest;
import org.gms.bogavante.connector.http.processor.HttpRequestProcessor;
import org.gms.bogavante.connector.http.processor.HttpResponse;

public class HttpContext {
	
	private String scheme;
	private HttpRequestLine requestLine = new HttpRequestLine();
	
	private static final String HTTP_VERSION_1_PATTERN = "HTTP/1\\.\\d"; 
	
	public HttpContext(String scheme){
		this.scheme = scheme;
		
	}
	
	/*
	 * Lee la primera linea de la petición y retorna el RequestProcesor adecuado
	 * para procesar la petición.
	 */
	public HttpRequestProcessor getRequestProcesor(SocketInputStream input, OutputStream output) throws IOException{
		input.readRequestLine(requestLine);
		
		String versión = requestLine.getHTTP_version();
		if(versión.matches(HTTP_VERSION_1_PATTERN)){
			Http1RequestProcessor requestProcesor = new Http1RequestProcessor(this);
			HttpRequest req = new HttpRequest(input);
			req.setScheme(scheme);
			requestProcesor.setRequest(req);
			requestProcesor.setResponset(new HttpResponse(output,req));
			requestProcesor.setInputStream(input);
			requestProcesor.setOutputStream(output);
			requestProcesor.setMessageBodyProcessor(new Http1MessageBodyProcessor());
			requestProcesor.setStaticResourceProcessor(new StaticResourceProcessor());
			return requestProcesor;
		}else{
			throw new HttpRequestParseException(505, "HTTP Version Not Supported");
		}
	}
	
	public String getScheme(){
		return scheme;
	}
	
	public HttpRequestLine getRequesLine(){
		return requestLine;
	}
}
