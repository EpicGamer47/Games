package singlePlayer;

import common.Board;
import common.Ranker;

public class MultiRanker implements Ranker {

	private final Ranker[] rankers;

	public MultiRanker(Ranker... rankers) {
		this.rankers = rankers;
	}
	
	@Override
	public double rank(Board b, int n, int l) {
		int out = 0;
		
		for (var r : rankers) {
			out += r.rank(b, n, l);
		}
		
		return out;
	}
}
