package test;

import static pokerEngine.HandType.*;

import java.util.Arrays;
import java.util.Collections;

import pokerEngine.Hand;
import pokerEngine.PokerHand;

public class Misc {
	public static void main(String... args) {
		Hand[] hands = { 
				new Hand(0, 1, 2, 3, 4),
				new Hand(0, 12, 25, 38, 51),
				new Hand(0, 3, 16, 29, 42),
				new Hand(0, 5, 18, 31, 44),
				new Hand(0, 11, 24, 37, 50),
				new Hand(0, 1, 14, 2, 15),
				new Hand(0, 1, 14, 3, 15),
				new Hand(0, 1, 14, 2, 2),
				new Hand(0, 1, 14, 28, 2),
				new Hand(0, 1, 14, 16, 30),
				new Hand(0, 1, 2, 3, 5),
				new Hand(1, 1, 14, 27, 40),
				new Hand(2, 2, 15, 28, 41),
				new Hand(3, 3, 16, 29, 42),
				new Hand(0, 1, 14, 28, 42),
				new Hand(0, 1, 14, 15, 28),
				new Hand(0, 1, 14, 14, 15),
				new Hand(0, 1, 2, 2, 15),
				new Hand(0, 1, 14, 14, 28),
				new Hand(0, 0, 0, 13, 15),
				new Hand(0, 1, 2, 15, 28),
				new Hand(0, 1, 14, 14, 2),
				new Hand(0, 1, 14, 14, 28),
				new Hand(13, 1, 2, 2, 2),
				new Hand(0, 1, 2, 3, 6),
				new Hand(0, 9, 22, 35, 48),
				new Hand(0, 10, 23, 36, 49),
				new Hand(0, 13, 26, 39, 2),
				new Hand(0, 2, 15, 28, 41),
				new Hand(0, 1, 14, 14, 2),
				new Hand(0, 12, 11, 10, 9),
				new Hand(1, 2, 3, 4, 18),
				new Hand(1, 1, 2, 2, 2),
				new Hand(12, 5, 5, 12, 12),
				new Hand(12, 5, 5, 5, 12),
				new Hand(11, 5, 5, 5, 11)
		};

		PokerHand[] pHands = new PokerHand[hands.length];
		
		for (int i = 0; i < hands.length; i++) {
			pHands[i] = hands[i].getBestHand();
		}
		
		Arrays.sort(pHands);
		
		for (var h : pHands) {
			System.out.println(h.toString());
		}
	}
}
