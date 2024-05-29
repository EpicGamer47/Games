package singlePlayer;

import static common.Piece.*;

import java.util.EnumMap;
import common.Board;
import common.Piece;
import common.Ranker;

public class CoverageRanker2 implements Ranker {
	double mult;
	
	public CoverageRanker2() { 
		mult = 1;
	}
	
	public CoverageRanker2(double mult) {
		this.mult = mult;
	}
	
	// knight weightings are surprisingly useful?
	public static double[] posWeights = {
			-3.0, -2.0, -1.0, -1.0, -1.0, -1.0, -2.0, -3.0,
            -2.0, -2.0,  0.0,  0.0,  0.0,  0.0, -2.0, -2.0,
            -1.0,  0.0,  1.0,  1.5,  1.5,  1.0,  0.0, -1.0,
            -1.0,  0.5,  1.5,  2.0,  2.0,  1.5,  0.5, -1.0,
            -1.0,  0.0,  1.5,  2.0,  2.0,  1.5,  0.0, -1.0,
            -1.0,  0.5,  1.0,  1.5,  1.5,  1.0,  0.5, -1.0,
            -2.0, -2.0,  0.0,  0.0,  0.0,  0.0, -2.0, -2.0,
            -3.0, -2.0, -1.0, -1.0, -1.0, -1.0, -2.0, -3.0};

	// note: using coverage on a king is arguably horrible
	@Override
	public double rank(Board b, int n, int l) {
		final double standard = 1 / 8.0;
		
		if (b.board[n + l * 8] == KING || b.board[n + l * 8] == null)
			return 0;
		
		boolean side = (b.white & (1L << (n + l * 8))) != 0;
		
		long c = b.coverage(n, l, side);
		
		double val = 0;
		
		for (int ind = 0; ind < 64; ind++) {
			long i = 1L << ind;
			
			if ((c & i) != 0) {
				if ((b.white & i) != 0) {
					val += posWeights[n + l * 8];
				}
				else {
					val -= posWeights[n + (7 - l) * 8];
				}
			}
		}
		
		if (side)
			return  standard * mult * val;
		else
			return -standard * mult * val;
	}
}