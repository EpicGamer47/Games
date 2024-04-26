package pokerEngine;

public enum HandType implements Comparable<HandType> {
	
	ROYAL_FLUSH(9, "Royal Flush"),
	STRAIGHT_FLUSH(8, "Straight Flush"),
	QUADS(7, "Quads"),
	BOAT(6, "Full House"),
	FLUSH(5, "Flush"),
	STRAIGHT(4, "Straight"),
	TRIPLE(3, "Triplets"),
	TWO_PAIR(2, "Two Pair"),
	PAIR(1, "Pair"),
	CARD_HIGH(0, "Card High");

	
	private int rank;
	private String name;
	
	private HandType(int rank, String name) {
		this.rank = rank;
		this.name = name;
	}
	
	public int getRank() {
		return rank;
	}

	@Override
	public String toString() {
		return name;
	}
}
