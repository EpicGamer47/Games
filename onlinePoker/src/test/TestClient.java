package test;

import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Scanner;

public class TestClient {

	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter IP Address: ");
		InetAddress ip = InetAddress.getByName(sc.nextLine());
		System.out.print("Enter Port Number: ");
		int port = sc.nextInt();
		sc.nextLine();
		
		Socket s = new Socket(ip, port);
		String msg = "";
		OutputStream write = s.getOutputStream();
		InputStream read = s.getInputStream();
		do {
			System.out.print("Enter message or leave blank to quit: ");
			msg = sc.nextLine();
			if (!msg.isEmpty()) {
				write.write(msg.length());
				write.write(msg.getBytes());
				int len = read.read();
				byte[] response = new byte[len];
				read.read(response);
				System.out.println("Server says: " + new String(response));
			}
		} while (!msg.isEmpty());
		s.close();
		sc.close();
	}

}
