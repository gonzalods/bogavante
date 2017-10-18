package org.gms.bogavante.connector.http.header.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;
import org.gms.bogavante.connector.http.HttpRequestParseException;

public class ValidatorAndParseHeader {

	private HeaderParserChain nextParseHeader;
	
	public ValidatorAndParseHeader(){
		init();
	}
	/*
	 * RFC 7230 apartado 3.2.6 Field Value Components 
	 * Delimiters (DQUOTE and "(),/:;<=>?@[\]{}").
	 * token 			= 1*tchar
	 * tchar 			= "!" / "#" / "$" / "%" / "&" / "’" / "*"
	 * 					  / "+" / "-" / "." / "^" / "_" / "‘" / "|" / "~"
	 * 					  / DIGIT / ALPHA ; any VCHAR, except delimiters
	 * quoted-string 	= DQUOTE *( qdtext / quoted-pair ) DQUOTE
	 * qdtext 			= HTAB / SP /%x21 / %x23-5B / %x5D-7E / obs-text
	 * obs-text 		= %x80-FF
	 * comment 			= "(" *( ctext / quoted-pair / comment ) ")"
	 * ctext 			= HTAB / SP / %x21-27 / %x2A-5B / %x5D-7E / obs-text
	 * quoted-pair 		= "\" ( HTAB / SP / VCHAR / obs-text )
	 */
	public void validateAndParse(HttpRequest request, HttpHeader header){
		// TODO Mirar si hay que validar de forma genérica los campos-valor.
		nextParseHeader.parse(header, request);
	}
	
	private void init(){
		HostHeaderParser hostHeader = new HostHeaderParser();
		this.nextParseHeader = hostHeader;
		ContentLengthHeaderParser contentLenght = new ContentLengthHeaderParser();
		hostHeader.nextParseHeader(contentLenght);
		TransferEncodingHeaderParser transferEncoding = new TransferEncodingHeaderParser();
		contentLenght.nextParseHeader(transferEncoding);
	}
	
	/*
	 * RFC 7230 - 7 ABNF List Extension: #rule
	 * A construct "#" is defined, similar to "*", for defining comma-delimited lists 
	 * of elements. The full form is "<n>#<m>element" indicating at least <n> and at 
	 * most <m> elements, each separated by a single comma (",") and optional whitespace (OWS).
	 * 	 1#element 	=> element *( OWS "," OWS element )
	 * 	 #element => [ 1#element ]
	 * and for n >= 1 and m > 1:
	 * 	 <n>#<m>element => element <n-1>*<m-1>( OWS "," OWS element )
	 * 
	 * A recipient MUST accept lists that satisfy the following syntax:
	 * 	 #element => [ ( "," / element ) *( OWS "," [ OWS element ] ) ]
	 *   1#element => *( "," OWS ) element *( OWS "," [ OWS element ] )
	 *   
	 * Then the following are valid values
	 * 	 "foo,bar"; "foo ,bar,"; "foo , ,bar,charlie  "
	 * The following values would be invalid if at least one non-empty element is required
	 * 	 ""; "," ", ,"
	 */
	public static String[] parseCommaDelimitedList(String fieldValue, boolean required){
		Pattern pattern = Pattern.compile("[ \\t]*,[ \\t]*");
		String[] values = pattern.split(fieldValue);
		List<String> filterValues = new ArrayList<>();
		for(int i = 0;i < values.length;i++){
			String value = values[i].trim();
			if(value.trim().isEmpty()) continue;
			filterValues.add(value);
		}
		if(required && filterValues.size() == 0){
			throw new HttpRequestParseException(400, "Bad Request");
		}
		return filterValues.toArray(new String[filterValues.size()]);
	}
}
