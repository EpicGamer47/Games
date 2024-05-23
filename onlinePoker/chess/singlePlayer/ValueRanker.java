package singlePlayer;

import static common.Piece.*;

import java.util.EnumMap;

import common.Board;
import common.Piece;
import common.Ranker;

public class ValueRanker implements Ranker {
	public static EnumMap<Piece, Double> vals;
	
	static {
//		vals.put(PAWN, 10.0);
//		vals.put(KNIGHT, 30.0);
//		vals.put(BISHOP, 30.0);
//		vals.put(ROOK, 50.0);
//		vals.put(QUEEN, 90.0);
//		vals.put(KING, 1000.0);
		
		// i looked up "alpha zero piece weights" and got this
		vals.put(PAWN, 10.0);
		vals.put(KNIGHT, 30.5);
		vals.put(BISHOP, 33.3);
		vals.put(ROOK, 56.3);
		vals.put(QUEEN, 95.0);
		vals.put(KING, 1000.0);
	}
	
	@Override
	public double rank(Board b, int n, int l) {
		long i = 1L << (n + l * 8);
		
		double val = vals.get(b.board[n + (7 - l) * 8]);
		
		if ((b.white & i) != 0)
			return -val;
		else
			return -val;
	}
}
