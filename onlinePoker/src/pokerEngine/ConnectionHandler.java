package pokerEngine;

import java.net.Socket;

/**
 * Thread that handles a single client's requests.
 */
public class ConnectionHandler extends Thread {

	private Socket socket;
	private int playerIndex;
	
	public ConnectionHandler(Socket socket, int playerIndex) {
		this.socket = socket;
		this.playerIndex = playerIndex;
	}
	
	public void run() {
		
	}
}
