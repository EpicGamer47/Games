package pokerEngine;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;

public class PokerHand extends Hand implements Comparable<PokerHand> {
	
	private HandType type;

	public PokerHand(HandType type, ArrayList<Card> cards) {
		super(cards);
		
		if (cards.size() != 5)
			throw new InvalidParameterException("Final hands must have exactly 5 cards, got " + cards.size());
		
		this.type = type;
	}
	
	public PokerHand(HandType type, int... bytes) {
		super(bytes);
		
		if (bytes.length != 5)
			throw new InvalidParameterException("Final hands must have exactly 5 cards, got " + bytes.length);
		
		this.type = type;
	}

	public PokerHand(HandType type, byte... bytes) {
		super(bytes);
		
		if (bytes.length != 5)
			throw new InvalidParameterException("Final hands must have exactly 5 cards, got " + bytes.length);
		
		this.type = type;
	}

	@Override
	public int compareTo(PokerHand o) {
		int type = this.type.compareTo(o.type);
		
		if (type != 0)
			return type;
		
		for (int i = 0; i < 5; i++) {
			int val = o.cards.get(i).compareTo(cards.get(i));
			
			if (val != 0)
				return val;
		}
		
		return 0;
	}
	
	@Override
	public String toString() {
//		return super.toString() + " " + type.toString();
		
		switch (type) {
		case ROYAL_FLUSH:
			return "Royal Flush";
		case STRAIGHT_FLUSH:
			return String.format("%s-high Straight Flush", cards.get(0).toName());
		case QUADS:
			return String.format("Quads of %ss", cards.get(0).toName());
		case BOAT:
			return String.format("Full house, %ss over %ss", cards.get(0).toName(), cards.get(3).toName());
		case FLUSH:
			return String.format("%s-high Flush", cards.get(0).toName());
		case STRAIGHT:
			return String.format("%s-high Straight", cards.get(0).toName());
		case TRIPLE:
			return String.format("Set of %ss", cards.get(0).toName());
		case TWO_PAIR:
			return String.format("Two pair, %ss and %ss", cards.get(0).toName(), cards.get(2).toName());
		case PAIR:
			return String.format("Pair of %ss", cards.get(0).toName());
		case CARD_HIGH:
			return String.format("%s-high", cards.get(0).toName());
		}
		
		return null;
	}
}
