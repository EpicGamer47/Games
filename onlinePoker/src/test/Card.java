package test;

import processing.core.PApplet;
import processing.core.PImage;

public class Card {
	
	private boolean isFaceDown;
	
	// 1 = ace
	// 2-10 = numbers
	// 11-13 = royals (jack, queen, king)
	private int value;
	
	// 1 = clubs
	// 2 = spades
	// 3 = hearts
	// 4 = diamonds
	private int suit;
	
	public Card(int suit, int value, boolean isFaceDown) {
		this.suit = suit;
		this.value = value;
		this.isFaceDown = isFaceDown;
	}
	
	public int getSuit() {
		return suit;
	}
	
	public int getValue() {
		return value;
	}
	
	public boolean isFaceDown() {
		return isFaceDown;
	}
	
	public void setFaceDown(boolean fd) {
		isFaceDown = fd;
	}
	
	//Will just get the cards from mr. horn
	public void drawCard(PApplet a, float x, float y) {
		if (isFaceDown) {
			drawFaceDownCard(a, x, y);
			return;
		}
		String cardPath = "cards/";
		if (value == 13)
			cardPath += "king";
		else if (value == 12)
			cardPath += "queen";
		else if (value == 11)
			cardPath += "jack";
		else if (value == 1)
			cardPath += "ace";
		else
			cardPath += value;
		if (suit == 1)
			cardPath += "clubs";
		else if (suit == 2)
			cardPath += "spades";
		else if (suit == 3)
			cardPath += "hearts";
		else
			cardPath += "diamonds";
		cardPath += ".GIF";
		PImage cardImage = a.loadImage(cardPath);
		cardImage.resize(100, 0);
		a.image(cardImage, x, y);
	}
	
	private void drawFaceDownCard(PApplet a, float x, float y) {
		a.fill(255);
		a.rect(x, y, 100, 150);
		a.stroke(0, 0, 255);
		for (int i = 50; i <= 150; i += 10) {
			a.line(x, y + i, x + 100, y + i - 50);
		}
	}
}
