package common;

public interface Ranker {
	public default double rank(Board b) {
		double total = 0;
		
		for (int l = 0; l < 8; l++)
			for (int n = 0; n < 8; n++)
				total += rank(b, n, l);
		
		return total;
	}
	
	public double rank(Board b, int n, int l);
}
