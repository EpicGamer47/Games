package multiDealer;

import java.net.InetAddress;

public class DealerUIRunner
{
	public static void main(String[] args) throws Exception
	{
//		InetAddress server = InetAddress.getByName("123.234.32.23");
		int port = 456;
		
		DealerUI bjui = new DealerUI(port);
		bjui.playHandsUntilQuit();
	}
}
