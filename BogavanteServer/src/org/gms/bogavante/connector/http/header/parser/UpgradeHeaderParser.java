package org.gms.bogavante.connector.http.header.parser;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;

/*
 * RFC 7230 6.7. Upgrade
 * The "Upgrade" header field is intended to provide a simple mechanism for 
 * transitioning from HTTP/1.1 to some other protocol on the same connection.
 * 	 Upgrade 			= 1#protocol
 *   protocol 			= protocol-name ["/" protocol-version]
 *   protocol-name 		= token
 *   protocol-version 	= token
 */
public class UpgradeHeaderParser implements HeaderParserChain {

	private String headerName = "upgrade";
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
