package singlePlayer;

import java.text.NumberFormat;
import java.util.Scanner;

import bjEngine.Blackjack;

/**
 * A text based user interface that allows the user to
 * play a game of blackjack.
 */
public class PlayerUI
{
	private double MIN_BET = 50;
	
	private Blackjack bj; 
	private Scanner fromKeyboard;
	private NumberFormat nf;
	
	/**
	 * Constructs a blackjack game with $1,000 in player bankroll
	 */
	public PlayerUI()
	{
	    this.bj = new Blackjack(1000);
	    this.fromKeyboard = new Scanner(System.in);
	    this.nf = NumberFormat.getCurrencyInstance();
	}
	
	/**
     * Returns a valid numerical bet obtained from the player
     * @return a valid numerical bet obtained from the player
     */
    private double getValidBet()
    {
    	String bet = "-1";
    	do {
    		System.out.print(String.format("\nPlease enter a bet from %s to %s: ", nf.format(MIN_BET), nf.format(bj.getPlayersMoney())));
    		bet = fromKeyboard.nextLine();
    		
    		switch (bet) {
    		case "all":   bet = Double.toString(bj.getPlayersMoney()); break;
    		case "half":  bet = Double.toString(bj.getPlayersMoney()/2); break;
    		case "manny": bet = "69"; break;
    		case "min":   bet = Double.toString(MIN_BET); break;
    		}
    	} while (stringToNumber(bet) < MIN_BET || stringToNumber(bet) > bj.getPlayersMoney());
    	
    	return stringToNumber(bet);
    }
    
	private double getBet(String input) {
		switch (input) {
		
		}
		return 0;
	}

	/**
	 * Plays a single hand of blackjack
	 */
    public void playHand()
    {
    	bj.placeBetAndDealCards(getValidBet());
    	buffer();
    	
    	playPlayersHand();
    	bj.playDealersHand();
    	buffer();
    	
    	displayResult();
    	buffer();
    }
    
    /**
     * Plays blackjack hands until the user chooses to quit
     */
    public void playHandsUntilQuit()
    {
    	boolean quit = false;
    	while (!quit) {
    		playHand();
    		
    		if (bj.getPlayersMoney() < MIN_BET) {
    			System.out.print("You can no longer afford the minimum bet at the table.\nThe casino has kicked you out.");
    			buffer();
    			quit = true;
    		}
    		else {
    			System.out.print("Would you like to play another hand? (y/n): ");
            	char choice = fromKeyboard.nextLine().charAt(0);
            	
            	while (!(choice == 'y' || choice == 'n')) {
            		System.out.print("Error.\nWould you like to play another hand? (y/n): ");
            		choice = fromKeyboard.nextLine().charAt(0);;
            	}
            	
            	if (choice == 'y') quit = false;
            	else quit = true;
            	buffer();
    		}
    	}
    }

    /**
     * Allows the player to hit until it is no longer possible
     * to do so or until the player chooses to stand
     */
	private void playPlayersHand()
	{
		String action;
		
		System.out.println("Dealer's card: " + bj.getDealersHand().getCards().get(0).toString());
		
    	do {
    	    boolean isStanding = false;
    		while (bj.canHit() && !isStanding) {
    	    	String doubleDown = bj.canDoubleDown() ? "/double" : "";
    			String split = bj.canSplit() ? "/split" : ""; //only shows these options if they can be done
    			
    			System.out.print(String.format("\nCurrent Hand: %s\nWhat would you like to do? (hit/stand%s%s): ",
    					bj.getPlayersHand().toString(), doubleDown, split));
    			
    	    	action = fromKeyboard.nextLine().toLowerCase();
    	    	
    	    	while (!(action.equals("h") || action.equals("hit") 
    	    			||  action.equals("s") || action.equals("stand")
    	    			|| ((action.equals("d") || action.equals("double")) && bj.canDoubleDown())
    	    			|| ((action.equals("p") || action.equals("split")) && bj.canSplit()))) {
    	    		System.out.print(String.format("\nCurrent Hand: %s\nWhat would you like to do? (hit/stand%s%s): ",
    	    				bj.getPlayersHand().toString(), doubleDown, split));
    	    		
    		    	action = fromKeyboard.nextLine().toLowerCase();
    	    	}
    	    	
    	    	isStanding = doPlayerAction(action);
    	    }
    		
    		System.out.print("Final Hand: " + bj.getPlayersHand().toString() + "\n");
    	} while (bj.nextHand());
	}

	/**
     * Does the player's action
     * @return true if the player is standing, false otherwise
     */
	private boolean doPlayerAction(String action) {
		switch (action) {
    	case "h": case "hit":
    		bj.hit();
    		return false;
    	case "s": case "stand":
    		return true;
    	case "d": case "double":
    		bj.doubleDown();
    		return true;
    	case "p": case "split":
    		bj.split();
    		return false;
		}
		
		return false; // method shouldn't go here but compiler requires it
	}

	/**
	 * Displays the result of the hand (push, player win, player blackjack or loss)
	 */
	private void displayResult()
	{
		System.out.println("Final dealer's hand: " + bj.getDealersHand().toString());
		
	    String[] results = {"loss", "push", "player win", "player blackjack"};
	    for (int i = 0; i < bj.getNumHands(); i++) {
			System.out.println("\nFinal player's hand: " + bj.getPlayerHands().get(i).toString());
	    	System.out.println("Result: " + results[bj.getResult(i) + 1]);
	    }
	    
    	bj.resolveBetsAndReset();
	    System.out.println("\nPlayer's updated money: " + nf.format(bj.getPlayersMoney()));
	}

	/**
	 * Returns the numeric representation of input or -1 if input is not numeric
	 * @param input the value to be converted to a number
	 * @return numeric representation or -1
	 */
	private double stringToNumber(String input)
	{
		try
		{
			return Double.parseDouble(input);
		}
		catch(NumberFormatException e)
		{
			return -1;
		}
	}
	
	/*
	 * Prints a line to seperate different parts of the UI
	 */
	private void buffer() {
		System.out.print("\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n\n");
	}
}
