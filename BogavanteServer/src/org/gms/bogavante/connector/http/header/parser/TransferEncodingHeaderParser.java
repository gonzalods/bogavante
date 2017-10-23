package org.gms.bogavante.connector.http.header.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.processor.HttpRequest;

/*
 * RFC 7230 3.3.1. Transfer-Encoding 
 * Transfer-Encoding = 1#transfer-coding
 */
public class TransferEncodingHeaderParser implements HeaderParserChain, TransferCodingConstants{

	public final static Set<String> supportedCodings;
	private final static String FIXE_TRANSFER_CODINGS_PATTERN ="(chunked)|(compress)|(deflate)|(gzip)|(x-compress)|(x-zip)";
	/*
	 * RFC 7230 4. Transfer Codings
	 * transfer-extension 	= token *( OWS ";" OWS transfer-parameter )
	 * transfer-parameter 	= token BWS "=" BWS ( token / quoted-string )
	 */
	private final static String TRANSFER_CODING_EXT_PATTERN = "\\w+([ \\t]*;[ \\t]*\\w+[ \\t]*=[ \\t]*(\\w+|\"(.)*\"))*";

	static{
		Set<String> aux = new HashSet<>();
		
		/*
		 * RFC 7230 - 3.3.1. p-3
		 * A recipient MUST be able to parse the chunked transfer coding 
		 * because it plays a crucial role in framing messages when the 
		 * payload body size is not known in advance.
		 */
		aux.add(CHUNKED);
		aux.add(DEFLATE);
		aux.add("7z");//for test
		supportedCodings = Collections.unmodifiableSet(aux);
		
	}
	
	private HeaderParserChain nextParser;
	private String headerName = "transfer-encoding";	

	@Override
	public void parse(HttpHeader header, HttpRequest request) {
		
		if(header.getHeader_name().equalsIgnoreCase(headerName)){
			String[] codings = Http1ValidatorAndParseHeader
					.parseCommaDelimitedList(header.getHeader_value(),true);
			validateListCodign(codings);
			
			request.setHeader(header.getHeader_name(), codings);
		}else{
			nextParser.parse(header, request);
		}

	}

	@Override
	public void nextParseHeader(HeaderParserChain headerParser) {
		this.nextParser=headerParser;
	}
	
	/*
	 * RFC 7230 - 3.3.1. p-1
	 * The Transfer-Encoding header field lists the transfer coding names 
	 * corresponding to the sequence of transfer codings that have been (or 
	 * will be) applied to the payload body in order to form the message body.
	 */
	private void validateListCodign(String[] codings){
		
		boolean chunkedPresent = false;
		Pattern fixedCondings_pattern = Pattern.compile(FIXE_TRANSFER_CODINGS_PATTERN);
		Pattern transfer_ext_pattern = Pattern.compile(TRANSFER_CODING_EXT_PATTERN);

		for (String coding : codings) {

			Matcher matcher = fixedCondings_pattern.matcher(coding.trim());
			boolean isFixeCoding = matcher.matches(); 
			if(!isFixeCoding && matcher.find(0)){
				// Coding contains name of a fixe coding but have more information.
				//(reject the request)
				System.out.println("isFixeCoding:" + isFixeCoding);
				System.out.println(coding + ":" + Arrays.toString(codings));
				throw new HttpRequestParseException(400, "Bad Request");
			}

			String onlyCoding = coding;
			if(!isFixeCoding){
				System.out.println("transfer-extension:" + coding);
				Matcher matcher2 = transfer_ext_pattern.matcher(coding.trim());
				if(!matcher2.matches()){
					// Coding don`t meet sintax.
					//(reject the request)
					throw new HttpRequestParseException(400, "Bad Request");
				}
				String[] components = coding.split(";");
				onlyCoding = components[0].trim();
			}
			/*
			 * RFC 7230 - 3.3.1. p-10
			 * A server that receives a request message with a transfer coding it
			 * does not understand SHOULD respond with 501 (Not Implemented).
			 */
			if(!supportedCodings.contains(onlyCoding.toLowerCase())){
				throw new HttpRequestParseException(501, "Not Implemented");
			}
			if(coding.trim().equals(CHUNKED))
				chunkedPresent = true;
		}
		/*
		 * RFC 7230 p-3
		 * If any transfer coding other than chunked is applied to a request 
		 * payload body, the sender MUST apply chunked as the final transfer 
		 * coding. (chuncked MUST BE the last coding in the list.)
		 */
		if(chunkedPresent && !codings[codings.length-1].equals(CHUNKED)){
			throw new HttpRequestParseException(400, "Bad Request");
		}
	}

}
