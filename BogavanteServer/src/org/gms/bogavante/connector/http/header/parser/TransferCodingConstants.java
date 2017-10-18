package org.gms.bogavante.connector.http.header.parser;

public interface TransferCodingConstants {

	/*
	 * RFC 7230 4. Transfer Codings
	 * transfer-coding 	= "chunked" 
	 *					/ "compress"
	 *					/ "deflate" 
	 *					/ "gzip" 
	 *					/ transfer-extension
	 * (Constants definded for fixed codings) 
	 */
	String CHUNKED = "chunked"; 
	String COMPRESS = "compress";
	String X_COMPRESS = "x-compress";
	String DEFLATE = "deflate";
	String GZIP = "gzip";
	String X_GZIP = "x-gzip";
	String FIXE_TRANSFER_CODINGS_PATTERN ="(chunked)|(compress)|(deflate)|(gzip)|(x-compress)|(x-zip)";
	String TRANSFER_CODING_EXT_PATTERN = "\\w+([ \\t]*;[ \\t]*\\w+[ \\t]*=[ \\t]*(\\w+|\"(.)*\"))*";

}
