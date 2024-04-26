package pokerEngine;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static pokerEngine.HandType.*;
import static pokerEngine.Card.order;

/**
 * Represents all or part player's or dealer's blackjack hand
 */
public class Hand {
	protected ArrayList<Card> cards;

	/**
	 * Constructs a hand containing the specified cards
	 * 
	 * @param cards the cards to add
	 */
	public Hand(Card... cards) {
		this.cards = new ArrayList<Card>();
		this.cards.addAll(Arrays.asList(cards));
	}

	/**
	 * Constructs a hand containing the specified list of cards
	 * 
	 * @param cards the cards to add
	 */
	public Hand(List<Card> cards) {
		this.cards = new ArrayList<Card>();
		this.cards.addAll(cards);
	}

	/**
	 * Constructs a hand from an array of cards as bytes.
	 * 
	 * @param b the bytes
	 */
	public Hand(byte... bytes) {
		cards = new ArrayList<Card>();

		for (byte b : bytes) {
			cards.add(new Card(b));
		}
	}

	/**
	 * Constructs a hand from an array of cards as bytes.
	 * 
	 * @param b the bytes
	 */
	public Hand(int... bytes) {
		cards = new ArrayList<Card>();

		for (int b : bytes) {
			cards.add(new Card(b));
		}
	}

	/**
	 * Gets the best poker hand that can be made with this hand
	 * 
	 * @return the best hand, null if insufficient cards
	 */
	public PokerHand getBestHand() {
		return getBestHand(this);
	}

	/**
	 * Ranks the combination of the hands according to standard poker rules.
	 * 
	 * @return the best hand, null if insufficient cards
	 */
	public static PokerHand getBestHand(Hand... hands) {
		int[] freqCards = new int[52];
		int[] freqSuits = new int[4];
		int[] freqVals = new int[13];

		for (Hand h : hands) {
			for (Card c : h.cards) {
				int b = c.toByte();

				freqCards[b]++;
				freqSuits[b / 13]++;
				freqVals[b % 13]++;
			}
		}

		int[][] freqValsSorted = new int[13][2];

		for (int num = 0; num < 13; num++) {
			freqValsSorted[num][0] = num;
			freqValsSorted[num][1] = freqVals[num];
		}

		Arrays.sort(freqValsSorted, (a, b) -> {
			if (b[1] - a[1] != 0)
				return b[1] - a[1];
			else
				return order[a[0]] - order[b[0]];
		});

		// Royal Flush
		for (int suit = 0; suit < 4; suit++) {
			int a = suit * 13; // a is the ace of the specified suit

			if (freqCards[a] > 0 && freqCards[a + 12] > 0 && freqCards[a + 11] > 0 && freqCards[a + 10] > 0
					&& freqCards[a + 9] > 0)
				return new PokerHand(ROYAL_FLUSH, a, a + 12, a + 11, a + 10, a + 9);
		}

		// Straight Flush
		for (int num = 12; num >= 4; num--) {

			toNextCard: 
			for (int suit = 0; suit < 4; suit++) {
				int hi = suit * 13 + num; // highest card

				for (int o = 0; o < 5; o++) // offset from high card
					if (freqCards[hi - o] == 0)
						continue toNextCard;

				return new PokerHand(STRAIGHT_FLUSH, hi, hi - 1, hi - 2, hi - 3, hi - 4);
			}
		}

		// Four of a kind
		// Assumes no five of a kinds or higher
		if (freqValsSorted[0][1] == 4) {
			int n = freqValsSorted[0][0];

			freqVals[n] -= 4;
			var extra = Card.cardsToBytes(getLargestCards(freqCards, 1));

			return new PokerHand(QUADS, n, n, n, n, extra[0]);
		}

		// Full house
		if (freqValsSorted[0][1] == 3 && freqValsSorted[1][1] >= 2) {
			int hi = freqValsSorted[0][0]; // triple card

			int lo = freqValsSorted[1][0]; // double card

			for (int i = 2; i < freqValsSorted.length && freqValsSorted[i][1] >= 2; i++) {
				if (freqValsSorted[i][0] > freqValsSorted[lo][0]) {
					lo = freqValsSorted[i][0];
				}
			}

			return new PokerHand(BOAT, hi, hi, hi, lo, lo);
		}

		// Flush
		toNextSuit: 
		for (int suit = 0; suit < 4; suit++) {
			if (freqSuits[suit] < 5)
				continue toNextSuit;
			
			var cards = new ArrayList<Card>();
			
			for (int n = 0; n < 13 && cards.size() < 5; n++) {
				int c = suit * 13 + order[n];

				if (freqCards[c] > 0) {
					int toAdd = Math.min(freqCards[c], 5 - cards.size());

					for (int i = 0; i < toAdd; i++) {
						cards.add(new Card(c));
					}
				}
			}

			return new PokerHand(FLUSH, Card.cardsToBytes(cards));
		}

		// Straight
		if (freqVals[0] > 0 && freqVals[12] > 0 && freqVals[11] > 0 && freqVals[10] > 0 && freqVals[9] > 0)
			return new PokerHand(STRAIGHT, findNum(freqCards, 0), findNum(freqCards, 12), findNum(freqCards, 11),
					findNum(freqCards, 10), findNum(freqCards, 9));

		toNextCard: 
		for (int num = 12; num >= 4; num--) {
			int hi = num; // highest card

			for (int o = 0; o < 5; o++) // offset from high card
				if (freqVals[hi - o] == 0)
					continue toNextCard;

			int[] cards = new int[5];
			for (int o = 0; o < 5; o++)
				cards[o] = findNum(freqCards, hi - o);

			return new PokerHand(STRAIGHT, cards);
		}

		// Three of a kind
		if (freqValsSorted[0][1] == 3) {
			int n = freqValsSorted[0][0];

			freqVals[n] -= 3;
			var extra = Card.cardsToBytes(getLargestCards(freqCards, 2));

			return new PokerHand(TRIPLE, n, n, n, extra[0], extra[1]);
		}

		// Two pair
		if (freqValsSorted[0][1] == 2 && freqValsSorted[1][1] == 2) {
			int hi = freqValsSorted[0][0];
			int lo = freqValsSorted[1][0];

			freqVals[hi] -= 2;
			freqVals[lo] -= 2;
			var extra = Card.cardsToBytes(getLargestCards(freqCards, 1));

			return new PokerHand(TWO_PAIR, hi, hi, lo, lo, extra[0]);
		}

		// Pair
		if (freqValsSorted[0][1] == 2) {
			int n = freqValsSorted[0][0];

			freqVals[n] -= 2;
			var extra = Card.cardsToBytes(getLargestCards(freqCards, 3));

			return new PokerHand(PAIR, n, n, extra[0], extra[1], extra[2]);
		}

		// Card High
		var extra = Card.cardsToBytes(getLargestCards(freqCards, 5));
		return new PokerHand(CARD_HIGH, extra);
	}

