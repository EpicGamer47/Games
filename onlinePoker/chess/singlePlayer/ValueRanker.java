package singlePlayer;

import static common.Piece.*;

import java.util.EnumMap;

import common.Board;
import common.Piece;
import common.Ranker;

public class ValueRanker implements Ranker {
	double mult;
	
	public ValueRanker() { 
		mult = 1;
	}
	
	public ValueRanker(double mult) {
		this.mult = mult;
	}
	
	public static EnumMap<Piece, Double> vals;
	
	static {
		vals = new EnumMap<Piece, Double>(Piece.class);
		
		vals.put(PAWN, 10.0);
		vals.put(KNIGHT, 30.0);
		vals.put(BISHOP, 30.0);
		vals.put(ROOK, 50.0);
		vals.put(QUEEN, 90.0);
		vals.put(KING, 1000.0);
	}
	
	@Override
	public double rank(Board b, int n, int l) {
		if (b.board[n + l * 8] == null)
			return 0;
		
		long i = 1L << (n + l * 8);
		double val = vals.get(b.board[n + l * 8]);
		
		if ((b.white & i) != 0)
			return  val * mult;
		else
			return -val * mult;
	}
}
