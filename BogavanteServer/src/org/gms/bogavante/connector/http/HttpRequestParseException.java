package org.gms.bogavante.connector.http;

public class HttpRequestParseException extends RuntimeException {

	private static final long serialVersionUID = -265194532483881849L;
	private int codeError;
	
	public HttpRequestParseException(int codeError, String message){
		super(message);
		this.codeError = codeError; 
	}
	
	public int getCodeError(){
		return codeError;
	}
}
