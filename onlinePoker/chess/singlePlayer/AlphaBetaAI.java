package singlePlayer;

import static common.Move.DOUBLE;
import static common.Piece.KING;
import static common.Piece.PAWN;
import static common.Piece.ROOK;

import java.util.Arrays;

import common.AI;
import common.Board;
import common.Move;
import common.Ranker;

public class AlphaBetaAI extends AI {
	static final Ranker r = new CoverageRanker();
	
	public AlphaBetaAI(Board b) {
		super(b);
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
