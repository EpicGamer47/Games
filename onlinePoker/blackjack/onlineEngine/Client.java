package onlineEngine;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Client implements AutoCloseable {
	Socket server;
	ServerSocket socket;

	public Client(InetAddress server, int port) throws Exception {
		socket = new ServerSocket(port);
		
		System.out.println("Connected to " + server.toString());
	}
	
	public String read() throws IOException {
		var in = server.getInputStream();
		
		int len = in.read();
		byte[] buffer = new byte[len];
		in.read(buffer);
		
		return new String(buffer);
	}
	
	public void write(String output) throws IOException {
		var out = server.getOutputStream();
		
		out.write(output.length());
		out.write(output.getBytes());
	}

	@Override
	public void close() throws Exception {
		socket.close();
	}
}
