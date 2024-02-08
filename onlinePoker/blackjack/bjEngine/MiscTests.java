package bjEngine;

import java.util.ArrayList;

public class MiscTests {
	
	public static void main(String[] args) throws InterruptedException {
		split();
	}
	
	public static void basic() {
		Blackjack bj = new Blackjack(1000);
		int i = 0;
		int maxPlay = 0;
		double maxMoney = 1000;
		
		while (bj.getPlayersMoney() > 0) {
			bj.placeBetAndDealCards(Math.min(bj.getPlayersMoney(), 100));
			
			if (bj.getPlayersHand() != null) {
				while (bj.canHit() && bj.getPlayersHand().getValue() < 17) {
		    		bj.hit();	
		    	}
				System.out.println("\nPlayers's hand: " + bj.getPlayersHand().toString());
				
				bj.playDealersHand();
				System.out.println("\nDealer's hand: " + bj.getDealersHand().toString());
				
				System.out.println("\nResult: " + bj.getResult(0));
				bj.resolveBetsAndReset();
				System.out.println("\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
			}
			
			if (maxMoney < bj.getPlayersMoney()) {
				maxMoney = bj.getPlayersMoney();
				maxPlay = i;
			}
			i++;
		}
		
		System.out.println("Games Played: " + i);
		System.out.println("Highest Game: " + maxPlay);
		System.out.println("Max money: " + maxMoney);
	}
	
	public static void split() {
		Blackjack bj = new Blackjack(1000);
		bj.placeBetAndDealCards(100);
		
		ArrayList<Card> playerHand = bj.getPlayersHand().getCards();
		ArrayList<Card> dealerHand = bj.getDealersHand().getCards();
		ArrayList<Card> shoe = bj.getShoe().getCards();
		
		playerHand.clear(); playerHand.add(new Card("D", 1)); playerHand.add(new Card("S", 1)); // player has 2 aces
		dealerHand.clear(); dealerHand.add(new Card("D", 1)); dealerHand.add(new Card("S", 13)); // dealer has blackjack
		shoe.add(new Card("H", 10)); shoe.add(new Card("C", 10)); // shoe has 2 10s on top
		
		// player should have 2 21s, which should both be less than the dealer blackjack.
		
		System.out.println("canSplit(): " + bj.canSplit()); //true
		if (!bj.canSplit()) return;
		bj.split();
		
		System.out.println("getResult() hand 1: " + bj.getResult(0)); // -1
		System.out.println("getResult() hand 2: " + bj.getResult(1)); // -1
		
		bj.resolveBetsAndReset();
		bj.placeBetAndDealCards(100);
		System.out.println();
		

		
		playerHand = bj.getPlayersHand().getCards();
		dealerHand = bj.getDealersHand().getCards();
		shoe = bj.getShoe().getCards();
		
		playerHand.clear(); playerHand.add(new Card("D", 1)); playerHand.add(new Card("S", 1)); // player has 2 aces
		dealerHand.clear(); dealerHand.add(new Card("D", 1)); dealerHand.add(new Card("S", 13)); // dealer has blackjack
		shoe.add(new Card("H", 10)); shoe.add(new Card("C", 10)); // shoe has 2 aces on top
		
		// player should have 2 soft 12s, which should be unhittable.
		
		System.out.println("canSplit(): " + bj.canSplit()); //true
		if (!bj.canSplit()) return;
		bj.split();

		System.out.println("canHit() hand 1: " + bj.canHit()); //false
		if (bj.canHit()) return;
		bj.nextHand();
		
		System.out.println("canHit() hand 2: " + bj.canHit()); //false
		if (bj.canHit()) return;
		
		bj.resolveBetsAndReset();
		bj.placeBetAndDealCards(100);
		System.out.println();
		
		
		
		playerHand = bj.getPlayersHand().getCards();
		dealerHand = bj.getDealersHand().getCards();
		shoe = bj.getShoe().getCards();
		
		playerHand.clear(); playerHand.add(new Card("D", 10)); playerHand.add(new Card("S", 11)); // player has 2 10s
		dealerHand.clear(); dealerHand.add(new Card("D", 1)); dealerHand.add(new Card("S", 13)); // dealer has blackjack
		for (int i = 0; i < 16; i++) shoe.add(new Card("C", 12)); // shoe has 16 tens on top
		
		// player should be able to keep splitting until they hit 4 hands
		
		do {
			bj.split();
		} while (bj.canSplit() && bj.nextHand());
		System.out.println("Total hands: " + bj.getPlayerHands().size());
	}
}
