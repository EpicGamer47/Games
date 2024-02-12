package onlineEngine;

import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class SingleServer implements AutoCloseable {
	ServerSocket socket;
	Socket client;

	public SingleServer(int port) throws Exception {
		socket = new ServerSocket(port);
		
		System.out.println("IP Address: " + InetAddress.getLocalHost().toString());
	}
	
	public String read() throws IOException {
		var in = client.getInputStream();
		
		int len = in.read();
		byte[] buffer = new byte[len];
		in.read(buffer);
		
		return new String(buffer);
	}
	
	public void write(String output) throws IOException {
		var out = client.getOutputStream();
		
		out.write(output.length());
		out.write(output.getBytes());
	}
	
	public void writeBytes(byte[] buffer) throws IOException { 
		var out = client.getOutputStream();
		
		out.write(buffer.length);
		out.write(buffer);
	}
	
	public void acceptClient() throws IOException {
		client = socket.accept();
		System.out.println("Connection from " + client.getInetAddress().toString());
	}

	@Override
	public void close() throws Exception {
		socket.close();
	}
}
