package org.gms.bogavante.connector.http.encoding;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.SocketInputStream;
import org.gms.bogavante.connector.http.processor.HttpRequest;

public class ChunkedEncodingBody implements TransferEncodingBody {

	private static final byte CR = (byte)'\r';
	private static final byte LF = (byte)'\n';
	private static final byte SC = (byte)';';
	
	private byte[] payload = new byte[16384];
	private TransferEncodingBody nextEncodingBody;
	
	private int payloadSize = 0;
	private Map<String, String> mapChunkExt = new LinkedHashMap<>();
	private TrailerHeadersValidatorAndParser trailerHeadersParser;

	@Override
	public void decodeBody(InputStream input, HttpRequest request) throws IOException{

		int chunkSize = readChunkSize(input);
		while(chunkSize > 0){
			byte[] chunkData = readChunkData(input, chunkSize);
			if(payloadSize + chunkSize > payload.length){
				payload = Arrays.copyOf(payload, payload.length * 2);
			}
			System.arraycopy(chunkData, 0, payload, payloadSize, chunkSize);
			payloadSize += chunkSize;
			chunkSize = readChunkSize(input);
		}
		List<String> trailerValues = request.getHeaderValues("Trailer");  
		if(trailerValues != null && !trailerValues.isEmpty()){
			readTrailerPart(input,request,trailerValues);
		}
		
		
		if(nextEncodingBody != null){
			ByteArrayInputStream newInput = new ByteArrayInputStream(payload);
			nextEncodingBody.decodeBody(newInput, request);
		}
	}

	@Override
	public void nextEncoding(TransferEncodingBody next) {
		this.nextEncodingBody = next;
		
	}
	
	private int readChunkSize(InputStream input) throws IOException{
		
		StringBuilder aux = new StringBuilder("0x");
		String chunk_ext = "";
		int chunkSize = 0;
		boolean quoted = false;
		boolean endLine = false;
		while(true){
			int ch = input.read();
			if(ch == SC && !quoted && chunkSize == 0){
				chunkSize = Integer.decode(aux.toString()).intValue();
				aux = new StringBuilder();
			}else if(ch == CR && !quoted){
				endLine = true;
			}else if(ch == LF && endLine){
				if(chunkSize == 0){
					chunkSize = Integer.decode(aux.toString()).intValue();
				}else{
					chunk_ext = aux.toString();
				}
				break;
			}
			aux.append((char)ch);
		}
		parseChunkExt(chunk_ext);
		return chunkSize;
	}
	
	private void parseChunkExt(String extensions){
		if(extensions.isEmpty()) return;
		String[] chunk_exts = extensions.split(";");
		for (String chunk_ext : chunk_exts) {
			String[] aux =  chunk_ext.split("=");
			mapChunkExt.put(aux[0].trim(), aux[1].trim());
		}
	}
	
	private byte[] readChunkData(InputStream input, int chunkSize) throws IOException{
		byte[] chunkData = new byte[chunkSize];
		input.read(chunkData);
		//Se leen CR y LF
		input.read();
		input.read();
		return chunkData;
	}
	
	private void readTrailerPart(InputStream input, HttpRequest request,List<String> trailerValues) throws IOException{
		SocketInputStream sis = (SocketInputStream)input;
		while(true){
			HttpHeader header = new HttpHeader();

			sis.readHeader(header);
			String header_name = header.getHeader_name();
			if(trailerValues.contains(header_name)){
				if(trailerHeadersParser.isForbidden(header_name)){
					throw new HttpRequestParseException(400,"Bad Request");
				}
				trailerHeadersParser.validateAndParse(request, header);
			}
			
			if(header.getHeader_name() == null){// End of trailer section
				break;
			}
		}
	}
}
