package pokerEngine;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;

public class PokerHand extends Hand {
	
	private HandType type;

	public PokerHand(HandType type, ArrayList<Card> cards) {
		super(cards);
		
		if (cards.size() != 5)
			throw new InvalidParameterException("Final hands must have exactly 5 cards, got " + cards.size());
		
		Collections.sort(getCards());
		
		this.type = type;
	}
	
	public PokerHand(HandType type, int... bytes) {
		super(bytes);
		
		if (bytes.length != 5)
			throw new InvalidParameterException("Final hands must have exactly 5 cards, got " + bytes.length);
		
		Collections.sort(getCards());
		
		this.type = type;
	}
}
