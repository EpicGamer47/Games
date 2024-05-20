package common;

import java.util.Arrays;

public class AllMoves {
	public static int[][] single;
	public static int[][] repeat;
	public static int[][] attack;
	
	static {
		single = Arrays.stream(Piece.values())
				.filter(p -> p != Piece.KING && p.canMoveAttack)
				.map(p -> p.single)
				.flatMap(a -> Arrays.stream(a))
				.distinct()
				.toArray(int[][]::new);
		repeat = Arrays.stream(Piece.values())
				.map(p -> p.repeat)
				.flatMap(a -> Arrays.stream(a))
				.distinct()
				.toArray(int[][]::new);
		attack = Arrays.stream(Piece.values())
				.map(p -> p.attack)
				.flatMap(a -> Arrays.stream(a))
				.distinct()
				.toArray(int[][]::new);
	}
	
	private AllMoves() {
		
	}
}
