package singlePlayer;

import static common.Piece.*;

import java.util.EnumMap;
import common.Board;
import common.Piece;
import common.Ranker;

public class CoverageRanker3 implements Ranker {
	double mult;
	
	public CoverageRanker3() { 
		mult = 1;
	}
	
	public CoverageRanker3(double mult) {
		this.mult = mult;
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

	// quadratic dropoff per covered square
	// cubic up to 27, the max amount of squares a queen covers
	@Override
	public double rank(Board b, int n, int l) {
		final double standard = 10 / 7.0;
		
		if (b.board[n + l * 8] == KING || b.board[n + l * 8] == null)
			return 0;
		
		long i = 1L << (n + l * 8);
		boolean side = (b.white & i) != 0;
		
		int c = Long.bitCount(b.coverage(n, l, side));
		double dxInt = 46.8;
		
		double val = -c * (c - dxInt) * (c + dxInt) / (dxInt * dxInt);
		
		if ((b.white & i) != 0)
			return  standard * val;
		else
			return -standard * val;
	}
}