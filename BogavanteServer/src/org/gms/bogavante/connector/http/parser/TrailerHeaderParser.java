package org.gms.bogavante.connector.http.parser;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;

/*
 * RFC 7230 4.4. Trailer
 * This header allows the sender to indicate which fields will be present in the
 * trailers when a message includes a message body encoded with the chunked transfer 
 * coding. This allows the recipient to prepare for receipt of that metadata before
 * it starts processing the body.
 * Trailer = 1#field-name
 * (No validation occur to set de header in the HttpRequest object. The value is set
 * as is in the HttpRequest object.)
 */
public class TrailerHeaderParser implements HeaderParserChain {

	private String headerName = "trailer";
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
