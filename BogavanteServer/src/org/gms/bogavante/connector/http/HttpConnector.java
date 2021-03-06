package org.gms.bogavante.connector.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.gms.bogavante.connector.http.processor.HttpProcessor;

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
				try(Socket socket = serverSocket.accept()){
			
					HttpContext context = new HttpContext(scheme);
					HttpProcessor processor = new HttpProcessor(context);
					processor.process(socket);
					
				}catch(IOException e){
					continue;
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
	}
}
