package pokerEngine;

import java.io.IOException;
import java.net.*;
import java.util.Vector;

/**
 * Class that handles networking when hosting the game.
 */
public class HostingServer {

	private ServerSocket socket;
	private Vector<Player> players; //Thread safe, local player is always index 0
	private Shoe shoe;
	
	/**
	 * Begins hosting the game on a specified port. The current player is also initialized, dealing them their two cards.
	 * @param port The port number to host the game on.
	 * @param decks The amount of decks in play.
	 * @param startingBal The balance that all players will start with when the game begins.
	 * @throws IOException 
	 */
	public HostingServer(int port, int decks, double startingBal) throws IOException {
		socket = new ServerSocket(port);
		players = new Vector<>();
		shoe = new Shoe(decks);
		Hand h = new Hand(shoe.dealCard(), shoe.dealCard());
		players.add(new Player(h, startingBal));
	}
	
	/**
	 * Gets the player with the specified index. The local player will always have an index of 0.
	 * @param i The index.
	 * @return The player with the specified index.
	 */
	public Player getPlayer(int i) {
		return players.get(i);
	}
	
	/**
	 * Cleanup method. Closes the socket and stops hosting.
	 * @throws IOException 
	 */
	public void close() throws IOException {
		socket.close();
	}
}
