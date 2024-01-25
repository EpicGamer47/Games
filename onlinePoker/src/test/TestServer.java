package test;

import java.util.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter Port Number: ");
		int port = sc.nextInt();
		sc.nextLine();
		
		ServerSocket socket = new ServerSocket(port);
		Socket client = socket.accept();
		System.out.println("Connection from " + client.getInetAddress().toString());
		InputStream read = client.getInputStream();
		OutputStream write = client.getOutputStream();
		String msg = "";
		do {
			int len = read.read();
			byte[] buffer = new byte[len];
			read.read(buffer);
			System.out.println(new String(buffer));
			System.out.print("Enter message or leave blank to quit: ");
			msg = sc.nextLine();
			if (!msg.isBlank()) {
				write.write(msg.length());
				write.write(msg.getBytes());
			}
		} while(!msg.isBlank());
		sc.close();
		socket.close();
	}

}
