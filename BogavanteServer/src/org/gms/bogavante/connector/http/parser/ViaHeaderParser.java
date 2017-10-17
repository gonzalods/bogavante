package org.gms.bogavante.connector.http.parser;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;

/*
 * RFC 7230 5.7.1. Via
 * The "Via" header field indicates the presence of intermediate 
 * protocols and recipients between the user agent and the server (on 
 * requests) or between the origin server and the client (on responses)
 * Via 				 	= 1#( received-protocol RWS received-by [ RWS comment ] ) 
 * received-protocol 	= [ protocol-name "/" ] protocol-version 
 * received-by 			= ( uri-host [ ":" port ] ) / pseudonym
 * pseudonym 			= token
 */
public class ViaHeaderParser implements HeaderParserChain {

	private String headerName = "via";
	private HeaderParserChain nextParser;
	@Override
	public void parse(HttpHeader header, HttpRequest request) {
		String reequestHeader = header.getHeader_name();

		if(reequestHeader.equalsIgnoreCase(headerName)){
			if(request.getHeader(reequestHeader)!= null){
				//TODO decidir que lógica implemantar cuando viene la cabecera duplicada.
			}
			String[] tCodings = ValidatorAndParseHeader
					.parseCommaDelimitedList(header.getHeader_value(),false);
			
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
