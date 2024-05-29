package common;

public class MultiRanker implements Ranker {

	private final Ranker[] rankers;

	public MultiRanker(Ranker... rankers) {
		this.rankers = rankers;
	}
	
	@Override
	public double rank(Board b) {
		double out = 0;
		
		for (var r : rankers) {
			out += r.rank(b);
		}
		
		return out;
	}
	
	@Override
	public double rank(Board b, int n, int l) {
		double out = 0;
		
		for (var r : rankers) {
			out += r.rank(b, n, l);
		}
		
		return out;
	}
}
