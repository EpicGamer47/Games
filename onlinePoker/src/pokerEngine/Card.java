package pokerEngine;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Represents a playing card
 */
public class Card  implements Comparable<Card>
{
	static int[] order = {0, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
	
	public static final List<String> suits = Arrays.asList(new String[] {"S", "H", "D", "C"});
	
    private String suit;
    private int value;
    
    /**
     * Constructs a playing card with the specified suit and value
     * @param suit one of "D", "H", "S", "C"
     * @param value 1 - 13 corresponding to Ace, 2 - 10, Jack, Queen, King
     * @throws IllegalArgumentException if argument formats don't match
     */
    public Card(String suit, int value)
    {
        if (suits.indexOf(suit) == -1)
            throw new IllegalArgumentException("suit must be one of \"S\", \"H\", \"H\", \"C\"");
        
        if(! (1 <= value && value <= 13) )
            throw new IllegalArgumentException("value must be 1 - 13 (inclusive)");
        
        this.suit = suit;
        this.value = value;
    }
    
    public Card(int b) {
    	this(suits.get(b/13), b % 13 + 1);
    }
    
    public Card(byte b) {
    	this(suits.get(b/13), b % 13 + 1);
    }
    
    /**
     * Returns this card's suit ("D", "H", "S" or "C")
     * @return this card's suit
     */
    public String getSuit()
    {
        return suit;
    }
    
    /**
     * Return this card's value (1 - 13 corresponding to Ace, 2 - 10, Jack, Queen, King)
     * @return this card's value
     */
    public int getValue()
    {
        return value;
    }
    
    /**
     * Returns this card with the 1 or 2 character value (A, 2-10, J, Q, K)
     * followed by the 1 character suit (D, H, S, C)
     * Examples: JD, 10H, AS, 9C
     */
    public String toString()
    {
    	switch (value) {
    	case 1: return "A" + suit;
    	case 10: return "T" + suit; // note
    	case 11: return "J" + suit;
    	case 12: return "Q" + suit;
    	case 13: return "K" + suit;
    	default: return Integer.toString(value) + suit;
    	}
    }
    
    public String toName()
    {
    	switch (value) {
    	case 1: return "Ace";
    	case 11: return "Jack";
    	case 12: return "Queen";
    	case 13: return "King";
    	default: return Integer.toString(value);
    	}
    }
    
    /**
     * Returns a single byte representing this card.
     * @return A byte representing this card.
     */
    public byte toByte() {
    	return (byte) (suits.indexOf(suit) * 13 + (value - 1));
    }
    
    /**
     * Draws this card to a specified location on the window.
     * @param a The window PApplet
     * @param x The x-coordinate of the card.
     * @param y The y-coordinate of the card.
     */
    public void draw(PApplet a, float x, float y) {
    	String path = "cards/";
    	switch (value) {
    	case 1: path += "ace";
    	case 11: path += "jack";
    	case 12: path += "queen";
    	case 13: path += "king";
    	default: path += value;
    	}
    	
    	switch (suit) {
    	case "S": path += "spades";
    	case "H": path += "hearts";
    	case "D": path += "diamonds";
    	default: path += "clubs";
    	}
    	
    	path += ".GIF";
    	PImage img = a.loadImage(path);
    	a.image(img, x, y);
    }
    
    /**
	 * Compares the cards in nonincreasing order: A, K, ... 2
	 */
	@Override
	public int compareTo(Card o) {
		if (this.value != o.value)
			return order[o.value - 1] - order[value - 1];
		
		int suitDifference = suits.indexOf(o.suit) - suits.indexOf(this.suit);
		
		return suitDifference;
	}
	
	/**
	 * Converts a list of cards into an array of bytes
	 * @param cards The cards to be converted
	 * @return An array, with each card in cards converted into a byte in the same position
	 */
	public static byte[] cardsToBytes(ArrayList<Card> cards) {
		byte[] out = new byte[cards.size()];
		
		for (int i = 0; i < out.length; i++)
			out[i] = cards.get(i).toByte();
		
		return out;
	}
}
