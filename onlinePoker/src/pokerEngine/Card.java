package pokerEngine;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a playing card
 */
public class Card  implements Comparable<Card>
{
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
    
    public byte toByte() {
    	return (byte) (suits.indexOf(suit) * 13 + (value - 1));
    }
    
    /**
	 * Compares the cards in nonincreasing order: A, K, ... 2
	 */
	@Override
	public int compareTo(Card o) {
		if (this.value == 1)
			return 1;
		else if (o.value == 1)
			return -1;
		else if (this.value != o.value)
			return this.value - o.value;
		
		int suitDifference = suits.indexOf(this.suit) - suits.indexOf(o.suit);
		
		return suitDifference;
	}
}
