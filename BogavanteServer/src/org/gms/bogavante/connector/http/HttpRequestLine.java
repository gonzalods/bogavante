package org.gms.bogavante.connector.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequestLine {

	public static final int MAX_LENGTH_LINE = 32768;
	public static final int INITIAL_LENGTH_LINE = 1204;
	public static final int MAX_LENGTH_URI = 32752;
	private String method;
	private String request_target;
	private String HTTP_version;
	
	/*
	 * RFC 7230 - 3.1.1 Request Line
	 * request-line = method SP request-target SP HTTP-version CRLF
	 */
	public void parseLine(char[] buf, int length){
		
		String line = new String(buf,0,length);
		/*
		 * RFC 7230 apartado 3.5: 
		 * La linea de petición puede separar los componentes con cualquier 
		 * forma de espacio-en-blanco e ignorar cualquir espacio-en-blanco 
		 * antes y despúes, este espacio-en-blanco incluyen uno o más de los 
		 * siguientes octetos (SP=' ', HTAB=\t, VT=x0B, FF=\f o CR=\r. O \s
		 * que engloba a todos).
		 */
		String[] components = line.trim().split("\\s+");
		if(components.length != 3){
			//TODO Posible redirección 301 (Moved Permanently) para URIs 
			//con espacios-en-blanco. RFC 7230 - 3.1.1-p6.
			throw new HttpRequestParseException(400, "Bad Request");
		}
		method = components[0];
		request_target = components[1];
		validateRequestTarget();
		HTTP_version = components[2];
		validateVersion();
	}

	private void validateRequestTarget(){
		if(request_target.length() > MAX_LENGTH_URI){
			throw new HttpRequestParseException(414, "URI Too Long");
		}
	}
	
	/*
	 * RFC 7230 - 2.6. Protocol Versioning
	 * 	 HTTP-version 		= HTTP-name "/" DIGIT "." DIGIT 
	 *   HTTP-name 			= %x48.54.54.50 ; "HTTP", case-sensitive
	 */
	private void validateVersion(){
		Pattern pattern = Pattern.compile("HTTP/\\d\\.\\d");
		Matcher matcher = pattern.matcher(HTTP_version);
		if(!matcher.matches()){
			throw new HttpRequestParseException(400, "Bad Request");
		}
	}
	
	public String getMethod() {
		return method;
	}

	public String getRequest_target() {
		return request_target;
	}

	public String getHTTP_version() {
		return HTTP_version;
	}

	@Override
	public String toString() {
		return "HttpRequestLine [method=" + method + ", request target=" + request_target + ", HTTP version="
				+ HTTP_version + "]";
	}
	
	
}
