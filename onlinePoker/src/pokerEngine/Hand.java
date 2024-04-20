package pokerEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents all or part player's or dealer's blackjack hand
 */
public class Hand
{
    private ArrayList<Card> cards;
    
    /**
     * Constructs a hand containing the specified cards
     * @param cards the cards to add
     */
    public Hand(Card... cards)
    {
        this.cards = new ArrayList<Card>();
        this.cards.addAll(Arrays.asList(cards));
    }
    
    /**
     * Constructs a hand containing the specified list of cards
     * @param cards the cards to add
     */
    public Hand(List<Card> cards)
    {
        this.cards = new ArrayList<Card>();
        this.cards.addAll(cards);
    }
    
    /**
     * Constructs a hand from an array of cards as bytes.
     * @param b the bytes
     */
    public Hand(byte[] bytes)
    {
    	cards = new ArrayList<Card>();
    	
    	for (byte b : bytes) {
    		cards.add(new Card(b));
    	}
    }
    
    /**
     * Constructs a hand from an array of cards as bytes.
     * @param b the bytes
     */
    public Hand(int[] bytes)
    {
    	cards = new ArrayList<Card>();
    	
    	for (int b : bytes) {
    		cards.add(new Card(b));
    	}
    }
    
    /**
     * Gets the best poker hand that 
     * can be made with this hand
     * @return the best hand, null if insufficient cards
     */
    public PokerHand getBestHand() {
		return getBestHand(this);
    }
    
    /**
     * Gets the best poker hand that 
     * can be made with this hand and another.
     * Useful for combining a hole hand with a table hand.
     * @return the best hand, null if insufficient cards
     */
    public PokerHand getBestHand(Hand o) {
		return getBestHand(this, o);
    }

    /**
     * Ranks the combination of the hands according to standard poker rules.
     * @return the best hand, null if insufficient cards
     */
    public static PokerHand getBestHand(Hand... hands) {
    	int[] order = {0, 12, 11, 10, 99, 8, 7, 6, 5, 4, 3, 2, 1};
    	
    	ArrayList<Card> cards = new ArrayList<Card>();
    	
    	for (Hand h : hands)
    		cards.addAll(h.getCards());
    	
    	if (cards.size() < 5)
    		return null; // change to error?
    	
    	Collections.sort(cards);
    	
    	int[] freqCards = new int[52];
    	int[] freqSuits = new int[4];
    	int[] freqVals = new int[13];
        
        for (Card c : cards) {
        	int b = c.toByte();
        	
        	freqCards[b]++;
        	freqSuits[b / 13]++;
        	freqCards[b % 13]++;
        }
        
        // Royal Flush
        for (int suit = 0; suit < 4; suit++) {
        	int a = suit * 13; // a is the ace of the specified suit
        	
        	if (freqCards[a] > 0 && 
        			freqCards[a + 12] > 0 && 
        			freqCards[a + 11] > 0 && 
        			freqCards[a + 10] > 0 && 
        			freqCards[a + 9] > 0)
        		return new PokerHand(HandType.ROYAL_FLUSH, a, a + 12, a + 11, a + 10, a + 9);
        }
        
        // Straight Flush
        for (int num = 12; num >= 5; num--) {
        	
        	toNextCard:
        	for (int suit = 0; suit < 4 && cards.size() < 5; suit++) {
        		int hi = suit * 13 + num; // highest card
        		
        		for (int o = 0; o < 5; o++) // offset from high card
        			if (freqCards[hi - o] == 0)
        				continue toNextCard;
        		
        		return new PokerHand(HandType.STRAIGHT_FLUSH, hi, hi - 1, hi - 2, hi - 3, hi - 4);
        	}
        }
        
        // Four of a kind
        for (int val = 0; val < 13; val++) {
        	int 
        	
        }
        
        return new PokerHand(HandType.CARDHIGH, cards);
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
        
        return output;
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