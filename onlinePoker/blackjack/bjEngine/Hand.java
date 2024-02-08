package bjEngine;

import java.util.ArrayList;

/**
 * Represents all or part player's or dealer's blackjack hand
 */
public class Hand
{
    private ArrayList<Card> cards;
    
    /**
     * Constructs a hand containing the specified 2 cards
     * @param card1 the first card
     * @param card2 the second card
     */
    public Hand(Card card1, Card card2)
    {
        cards = new ArrayList<Card>();
        cards.add(card1);
        cards.add(card2);
    }

    /**
     * Returns the numerical value of this hand according to the rules of blackjack
     * @return the numerical value of this hand
     */
    public int getValue()
    {
        int totalScore = 0;
        boolean handContainsAce = false;
        
        for (Card card : cards) { 
        	totalScore += card.getAdjustedValue();
        	if (card.getValue() == 1) handContainsAce = true;
        }
        
        if (handContainsAce && totalScore < 12) totalScore += 10;
        
        return totalScore;
    }

    /**
     * Returns true if this hand is soft (has an ace counting as 11)
     * @return true if this hand is soft, false otherwise
     */
    public boolean isSoft()
    {
    	boolean handContainsAce = false;
    	int totalScore = 0;
    	
    	for (Card card : cards) { 
    		totalScore += card.getAdjustedValue();
        	if (card.getValue() == 1) handContainsAce = true;
        }
    	
    	return handContainsAce && totalScore < 12;
    }
    
    /**
     * Returns true if this hand is a blackjack, false otherwise
     * @return true if this hand is a blackjack, false otherwise
     */
    public boolean isBlackjack()
    {
    	return this.getNumCards() == 2 && this.getValue() == 21;
    }

    /**
     * Returns the cards in this hand
     * @return the cards in this hand
     */
    public ArrayList<Card> getCards()
    {
        return cards;
    }
    
    /**
     * Returns the cards in this hand followed by their numerical value
     * Ex: JS AH (21)
     * @return the string representation of a Hand
     */
    public String toString()
    {
    	String output = "";
    	
        for (Card card : cards) {
        	output += card.toString() + ' ';
        }
        
        if (isBlackjack())
        	return output + "(Blackjack!)";
        if (isSoft())
        	return output + "(Soft " + this.getValue() + ')';
        else
        	return output + '(' + this.getValue() + ')';
    }
    
    public byte[] toBytes()
    {
    	byte[] bytes = new byte[cards.size()];
    	
    	for (int i = 0; i < cards.size(); i++) {
    		bytes[i] = cards.get(i).toByte();
    	}
    	
    	return bytes;
    }

    /**
     * Adds the specified card to this hand
     * @param card the card to add
     */
    public void addCard(Card card)
    {
    	cards.add(card);
    }

    /**
     * Returns the number of cards in this hand
     * @return the number of cards in this hand
     */
    public int getNumCards()
    {
        return cards.size();
    }
}