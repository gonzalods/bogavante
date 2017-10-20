package org.gms.bogavante.connector.http.header.parser;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.processor.HttpRequest;

public interface HeaderParserChain {

	public void parse(HttpHeader header, HttpRequest request);
	public void nextParseHeader(HeaderParserChain headerParser);
}
