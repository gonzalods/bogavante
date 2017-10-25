package org.gms.bogavante.connector.http.processor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.encoding.ChunkedDecoderBody;
import org.gms.bogavante.connector.http.encoding.TransferDecoderBody;

public class Http1MessageBodyProcessor{

	private TransferDecoderBody firstTransferDecoderBody;
	
	public void process(InputStream input, HttpRequest request) throws IOException{
		List<String> transferEncodings = request.getHeaderValues("Transfer-Encoding");
		if(transferEncodings != null && !transferEncodings.isEmpty()){
			createTransferDecoderBodyChain(transferEncodings);
			firstTransferDecoderBody.decodeBody(input, request);
		}
	}
	
	private void createTransferDecoderBodyChain(List<String> transferEncodings){
		int size = transferEncodings.size();
		String encoding = transferEncodings.get(size - 1);
		firstTransferDecoderBody = createTrasnferDecoderBody(encoding);
		TransferDecoderBody nextTransferDecoderBody = firstTransferDecoderBody;
		for(int i = size - 2; i > -1; i--){
			encoding = transferEncodings.get(i);
			TransferDecoderBody decoder = createTrasnferDecoderBody(encoding);
			nextTransferDecoderBody.nextEncoding(decoder);
			nextTransferDecoderBody = decoder;
		}
	}
	
	private TransferDecoderBody createTrasnferDecoderBody(String encoding){
		switch(encoding.toLowerCase()){
			case "chunked":
				return new ChunkedDecoderBody(); 
			default:
				throw new HttpRequestParseException(501, "Not Implemented");
		}
	}
}