	static ArrayList<Card> getLargestCards(int[] freqCards, int cardsToGet) {
		var cards = new ArrayList<Card>();

		for (int num = 0; num < 13 && cards.size() < cardsToGet; num++) {
			for (int suit = 0; suit < 4 && cards.size() < cardsToGet; suit++) {
				int c = suit * 13 + order[num];

				if (freqCards[c] > 0) {
					int toAdd = Math.min(freqCards[c], cardsToGet - cards.size());

					for (int i = 0; i < toAdd; i++) {
						cards.add(new Card(c));
					}
				}
			}
		}

		return cards;
	}

	static int findNum(int[] freqCards, int n) {

		for (int c = n; c < 52; c += 13) {

			if (freqCards[c] > 0) {
				return c;
			}
		}

		return -1;
	}

	/**
	 * Returns the cards in this hand
	 * 
	 * @return the cards in this hand
	 */
	public ArrayList<Card> getCards() {
		return cards;
	}

	/**
	 * Returns the cards in this hand followed by their numerical value Ex: JS AH
	 * (21)
	 * 
	 * @return the string representation of a Hand
	 */
	public String toString() {
		String output = "";

		for (Card card : cards) {
			output += card.toString() + ' ';
		}

		return output;
	}

	public byte[] toBytes() {
		byte[] bytes = new byte[cards.size()];

		for (int i = 0; i < cards.size(); i++) {
			bytes[i] = cards.get(i).toByte();
		}

		return bytes;
	}

	/**
	 * Adds the specified card to this hand
	 * 
	 * @param card the card to add
	 */
	public void addCard(Card card) {
		cards.add(card);
	}

	/**
	 * Returns the number of cards in this hand
	 * 
	 * @return the number of cards in this hand
	 */
	public int getNumCards() {
		return cards.size();
	}
}