package org.gms.bogavante.connector.http.parser;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;

/*
 * RFC 7230 4.3 TE 
 * The "TE" header field in a request indicates what transfer codings, 
 * besides chunked, the client is willing to accept in response, and 
 * whether or not the client is willing to accept trailer fields in a 
 * chunked transfer coding.
 * The TE field-value consists of a comma-separated list of transfer coding names,
 * each allowing for optional parameters and/or the keyword "trailers". A client 
 * MUST NOT send the chunked transfer coding name in TE; chunked is always acceptable
 * for HTTP/1.1 recipients.
 * TE 			= #t-codings
 * t-codings 	= "trailers" / ( transfer-coding [ t-ranking ] )
 * t-ranking 	= OWS ";" OWS "q=" rank
 * rank 		= ( "0" [ "." 0*3DIGIT ] ) / ( "1" [ "." 0*3("0") ] )
 * (No validation occur to set de header in the HttpRequest object. Later, when the
 * tranfer encoding has to apply to the response, it will validate.)
 * TODO limpiar lo que sobra.
 */
public class TEHeaderParser implements HeaderParserChain, TransferCodingConstants {

//	public final static String TRAILERS = "trailers";

//	private final static String RANK_PATTERN = "([ \t];[ \t]q=(0(\\.\\d{0.3})?)|(1(\\.0{0.3})?))?";
//	private final static String FIXED_TRANSFER_CODINGS_RANK_PATTERN =
//		"(trailers)|(((compress)|(deflate)|(gzip)|(x-compress)|(x-zip))" + RANK_PATTERN + ")";
//	private final static String TRANSFER_CODING_EXT_RANK_PATTERN = 
//			"(?:chunked)\\w+([ \\t]*;[ \\t]*\\w+[ \\t]*=[ \\t]*(.)+)*";

	private String headerName = "te";
	private HeaderParserChain nextParser;
	
	@Override
	public void parse(HttpHeader header, HttpRequest request) {
		String reequestHeader = header.getHeader_name();

		if(reequestHeader.equalsIgnoreCase(headerName)){
			if(request.getHeader(reequestHeader)!= null){
				//TODO deciri que lógica implemantar cuando viene la cabecera duplicada.
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
		this.nextParser=headerParser;

	}
}
