package onlineEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleServer implements AutoCloseable {
	ServerSocket socket;
	Socket client;

	public SingleServer(int port) throws Exception {
		socket = new ServerSocket(port);
		
		System.out.println("IP Address: " + InetAddress.getLocalHost().toString());
	}
	
	public String readString() {
		try {
			var in = client.getInputStream();
			
			int len = in.read();
			byte[] buffer = new byte[len];
			in.read(buffer);
			
			return new String(buffer);
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public byte[] readBytes() {
		try {
			var in = client.getInputStream();
			
			int len = in.read();
			byte[] buffer = new byte[len];
			in.read(buffer);
			
			return buffer;
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void write(String output) {
		try {
			var out = client.getOutputStream();
			
			out.write(output.length());
			out.write(output.getBytes());
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeBytes(byte[] buffer) throws IOException {
		try {
			var out = client.getOutputStream();
			
			out.write(buffer.length);
			out.write(buffer);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeByte(byte b) throws IOException {
		try {
			var out = client.getOutputStream();
			
			out.write(1);
			out.write(new byte[] {b});
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void acceptClient() throws IOException {
		client = socket.accept();
		System.out.println("Connection from " + client.getInetAddress().toString());
	}
	
	public Socket getClient() throws IOException {
		return client;
	}

	@Override
	public void close() throws Exception {
		socket.close();
	}
}
