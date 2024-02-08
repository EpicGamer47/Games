package bjEngine;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a playing card
 */
public class Card
{
	private static final List<String> suits = Arrays.asList(new String[] {"D", "H", "S", "C"});
	
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
            throw new IllegalArgumentException("suit must be one of \"D\", \"H\", \"S\", \"C\"");
        
        if(! (1 <= value && value <= 13) )
            throw new IllegalArgumentException("value must be 1 - 13 (inclusive)");
        
        this.suit = suit;
        this.value = value;
    }
    
    public Card(byte b) {
    	this(suits.get(b/13), b % 13);
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
     * Return this card's adjusted value (1 - 10 corresponding to Ace, 2 - 10; face cards are all 10)
     * @return this card's adjusted value
     */
    public int getAdjustedValue()
    {
        return Math.min(value, 10);
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
    	case 11: return "J" + suit;
    	case 12: return "Q" + suit;
    	case 13: return "K" + suit;
    	default: return Integer.toString(value) + suit;
    	}
    }
    
    public byte toByte() {
    	return (byte) (suits.indexOf(suit) * 13 + value);
    }
}
