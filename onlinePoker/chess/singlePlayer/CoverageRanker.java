package singlePlayer;

import static common.Piece.*;

import java.util.EnumMap;
import common.Board;
import common.Piece;
import common.Ranker;

public class CoverageRanker implements Ranker {
	double mult;
	
	public CoverageRanker() { 
		mult = 1;
	}
	
	public CoverageRanker(double mult) {
		this.mult = mult;
	}
	
	public static EnumMap<Piece, Double> vals;
	
	static {
		vals = new EnumMap<Piece, Double>(Piece.class);
		
		vals.put(PAWN, 1.5);
		vals.put(KNIGHT, 1.3);
		vals.put(BISHOP, .9);
		vals.put(ROOK, 1.0);
		vals.put(QUEEN, .85);
		vals.put(KING, 0.0);
	}
	
//	public static EnumMap<Piece, Double> maxMoves;
//	
//	static {
//		maxMoves.put(PAWN, 2.0);
//		maxMoves.put(KNIGHT, 8.0);
//		maxMoves.put(BISHOP, 13.0);
//		maxMoves.put(ROOK, 14.0);
//		maxMoves.put(QUEEN, 27.0);
//		maxMoves.put(KING, 8.0);
//	}

	// note: using coverage on a king is arguably horrible
	@Override
	public double rank(Board b, int n, int l) {
		final double standard = 100 / 7.0;
		
		if (b.board[n + l * 8] == KING || b.board[n + l * 8] == null)
			return 0;
		
		long i = 1L << (n + l * 8);
		boolean side = (b.white & i) != 0;
		
		int c = Long.bitCount(b.coverage(n, l, side));
		
		if ((b.white & i) != 0)
			return  c * standard * mult * vals.get(b.board[n + l * 8]);
		else
			return -c * standard * mult * vals.get(b.board[n + l * 8]);
	}
}