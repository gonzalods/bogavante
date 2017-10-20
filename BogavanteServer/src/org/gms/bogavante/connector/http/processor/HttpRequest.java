package org.gms.bogavante.connector.http.processor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HttpRequest {

	private InputStream input;
	protected HashMap<HeaderName, List<String>> headers = new HashMap<>();
	protected List<String> cookies = new ArrayList<>();
	private String scheme;
	private String method;
	private String uri;
	private String queryString;
	private long contentLength;
	private int localPort;
	private String serverName;

	private String effectiveRequestURI;
	private String authority;
	public String getScheme() {
		return scheme;
	}
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public long getContentLength() {
		return contentLength;
	}
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	public String getEffectiveRequestURI() {
		return effectiveRequestURI;
	}
	public void setEffectiveRequestURI(String effectiveRequestURI) {
		this.effectiveRequestURI = effectiveRequestURI;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public void setHeader(String name, String ... values){
		HeaderName header_name = new HeaderName(name);
		headers.put(header_name, Arrays.asList(values));
	}
	public List<String> getHeaderValues(String name){
		HeaderName header_name = new HeaderName(name);
		return headers.get(header_name);
	}
	public int getLocalPort() {
		return localPort;
	}
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/* 
	 * Class repersenting a header name case insensitive.
	 * It's used as key of the Map containing de request's headers.
	 */
	private class HeaderName{
		private String name;

		HeaderName(String name){
			this.name = name;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof HeaderName){
				HeaderName other = (HeaderName)obj;
				return this.name.toLowerCase().equals(other.name.toLowerCase());
			}else 
				return false;
		}

		@Override
		public int hashCode() {
			return name.toLowerCase().hashCode();
		}
		
	}
}
