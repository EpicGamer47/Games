package singlePlayer;

import common.AI;
import common.Board;

public class RandomAI implements AI {
	Board b;
	
	public RandomAI(Board b) {
		this.b = b;
	}
	
	public void makeAMove() {
		var moves = b.getAllMoves(b.turn);
		
		if (moves.size() == 0)
			return;
		
		var move = moves.get((int) (Math.random() * moves.size()));
		
		b.move(move[0], move[1], move[2], move[3]);
	}
}
