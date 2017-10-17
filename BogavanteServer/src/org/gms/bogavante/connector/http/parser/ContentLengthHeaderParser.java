package org.gms.bogavante.connector.http.parser;

import java.math.BigInteger;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;
import org.gms.bogavante.connector.http.HttpRequestParseException;

public class ContentLengthHeaderParser implements HeaderParserChain {

	private String headerName = "content-length";
	private HeaderParserChain nextParser;
	
	/*
	 * RFC 7230 3.3.2. Content-Length 
	 * Content-Length 	= 1*DIGIT
	 */
	@Override
	public void parse(HttpHeader header, HttpRequest request) {
		
		if(header.getHeader_name().equalsIgnoreCase(headerName)){

			String valueHeader = reduceListValues(header.getHeader_value());
			long contentLength = castStringValueToLong(valueHeader);
			
			/*
			 * p-10
			 * A message can have multiple Content-Length header fields with field-values 
			 * consisting of the same decimal value, then the recipient MUST either reject 
			 * the message as invalid (not here) or replace the duplicated field-values with 
			 * a single valid Content-Length field containing that decimal value. 
			 * (If they have different values reject de message as invalid. In object Request
			 * insert the value of the header of the request).
			 */
			String existingValue = request.getHeader(header.getHeader_name());
			if(existingValue == null){
				request.setHeader(header.getHeader_name(), valueHeader);
				request.setContentLength(contentLength);
			} else {
				if(!existingValue.equals(String.valueOf(contentLength))){
					throw new HttpRequestParseException(400, "Bad Request");
				}
			}
			
		}else{
			nextParser.parse(header, request);
		}
	}

	@Override
	public void nextParseHeader(HeaderParserChain headerParser) {
		this.nextParser = headerParser;

	}
	/*
	 * p-10
	 * A message can have a single Content-Length header field with a 
	 * field value containing a list of identical decimal values (e.g., 
	 * "Content-Length: 42, 42"), then the recipient MUST either reject 
	 * the message as invalid (not here) or replace the duplicated 
	 * field-values with a single valid Content-Length field containing 
	 * that decimal value.
	 * (If they have different values reject de message as invalid).
	 */
	private String reduceListValues(String header_value){
		String[] values = header_value.split(",");
		if(values.length == 0){
			throw new HttpRequestParseException(400, "Bad Request");
		}
		String value = values[0];

		for(int i =1;i < values.length;i++){
			if(!value.equals(values[i])){
				throw new HttpRequestParseException(400, "Bad Request");
			}
		}
		return value;
	}
	/*
	 * p9 - 
	 * Any Content-Length field value greater than or equal to zero is valid
	 * (negative invalid). 
	 * A recipient MUST anticipate potentially large decimal numerals and
	 * prevent parsing errors due to integer conversion overflows (use BigInteger).
	 */
	private long castStringValueToLong(String value){
		long longContentLength = -1L;
		try{
			BigInteger contentLength = new BigInteger(value);
			if(contentLength.signum() == -1){
				throw new NumberFormatException();
			}else{
				try{
					longContentLength = contentLength.longValueExact();
				}catch(ArithmeticException e){
					/*
					 * Api-doc Sevlet 4.0 ServletRequest.getContentLengthLong()
					 * Return -1L if the length is not known.
					 * (If content-length too long, value valid but unknow -> -1).
					 */
				}
			}
		}catch(NumberFormatException e){
			throw new HttpRequestParseException(400, "Bad Request");
		}
		return longContentLength;
	}
}
