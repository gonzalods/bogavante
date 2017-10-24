package org.gms.bogavante.connector.http.encoding;

import java.util.HashSet;
import java.util.Set;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.header.parser.HeaderParserChain;
import org.gms.bogavante.connector.http.processor.HttpRequest;

public class TrailerHeadersValidatorAndParser {

	private HeaderParserChain firstParseHeader;
	private Set<String> forbiddenHeaders;
	
	public TrailerHeadersValidatorAndParser(){
		init();
	}
	
	public void validateAndParse(HttpRequest request, HttpHeader header){
		firstParseHeader.parse(header, request);
	}
	
	private void init(){
		forbiddenHeaders = new HashSet<>();
		forbiddenHeaders.add("transfer-encoding");
		forbiddenHeaders.add("Content-Length");
		forbiddenHeaders.add("host");
		forbiddenHeaders.add("content-encoding");
		forbiddenHeaders.add("content-type");
		forbiddenHeaders.add("content-range");
		forbiddenHeaders.add("trailer");
		//TOD faltan más cabeceras prohibidas
	}
	
	public boolean isForbidden(String headerName){
		return forbiddenHeaders.contains(headerName.toLowerCase());
	}
}
