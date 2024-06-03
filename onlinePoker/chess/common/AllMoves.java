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
				.map(a -> new HashableArray(a))
				.distinct()
				.map(a -> a.array)
				.toArray(int[][]::new);
		repeat = Arrays.stream(Piece.values())
				.map(p -> p.repeat)
				.flatMap(a -> Arrays.stream(a))
				.map(a -> new HashableArray(a))
				.distinct()
				.map(a -> a.array)
				.toArray(int[][]::new);
		attack = Arrays.stream(Piece.values())
				.map(p -> p.attack)
				.flatMap(a -> Arrays.stream(a))
				.map(a -> new HashableArray(a))
				.distinct()
				.map(a -> a.array)
				.toArray(int[][]::new);
	}
	
	private AllMoves() {
		
	}
	
	private static class HashableArray {
		int[] array;
		
		@SuppressWarnings("unused")
		public HashableArray() { }
		
		public HashableArray(int[] array) {
			this.array = array;
		}
		
		@Override
		public int hashCode() {
			return Arrays.hashCode(array);
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (o == this)
				return true;
			if (!(o instanceof HashableArray))
				return false;
			
			var other = (HashableArray) o;
			
			return Arrays.equals(this.array, other.array);
		}
	}
}
