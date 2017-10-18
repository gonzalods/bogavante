package org.gms.bogavante.connector.http.processor.http1;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.servlet.ServletException;

import org.gms.bogavante.connector.http.HttpConnector;
import org.gms.bogavante.connector.http.HttpHeader;
import org.gms.bogavante.connector.http.HttpRequest;
import org.gms.bogavante.connector.http.HttpRequestLine;
import org.gms.bogavante.connector.http.HttpRequestParseException;
import org.gms.bogavante.connector.http.HttpResponse;
import org.gms.bogavante.connector.http.SocketInputStream;
import org.gms.bogavante.connector.http.header.parser.ValidatorAndParseHeader;

public class Http1Processor {

	private HttpConnector connector;
	private HttpRequest request;
	private HttpResponse response;
	private HttpRequestLine requestLine = new HttpRequestLine();

	public Http1Processor(HttpConnector connector) {
		this.connector = connector;
	}

	public void process(Socket socket) {

		try (SocketInputStream input = new SocketInputStream(socket.getInputStream(),2048);
			OutputStream output = socket.getOutputStream();){
			
			boolean keepAlive = true;
			while(keepAlive){
				request = new HttpRequest(input,connector.getScheme());
	
				response = new HttpResponse(output);
				response.setRequest(request);
	
				response.setHeader("Server", "Bogavante Server");
	
				try{
					parseRequest(input);
					parseHeader(input);
				}catch(HttpRequestParseException e){
					String statusLine = createStatusLine(e.getCodeError(), e.getMessage());
					output.write(statusLine.getBytes());
					return;
				}
				
				//TODO Temporal hasta que se implemente HttpResponse
				String statusLine = createStatusLine(200, "OK");
				output.write(statusLine.getBytes());
				
	//			if (request.getRequestURI().startsWith("/servlet/")) {
	//				ServletProcessor processor = new ServletProcessor();
	//				processor.process(request, response);
	//			} else {
	//				StaticResourceProcessor processor = new StaticResourceProcessor();
	//				processor.process(request, response);
	//			}
//				String connection = request.getHeader("Connection");
//				if(connection != null && connection.equals("close")){
					keepAlive = false;
//				}
			}
			//socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseRequest(SocketInputStream input) throws IOException, ServletException {
		
		input.readRequestLine(requestLine);
		request.setMethod(requestLine.getMethod());
		request.setRequestURI(requestLine.getRequest_target());
		
		
	}

	private void parseHeader(SocketInputStream input) throws IOException, ServletException {
		ValidatorAndParseHeader parser = new ValidatorAndParseHeader();
		while(true){
			HttpHeader header = new HttpHeader();

			input.readHeader(header);
			parser.validateAndParse(request, header);
			
			if(header.getHeader_name() == null){// End of header section
				break;
			}
		}
		if(request.getEffectiveRequestURI()==null){//No Host header field
			throw new HttpRequestParseException(400, "Bad Request");
		}
	}
	
	private String createStatusLine(int code, String message){
		StringBuilder statusLine = new StringBuilder("HTTP/1.1 ")
				.append(code).append(' ').append(message).append("\r\n");
		return statusLine.toString();
	}
}
