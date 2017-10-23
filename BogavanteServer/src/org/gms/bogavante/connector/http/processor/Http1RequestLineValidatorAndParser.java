package org.gms.bogavante.connector.http.processor;

import java.net.URI;
import java.net.URISyntaxException;

import org.gms.bogavante.connector.http.HttpRequestLine;
import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.method.RequestMethod;

public class Http1RequestLineValidatorAndParser {

	
	public void validateAndParse(HttpRequest request, HttpRequestLine requestLine){
		String method = requestLine.getMethod();
		validateMethod(method);
		request.setMethod(method);
		
		String requestTarger = requestLine.getRequest_target();
		URI uri = null;
		try{
			uri = new URI(requestTarger);
		}catch(URISyntaxException e){
			throw new HttpRequestParseException(400, "Bad Request");
		}
		validateUri(uri);
		request.setUri(requestTarger);
		String authority = uri.getRawAuthority();
		if(authority != null){
			request.setAuthority(authority);
		}
		String query = uri.getRawQuery();
		if(query != null){
			request.setQueryString(query);
		}
	}
	
	private void validateUri(URI uri){
		/*
		 * RFC 7230 - 2.7.1. http URI Scheme p3 
		 * A recipient that processes an "http" URI with an empty host identifier 
		 * reference MUST reject it as invalid.
		 */
		if(uri.getScheme()!=null && uri.getHost()==null){
			throw new HttpRequestParseException(400, "Bad Request");
		}
		/*
		 * RFC 7230 - 2.7.1. http URI Scheme p9
		 * Before making use of an "http" URI reference received from an untrusted 
		 * source, a recipient SHOULD parse for userinfo and treat its presence 
		 * as an error
		 */
		if(uri.getRawAuthority()!=null && uri.getUserInfo()!=null){
			throw new HttpRequestParseException(400, "Bad Request");
		}
	}
	
	private void validateMethod(String method){
		try{
			RequestMethod.valueOf(method.toUpperCase());
		}catch(IllegalArgumentException e){
			throw new HttpRequestParseException(400, "Bad Request");
		}
		
	}
}
