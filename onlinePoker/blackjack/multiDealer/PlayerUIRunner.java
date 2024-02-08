package multiDealer;

import java.net.InetAddress;
import java.util.Scanner;

public class PlayerUIRunner
{
	public static void main(String[] args) throws Exception
	{
		Scanner in = new Scanner(System.in);
		
		System.out.print("Enter IP Address: ");
		InetAddress server = InetAddress.getByName(in.nextLine());
		System.out.print("Enter Port Number: ");
		int port = in.nextInt();
		
		in.close();	
		
		PlayerUI bjui = new PlayerUI(server, port);
		bjui.playHandsUntilQuit();
	}
}
