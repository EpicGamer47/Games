package singlePlayer;

import common.AI;
import common.Board;

public class RandomAI implements AI {
	Board b;
	
	public RandomAI(Board b) {
		this.b = b;
	}
	
	@Override
	public boolean makeAMove() {
		var moves = b.getAllMoves(b.turn);
		
		if (moves.size() == 0)
			return false;
		
		var move = moves.get((int) (Math.random() * moves.size()));
		
		b.move(move[0], move[1], move[2], move[3]);
//		b.move(4, 7, 5, 6);
		
		return true;
	}
}
