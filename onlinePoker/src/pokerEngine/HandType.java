package pokerEngine;

public enum HandType {
	
	ROYAL_FLUSH(9),
	STRAIGHT_FLUSH(8),
	QUADS(7),
	BOAT(6),
	FLUSH(5),
	STRAIGHT(4),
	TRIPLE(3),
	TWO_PAIR(2),
	PAIR(1),
	CARDHIGH(0);
	
	private int rank;
	
	private HandType(int rank) {
		this.rank = rank;
	}
}
