package singlePlayer;

import static common.Piece.*;

import java.util.EnumMap;
import common.Board;
import common.Piece;
import common.Ranker;

public class BasicCoverageRanker implements Ranker {
	public static EnumMap<Piece, Double> maxMoves;
	
	static {
		maxMoves.put(PAWN, 2.0);
		maxMoves.put(KNIGHT, 8.0);
		maxMoves.put(BISHOP, 13.0);
		maxMoves.put(ROOK, 14.0);
		maxMoves.put(QUEEN, 27.0);
		maxMoves.put(KING, 8.0);
	}

	// TODO derive something actually useful for this
	@Override
	public double rank(Board b, int n, int l) {
		long i = 1L << (n + l * 8);
		boolean side = (b.white & i) != 0;
		
		int c = Long.bitCount(b.coverage(n, l, side));
		
		if ((b.white & i) != 0)
			return  c / maxMoves.get(b.board[n + l * 8]);
		else
			return -1 / maxMoves.get(b.board[n + l * 8]);
	}
}