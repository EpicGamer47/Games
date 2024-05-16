package TwoPlayer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class AllMoves {
	public static AllMoves moves = new AllMoves();
	
	int[][] single;
	int[][] repeat;
	int[][] attack;
	
	private AllMoves() {
		single = Arrays.stream(Piece.values())
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
				.filter(p -> !p.canMoveAttack)
				.map(p -> p.attack)
				.flatMap(a -> Arrays.stream(a))
				.distinct()
				.toArray(int[][]::new);
	}
}
