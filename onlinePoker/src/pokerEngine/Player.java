package pokerEngine;

/**
 * Represents a player currently playing the game.
 */
public class Player {

	private Hand hand;
	private double balance;
	
	public Player(Hand hand, double balance) {
		this.hand = hand;
		this.balance = balance;
	}
	
	public Hand getHand() {
		return hand;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public void setHand(Hand h) {
		hand = h;
	}
	
	public void setBalance(double bal) {
		balance = bal;
	}
}
