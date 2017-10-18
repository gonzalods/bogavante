package org.gms.bogavante.connector.http.header.parser;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;

/*
 * RFC 7230 6.1. Connection
 * The "Connection" header field allows the sender to indicate desired control 
 * options for the current connection
 * 	Connection 			= 1#connection-option 
 * 	connection-option 	= token
 * Connection options are case-insensitive.
 */
public class ConnectionHeaderParser implements HeaderParserChain {

	private String headerName = "connection";
	private HeaderParserChain nextParser;

	
	@Override
	public void parse(HttpHeader header, HttpRequest request) {
		String reequestHeader = header.getHeader_name();

		if(reequestHeader.equalsIgnoreCase(headerName)){
			if(request.getHeader(reequestHeader)!= null){
				//TODO decidir que lógica implemantar cuando viene la cabecera duplicada.
			}
			String[] tCodings = ValidatorAndParseHeader
					.parseCommaDelimitedList(header.getHeader_value(),true);
			
			request.setHeader(header.getHeader_name(), tCodings);
		}else{
			nextParser.parse(header, request);
		}

	}

	@Override
	public void nextParseHeader(HeaderParserChain headerParser) {
		this.nextParser = headerParser;

	}

}
