package org.gms.bogavante.connector.http.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.gms.bogavante.StaticResourceProcessor;
import org.gms.bogavante.connector.http.HttpContext;
import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.SocketInputStream;
import org.gms.bogavante.connector.http.header.parser.Http1ValidatorAndParseHeader;


public class Http1RequestProcessor implements HttpRequestProcessor {

	private HttpContext context;
	private HttpRequest request;
	private HttpResponse response;
	private Http1ValidatorAndParseHeader headerParser;
	private Http1RequestLineValidatorAndParser lineRequestParser;
	private Http1MessageBodyProcessor messageBodyProcessor;
	
	private SocketInputStream input;
	private OutputStream output;
	
	private StaticResourceProcessor staticResourceProcessor;
	
	public Http1RequestProcessor(HttpContext context) {
		this.context = context;
	}
	
	@Override
	public void process() throws IOException{
		validateAndParseRequestLine();
		validateAndParseHeaders();
		messageBodyProcessor.process(input, request);
		staticResourceProcessor.process(request, response);

	}
	
	private void validateAndParseRequestLine(){
		lineRequestParser.validateAndParse(request, context.getRequesLine());
	}
	
	private void validateAndParseHeaders() throws IOException{
		while(true){
			HttpHeader header = new HttpHeader();

			input.readHeader(header);
			headerParser.validateAndParse(request, header);
			
			if(header.getHeader_name() == null){// End of header section
				break;
			}
		}
		if(request.getEffectiveRequestURI()==null){//No Host header field
			throw new HttpRequestParseException(400, "Bad Request");
		}
	}
	
	public void setRequest(HttpRequest request){
		this.request = request;
	}
	
	public void setResponset(HttpResponse response){
		this.response = response;
	}
	
	public void setStaticResourceProcessor(StaticResourceProcessor staticResourceProcessor){
		this.staticResourceProcessor = staticResourceProcessor;
	}

	public void setValidatorAndParserRequestLine(){
		
	}
	
	public void setMessageBodyProcessor(Http1MessageBodyProcessor messageBodyProcessor) {
		this.messageBodyProcessor = messageBodyProcessor;
	}

	@Override
	public void setInputStream(SocketInputStream input) {
		this.input = input;
	}

	@Override
	public void setOutputStream(OutputStream output) {
		this.output = output;
		
	}

	@Override
	public boolean isKeepAlive() {
		return false;
	}

}
