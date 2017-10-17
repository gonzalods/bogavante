package org.gms.bogavante.connector.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SocketInputStream extends BufferedInputStream{

	private static final byte CR = (byte)'\r';
	private static final byte LF = (byte)'\n';
	
	
	public SocketInputStream(InputStream input, int size){
		super(input, size);
	}

	/*
	 * RFC 7230 apartado 3.1.1 Request Line
	 * request-line = method SP request-target SP HTTP-version CRLF
	 */
	public void readRequestLine(HttpRequestLine requestLine)throws IOException{
		
		int ch = 0;
		/* 
		 * RFC 7230 apartado 3.5 Message Parsing Robustness
		 * Se debe ignorar por lo menos una linea vacía (CLFR) recibida antes de la 
		 * línea de petición.
		 */
		do{
			try {
				ch = read();
			} catch (IOException e) {
				ch = -1;
			}
		}while(ch == CR || ch == LF);

		if(ch == -1)
			throw new HttpRequestParseException(400, "Bad Request");
		
		int count = 0;
		char[] lineRequest = new char[HttpRequestLine.INITIAL_LENGTH_LINE];
		lineRequest[count++] = (char)ch;
		while(true){
			if(count >= lineRequest.length){
				if((lineRequest.length * 2) <= HttpRequestLine.MAX_LENGTH_LINE){
					char[] newBuf = new char[lineRequest.length * 2];
					System.arraycopy(lineRequest, 0, newBuf, 0, lineRequest.length);
					lineRequest = newBuf;
				}else
					throw new IOException("Linea petición demasiado larga");
			}
			ch = read();
			/*
			 * RFC 7230 apartado 3.5. Message Parsing Robustness
			 * Se puede reconocer un único LF como terminación de línea e ignorar los 
			 * CR precedentes.
			 */
			if(ch == CR){}
			else if(ch == LF) {break;}
			else if(ch == -1){throw new HttpRequestParseException(400, "Bad Request");}
			else{lineRequest[count++] = (char)ch;}
		}
		requestLine.parseLine(lineRequest, count);
	}
	
	/*
	 * RFC 7230 apartado 3.2 Header Fields
	 * *( header-field CRLF )
	 * OWS (Obtional white space)
	 */
	public void readHeader(HttpHeader httpHeader) throws IOException{
		
		char[] lineHeadert = new char[HttpHeader.INITIAL_LENGTH_LINE];
		int count = 0;
		while(true){
			if(count >= lineHeadert.length){
				if((lineHeadert.length * 2) <= HttpHeader.MAX_LENGTH_LINE){
					char[] newBuf = new char[lineHeadert.length * 2];
					System.arraycopy(lineHeadert, 0, newBuf, 0, lineHeadert.length);
					lineHeadert = newBuf;
				}else
					throw new IOException("Linea petición demasiado larga");
			}
			
			int ch = read();
			/*
			 * RFC 7230 apartado 3.5. Message Parsing Robustness
			 * Se puede reconocer un único LF como terminación de línea e ignorar los 
			 * CR precedentes.
			 */
			if(ch == CR){}
			else if(ch == LF) {
				if(!is_obs_fold()) break;
				else {
					//It's a obs-fold. It replaces the received obs-fold with one SP
					//and leaves to interpretering later.
					httpHeader.setObs_fold(true);
					lineHeadert[count++] = ' ';
				}
			}else if(ch == -1) throw new HttpRequestParseException(400, "Bad Request"); 
			else{lineHeadert[count++] = (char)ch;}
		}
		if(count != 0){
			httpHeader.parseLine(lineHeadert, count);
		}
		
	}
	
	/*
	 * RFC 7230 apartado 3.2.4-p4,5 Field Parsing
	 * obs-fold = CRLF 1*( SP / HTAB )
	 */
	private boolean is_obs_fold() throws IOException{
		mark(1);
		int ch = read();
		char temp = (char)ch;
		if(temp == ' ' | temp == '\t'){
			return true;
		}else{
			reset();
			return false;
		}
	}
}
