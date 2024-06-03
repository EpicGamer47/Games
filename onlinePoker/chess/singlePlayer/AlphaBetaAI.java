package singlePlayer;


import java.util.ArrayList;
import java.util.Arrays;

import common.AI;
import common.Board;
import common.MultiRanker;
import common.Piece;
import common.Ranker;

public class AlphaBetaAI extends AI {
	private final Pair posI = new Pair(Double.POSITIVE_INFINITY);
	private final Pair negI = new Pair(Double.NEGATIVE_INFINITY);
	
	public static int iterations = 0;
	private static ArrayList<Pair> pairs = new ArrayList<Pair>();
	
	private static final Ranker r = new MultiRanker(
			new CoverageRanker3(1),
			new ValueRanker(2)
			);
//	private static final Ranker r = new MultiRanker(
//			new CoverageRanker(1.75),
//			new ValueRanker(1.5)
//			);
//	private static final Ranker r = new MultiRanker(
//			new CoverageRanker(1),
//			new CoverageRanker(.5),
//			new ValueRanker(1.5)
//			);
	private int depth;
	
	public AlphaBetaAI(Board b) {
		super(b);
		depth = 5;
	}
	
	public AlphaBetaAI(Board b, int depth) {
		super(b);
		this.depth = depth;
	}
	
	@Override
	public boolean makeAMove() {
		var old = b;
		b = new Board(b);
		var move = alphaBeta(depth, negI, posI, b.turn);
		b = old;
		// calc all on new board to keep drawing alive
		
		System.out.println("eval: " + move.val + ", iter: " + iterations);
		int esign = b.turn ? -1 : 1;
		pairs.sort((o, p) -> esign * Double.compare(o.val, p.val));
		System.out.println(pairs);
		pairs.clear();
		iterations = 0;
		
		if (move.move == null)
			return false;
		
		return b.move(move.move[0], move.move[1], move.move[2], move.move[3]) != null;
	}
	
	@SuppressWarnings("unused")
	private class Pair {
		double val;
		int[] move;
		Piece p;
		Pair next;
		//TODO
		//TODO
		//TODO
		boolean isCapture;
		
		public Pair(double val) {
			this.val = val;
		}
		
		public Pair(double val, int[] move, Piece p) {
			this.val = val;
			this.move = move;
			this.p = p;
		}
		
		public Pair(Pair next, int[] move, Piece p) {
			this.val = next.val;
			this.move = move;
			this.p = p;
			
			this.next = next;
		}
		
		public Pair(Pair next, int[] move, Piece p, boolean isCapture) {
			this.val = next.val;
			this.move = move;
			this.p = p;
			this.isCapture = isCapture;
			
			this.next = next;
		}

		public static Pair max(Pair a, Pair b) {
			return a.val > b.val ? a : b;
		}
		
		public static Pair min(Pair a, Pair b) {
			return a.val < b.val ? a : b;
		}
		
		@Override
		public String toString() {
			return "(" + val + ", " + moveToString() + ")";
		}
		
		private String moveToString() {
			if (move == null)
				return "";
			
			char[] out = new char[4];
			out[0] = (char) (move[0] + 'a');
			out[1] = (char) (move[1] + '1');
			out[2] = (char) (move[2] + 'a');
			out[3] = (char) (move[3] + '1');
			
			String prefix = p.getLetter() + (isCapture ? "x" : "");
			
			if (next.move == null)
				return prefix + String.valueOf(out);
			
			return prefix + String.valueOf(out) + ", " + next.moveToString();
		}
	}
	
	private Pair alphaBeta(int depth, Pair aa, Pair bb, boolean turn) {
		if (++iterations == Integer.highestOneBit(iterations))
			System.out.println("Reached " + iterations);
		
		if (b.movesSinceLastCapture + depth / 2 >= 50) {
			return new Pair(0);
		}
		
		if (depth == 0) {
			return new Pair(r.rank(b));
		}
		
		var value = new Pair(turn ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
		var moves = b.getAllMoves(turn);
		
//		var error = new int[] {0, 3, -1, 2};
//		for (var m : moves) {
//			if (Arrays.equals(m, error))
//				b.getAllMoves(turn);
//		}
		
		if (moves.size() == 0) {
			if ((b.coverage(!turn) & b.findKing(turn)) == 0) // if stalemate
				return new Pair(0);
			
			return value;
		}
		
		var it = moves.listIterator();
		if (turn) {
			for (var m = moves.get(0); it.hasNext(); m = it.next()) {
//				try {
//					b.toString();
//				}
//				catch (Exception e) {
//					throw new RuntimeException(Arrays.toString(it.previous()));
//				}
				
				if (!Board.isValidIndex(m[2], m[3]))
					continue;
				
				var p = b.board[m[0] + m[1] * 8];
				var isCapture = b.board[m[2] + m[3] * 8] != null;
				var d = b.move(m[0], m[1], m[2], m[3], turn);
				
//				if (Long.bitCount(b.white) + Long.bitCount(b.black) != Long.bitCount(b.exists))
//					throw new RuntimeException("fuck you");
				
//				if (d == null)
//					throw new RuntimeException(Arrays.toString(m) + "\n" + 
//							(moves.stream()
//									.map(a -> Arrays.toString(a) + ", ")
//									.reduce("", (curr, next) -> curr + next)) + "\n" + 
//							Board.toString(b.white) + "\n" + 
//							Board.toString(b.black) + "\n" + 
//							b.toString());
				
				var next = alphaBeta(depth - 1, aa, bb, false);
				next = new Pair(next, m, p, isCapture);
				b.noPushRevert();
				
				if (this.depth == depth)
					pairs.add(next);
				
				value = Pair.max(value, next);
				aa = Pair.max(aa, value);
				
				if (value.val >= bb.val)
					break;
			}
			
			return value;
		}
		else {
			for (var m = moves.get(0); it.hasNext(); m = it.next()) {
//				try {
//					b.toString();
//				}
//				catch (Exception e) {
//					throw new RuntimeException(Arrays.toString(it.previous()));
//				}
				
				var p = b.board[m[0] + m[1] * 8];
				var isCapture = b.board[m[2] + m[3] * 8] != null;
				var d = b.move(m[0], m[1], m[2], m[3], turn);
				
//				if (Long.bitCount(b.white) + Long.bitCount(b.black) != Long.bitCount(b.exists))
//					throw new RuntimeException("fuck you");
				
//				if (d == null)
//					throw new RuntimeException(Arrays.toString(m) + "\n" + 
//							(moves.stream()
//									.map(a -> Arrays.toString(a) + ", ")
//									.reduce("", (curr, next) -> curr + next)) + "\n" + 
//							Board.toString(b.white) + "\n" + 
//							Board.toString(b.black) + "\n" + 
//							b.toString());
				
				var next = alphaBeta(depth - 1, aa, bb, true);
				next = new Pair(next, m, p, isCapture);
				b.noPushRevert();
				
				if (this.depth == depth)
					pairs.add(next);
				
				value = Pair.min(value, next);
				bb = Pair.min(bb, value);
				
				if (value.val <= aa.val)
					break;
			}
			
			return value;
		}
	}
}
