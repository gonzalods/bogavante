package org.gms.bogavante.connector.http.header.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;
import org.gms.bogavante.connector.http.HttpRequestParseException;

public class HostHeaderParser implements HeaderParserChain {

	private HeaderParserChain nextParser;
	private Set<String> headersApply;
	{
		headersApply = new HashSet<>(1);
		headersApply.add("host");
	}
	@Override
	public void parse(HttpHeader header, HttpRequest request) {
		if(headersApply.contains(header.getHeader_name().toLowerCase())){
			
			// TODO Caberera Host duplicada
			if(request.getHeader(header.getHeader_name()) != null){
				throw new HttpRequestParseException(400, "Bad Request");
			}
			String hostHeaderValue = header.getHeader_value();
			if(!validateHostHeaderValue(hostHeaderValue)){
				throw new HttpRequestParseException(400, "Bad Request");
			}
			String rawUri = request.getRequestURI();
			/*
			 * RFC 7230 apartado 5.5. Effective Request URI
			 * Un servidor debe recostruir la URI de la petición recibida como una
			 * URI de petición efectiva.
			 */
			try {
				String strEffectiveRequestURI = "";
				URI uriRequestTarget = new URI(rawUri);
				if(uriRequestTarget.isAbsolute() && !uriRequestTarget.isOpaque()){
					strEffectiveRequestURI = uriRequestTarget.toString();
				}else{
					String scheme = request.getScheme();
					String authority = getAuthority(uriRequestTarget,request,hostHeaderValue);
					String path = getPath(uriRequestTarget, request);
					String query = getQuery(uriRequestTarget, request);
					/*
					 * Se podria utilizar el constructor de URI con (schema,authority,path,query,fragment)
					 * pero este constructor decodifica las la URI generada.
					 */
					StringBuilder sb = new StringBuilder(scheme).append("://")
							.append(authority).append(path).append(query);
					strEffectiveRequestURI = new URI(sb.toString()).toString();
				}
				request.setEffectiveRequestURI(strEffectiveRequestURI);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			request.setHeader(header.getHeader_name(), header.getHeader_value());
			
		}else{
			if(nextParser != null){
				nextParser.parse(header, request);
			}
		}

	}

	@Override
	public void nextParseHeader(HeaderParserChain headerParser) {
		this.nextParser = headerParser;

	}
	
	/*
	 * Authority is composed by userinfo@host:port (excluding any 
	 * userinfo and its "@" delimiter)
	 * RFC 7230 
	 */
	private String getAuthority(URI uri, HttpRequest request, String hostHeaderValue){
		String authority = null;
		// Fixed URI authority provided by the server's configuration
		if(request.getAuthority() != null){
			authority = request.getAuthority();
		}else if(uri.isOpaque()){// The request-target is in authority-form 
			//Una URI de la forma 'algo.otro:8080' se considera absoluta y opaca,
			//Esta es la forma que puede tener un URI en forma de authority con
			//puerto. La clase URI considera que 'algo.otro' es el esquema y '8080'
			//es la parte específica del esquema (no la autoridad).
			authority = uri.toString();
		}else if(!uri.getRawSchemeSpecificPart().startsWith("/") && // The request-target is in authority-form  
				!uri.getRawSchemeSpecificPart().equals("*")){ // and is not in asterisk-form
			// Una URI de la forma www.gms.es es una URI en forma authority, pero la
			// clase URI no la considera absoluta y por tanto tampoco opaca. 
			authority = uri.toString();
		}else if(!hostHeaderValue.isEmpty()){// Host header provides a non-empty field-value
			authority = hostHeaderValue;
		}else{// Default name configured for the server
			StringBuilder sbAuth = new StringBuilder(request.getServerName());
			String scheme = request.getScheme();
			int port = request.getLocalPort();
			if((scheme.equals("http") && port != 80) || 
				(scheme.equals("https") && port != 443)){
				sbAuth.append(":").append(port);
			}
			authority = sbAuth.toString();	
		}
		return authority;
	}

	private String getPath(URI uri, HttpRequest request){
		// Las URI de forma autoridad tiene un path que no empieza por '/'.
		String path = "";
		if(uri.getRawPath() != null &&uri.getRawPath().startsWith("/")){
			path = uri.getRawPath();
		}
		return path;
	}
	
	private String getQuery(URI uri, HttpRequest request){
		StringBuffer sb = new StringBuffer();
		if(uri.getRawQuery()!=null){
			sb.append("?").append(uri.getRawQuery());
		}
		return sb.toString();
	}
	
	/*
	 * TODO Explicación
	 */
	private boolean validateHostHeaderValue(String hostHeaderValue){
		if(!hostHeaderValue.isEmpty()){
			String[] comps = hostHeaderValue.split(":");
			String host = hostHeaderValue;
			int port = -1;
			if(comps.length == 2){
				host = comps[0];
				port = Integer.parseInt(comps[1]);
			}
			Pattern pattern = Pattern.compile("[/?#]");
			Matcher matcher = pattern.matcher(host);
			if(matcher.find()){
				return false;
			}
			try{
				new URI(null,null,host,port,null,null,null); 
			}catch(URISyntaxException | NumberFormatException e){
				return false;
			}
		}
		return true;
	}
}
