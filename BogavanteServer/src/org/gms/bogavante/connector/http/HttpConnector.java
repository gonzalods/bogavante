package org.gms.bogavante.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.gms.bogavante.connector.http.processor.http1.Http1Processor;

public class HttpConnector implements Runnable{

	private boolean stopped;
	private String scheme = "http";
	
	public String getScheme(){
		return scheme;
	}
	public void start(){
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		int port = 8080;
		
		try(ServerSocket serverSocket = new ServerSocket(port,1)){
			while(!stopped){
				try(Socket socket = serverSocket.accept();
					SocketInputStream input = new SocketInputStream(socket.getInputStream(),2048);
					OutputStream output = socket.getOutputStream()){

					try{
						HttpRequestLine requestLine = new HttpRequestLine();
						input.readRequestLine(requestLine);
					}catch(HttpRequestParseException e){
						String statusLine = createStatusLine(e.getCodeError(), e.getMessage());
						output.write(statusLine.getBytes());
					}
					
//					Http1Processor processor = new Http1Processor(this);
//					processor.process(socket);
				}catch(IOException e){
					continue;
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
	
	private String createStatusLine(int code, String message){
		StringBuilder statusLine = new StringBuilder("HTTP/1.1 ")
				.append(code).append(' ').append(message).append("\r\n");
		return statusLine.toString();
	}
}
