package common;

import static common.Moves.*;
import static common.Piece.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Board {
	private static Piece row1[] = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};

	public static int MOVE_NUM = 0x000F; // shift 0
	public static int MOVE_LETTER = 0x00F0; // shift 4
	public static int MOVE_NUM_2 = 0x0F00; // shift 8 
	public static int MOVE_LETTER_2 = 0xF000; // shift 12
	
	
	public Piece[] board;
	public long white;
	public long black;
	public long exists;
	
	public boolean turn; // true = white, false = black
	public boolean rightToCastleK_W;
	public boolean rightToCastleQ_W;
	public boolean rightToCastleK_B;
	public boolean rightToCastleQ_B;
	public int lastDouble;
	public int movesSinceLastCapture;
	public int[] lastMove;
	
	public Board() {
		reset();
	}
	
	public Board(Board b) {
		board = b.board.clone();
		white = b.white;
		black = b.black;
		exists = b.exists;
		
		turn = b.turn;
		rightToCastleK_W = b.rightToCastleK_W;
		rightToCastleQ_W = b.rightToCastleQ_W;
		rightToCastleK_B = b.rightToCastleK_B;
		rightToCastleQ_B = b.rightToCastleQ_B;
		
		lastDouble = b.lastDouble;
		movesSinceLastCapture = b.movesSinceLastCapture;
		lastMove = b.lastMove;
	}

	private void reset() {
		board = new Piece[8 * 8];
		
		for (int i = 0; i < 8; i++) {
			board[i] = row1[i];
			board[1 * 8 + i] = Piece.PAWN;
			board[6 * 8 + i] = Piece.PAWN;
//			board[3 * 8 + i] = Piece.PAWN;
//			board[4 * 8 + i] = Piece.PAWN;
			board[7 * 8 + i] = row1[i];
		}
		
		black = 0xFFFF_0000_0000_0000L;
		white = 0x0000_0000_0000_FFFFL;
//		black = 0xFF00_00FF_0000_0000L;
//		white = 0x0000_0000_FF00_00FFL;
		exists = black | white;
		
		resetFlags();
	}
	
	private void resetFlags() {
		turn = true;
		rightToCastleK_W = true;
		rightToCastleQ_W = true;
		rightToCastleK_B = true;
		rightToCastleQ_B = true;
		lastDouble = -1;
		movesSinceLastCapture = 0;
		lastMove = null;
	}
	
	public List<int[]> getAllMoves(boolean side) {
		ArrayList<int[]> out = new ArrayList<int[]>();
		
		for (int n = 0; n < 8; n++) {
			for (int l = 0; l < 8; l++) {
				out.addAll(getAllMoves(n, l, side));
			}
		}
		
		return out;
	}
	
	public List<int[]> getAllMoves(int n, int l) {
		long i = 1L << (n + l * 8);
		
		return getAllMoves(n, l, (white & i) != 0);
	}
	
	public List<int[]> getAllMoves(int n, int l, boolean side) {
		long i = 1L << (n + l * 8);
		
		long enemy = side ? black : white;
		long hero = side ? white : black;
		
		if ((hero & i) == 0)
			return Collections.emptyList();
		
		ArrayList<int[]> out = new ArrayList<int[]>();

		int sign = turn ? 1 : -1;
		Piece p = board[l * 8 + n];
		
		for (int[] d : p.single) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			if (isValidSpace(p, dN, dL, enemy) &&
					checkMove(n, l, dN, dL, NORMAL))
				out.add(new int[] {n, l, dN, dL});
		}
		
		for (int[] d : p.repeat) {
			int iN = n + d[0], iL = l + d[1] * sign;
			
			long k = 1L << (iN + iL * 8);
			
			while ((exists & k) == 0 && 
					checkMove(n, l, iN, iL, NORMAL)) {
				out.add(new int[] {n, l, iN, iL});
				iN += d[0];
				iL += d[1] * sign;
				k = 1L << (iN + iL * 8);
			}
			
			if ((enemy & k) != 0  && checkMove(n, l, iN, iL, NORMAL))
				out.add(new int[] {n, l, iN, iL});
		}
		
		for (int[] d : p.attack) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			if (isValidCapture(dN, dL, enemy) && checkMove(n, l, dN, dL, NORMAL))
				out.add(new int[] {n, l, dN, dL});	
		}
		
		if (p == PAWN) {
			if (isDouble(n, l, n, l + 2 * sign) && checkMove(n, l, n, l + 2 * sign, DOUBLE))
				out.add(new int[] {n, l, n, l + 2 * sign});	
			
			if (isEnPassant(n, l, n + 1, l + 1 * sign))
				out.add(new int[] {n, l, n + 1, l + 1 * sign});
			
			if (isEnPassant(n, l, n - 1, l + 1 * sign))
				out.add(new int[] {n, l, n - 1, l + 1 * sign});
		}
		
		if (p == KING) {
			if (isCastleKing(n, l, 6, l) && checkMove(n, l, 6, l, CASTLE_KING)) // castle king
				out.add(new int[] {n, l, 6, l});
			
			if (isCastleQueen(n, l, 2, l) && checkMove(n, l, 2, l, CASTLE_QUEEN)) // castle queen
				out.add(new int[] {n, l, 2, l});
		}
		
		return out;
	}

	
	@SuppressWarnings({ "exports", "unchecked" })
	public List<Point>[] getAllEndPoints(int n, int l){
		var in = getAllMoves(n, l);
		
		long enemy = turn ? black : white;
		
		var endPoints = in;
		
		var captures = endPoints.stream()
				.map(a -> new Point(a[2], a[3]))
				.filter(p -> {
					long j = 1L << (p.x + p.y * 8);
					
					return (enemy & j) != 0 || 
							(board[n + l * 8] == PAWN && 
							l == (turn ? 4 : 3) &&
							isEnPassant(n, l, p.x, p.y));
				})
				.collect(Collectors.toList());
		
		var normal = endPoints.stream()
				.map(a -> new Point(a[2], a[3]))
				.filter(p -> !captures.contains(p))
				.collect(Collectors.toList());
		
		return new List[] {
				normal, 
				captures
					};			
	}
	
	public boolean move(int n, int l, int dN, int dL) {
		var move = isValidMove(n, l, dN, dL);
		
		if (move == null) {
			System.out.println(String.format("ILLEGALFAIL %d %d %d %d", n, l, dN, dL));
			return false;
		}
		
		if (!checkMove(n, l, dN, dL, move)) {
			System.out.println(String.format("CHECKFAIL %d %d %d %d", n, l, dN, dL));
			return false;
		}
		
		long sk1 = 0x7L << (l * 8 + 4); // include king & 2 spaces
		long sq1 = 0x7L << (l * 8 + 1);
		
		long i = 1L << (n + l * 8);
		long j = 1L << (dN + dL * 8);
//		long king = findKing(turn);
		
		long enemy = turn ? black : white;
		
//		long c = coverage(!turn, enemy, exists);
		
		if (board[dL * 8 + dN] != null || board[l * 8 + n] == PAWN)
			movesSinceLastCapture = 0;
		
		switch (move) {
		case DOUBLE:
		case NORMAL:
			board[dL * 8 + dN] = board[l * 8 + n];
			board[l * 8 + n] = null;
			
			exists &= ~i;
			exists |= j;
			
			if (turn) {
				black &= ~j;
				white &= ~i;
				white |= j;
			}
			else {
				white &= ~j;
				black &= ~i;
				black |= j;
			}	
			
			break;
		case EN_PASSANT: // not gonna bother with discovered check detection
			board[dL * 8 + dN] = board[l * 8 + n];
			board[l * 8 + n] = null;
			board[l * 8 + dN] = null;
			
			long k1 = 1L << (l * 8 + dN); // en-passanted pawn
			
			exists &= ~i;
			exists &= ~k1;
			exists |= j;
			
			if (turn) {
				black &= ~k1;
				white &= ~i;
				white |= j;
			}
			else {
				white &= ~k1;
				black &= ~i;
				black |= j;
			}
			
			break;
		case CASTLE_KING:
			board[l * 8 + n] = null;
			board[dL * 8 + dN] = null;
			board[l * 8 + 6] = KING;
			board[l * 8 + 5] = ROOK;
			
			sk1 = 0b0110L << (l * 8 + 4);
			long sk2 = 0b1001L << (l * 8 + 4);
			
			exists &= ~sk2;
			exists |= sk1;
			
			if (turn) {
				white &= ~sk2;
				white |= sk1;
			}
			else {
				black &= ~sk2;
				black |= sk1;
			}
			break;
		case CASTLE_QUEEN:
			board[l * 8 + n] = null;
			board[dL * 8 + dN] = null;
			board[l * 8 + 2] = KING;
			board[l * 8 + 3] = ROOK;
			
			sq1 = 0b01100L << (l * 8 + 0);
			long sq2 = 0b10001L << (l * 8 + 0);
			
			exists &= ~sq2;
			exists |= sq1;
			
			if (turn) {
				white &= ~sq2;
				white |= sq1;
			}
			else {
				black &= ~sq2;
				black |= sq1;
			}
			break;
		}
		
		if (move == DOUBLE)
			lastDouble = n;
		else
			lastDouble = -1;
		
		if (board[l * 8 + n] == ROOK) {
			if (turn && l == 0) {
				if (n == 0)
					rightToCastleQ_W = false;
				else if (n == 7)
					rightToCastleK_W = false;
			}
			else if (!turn && l == 7) {
				if (n == 0)
					rightToCastleQ_B = false;
				else if (n == 7)
					rightToCastleK_B = false;
			}
		}
		if (board[l * 8 + n] == KING) {
			if (turn) {
				rightToCastleQ_W = false;
				rightToCastleK_W = false;
			}
			else {
				rightToCastleQ_B = false;
				rightToCastleK_B = false;
			}
		}
		
		long hero = turn ? white : black;
		
		long c = coverage(turn, hero, exists);
		System.out.println(String.format("%s %d %d %d %d %b", 
				move.toString(), n, l, dN, dL, 
				//String.format("%64s", Long.toBinaryString((c & findKing(!turn)))).replace(' ', '0')
				(c & findKing(!turn)) == 0
				));
		
		turn = !turn;
		movesSinceLastCapture++;
		lastMove = new int[] {n, l, dN, dL};

		return true;
	}

	public boolean checkMove(int n, int l, int dN, int dL, Moves move) {
		if (!isValidIndex(dN, dL)) {
			return false;
		}
		
		long enemy = turn ? black : white;
		
		long sk1 = 0x7L << (l * 8 + 4); // include king & 2 spaces
		long sq1 = 0x7L << (l * 8 + 1);
		long i = 1L << (n + l * 8);
		long j = 1L << (dN + dL * 8);
		long king = findKing(turn);
		long c = coverage(!turn, enemy, exists);
		long exists = this.exists;
		
		switch (move) {
		case DOUBLE:
		case NORMAL:
			if (board[l * 8 + n] == KING && ((c & j) != 0))
				return false;
			
			var temp = board[dL * 8 + dN];
			board[dL * 8 + dN] = board[l * 8 + n];
			board[l * 8 + n] = null;
			
			enemy &= ~j;
			exists &= ~i;
			exists |= j;
			
			c = coverage(!turn, enemy, exists);
			
			board[l * 8 + n] = board[dL * 8 + dN];
			board[dL * 8 + dN] = temp;
			
			if (board[l * 8 + n] != KING && (c & king) != 0) {
				return false;
			}
			
			return true;
		case EN_PASSANT: // not gonna bother with discovered check detection
			return true;
		case CASTLE_KING:
			return (c & sk1) == 0;
		case CASTLE_QUEEN:
			return (c & sq1) == 0;
		}
		
		throw new IllegalStateException("how did this even get here");
	}
	
	public Moves isValidMove(int n, int l, int dN, int dL) {
		if (!(isValidIndex(n, l) && isValidIndex(dN, dL)))
			return null;
		
		long i = 1L << (l * 8 + n);
		long j = 1L << (dL * 8 + dN);
		
		if (!canMoveFrom(n, l))
			return null;

		Piece p = board[l * 8 + n];
		
		if (p == PAWN) {
			if (isDouble(n, l, dN, dL))
				return DOUBLE;
			
			if (isEnPassant(n, l, dN, dL))
				return EN_PASSANT;
		}
		
		if (p == KING) {
			if (isCastleKing(n, l, dN, dL))
				return CASTLE_KING;
			
			if (isCastleQueen(n, l, dN, dL))
				return CASTLE_QUEEN;
		}
		
		long enemy = ((white & i) > 0) ? black : white;
		long side = ((white & i) > 0) ? 1 : -1;
		
		if ((exists & j) == 0 || ((enemy & j) != 0 && p.canMoveAttack)) {
			for (int[] d : p.single) {
				if (n + d[0] == dN && l + d[1] * side == dL)
					return NORMAL;
			}
			
			for (int[] d : p.repeat) {
				int iN = n, iL = l;
				
				while (isValidIndex(iN += d[0], iL += d[1] * side)) {
					if (iN == dN && iL == dL) {
						return NORMAL;
					}
					
					long k = 1L << (iL * 8 + iN);
					
					if ((exists & k) != 0)
						break;
				}
			}
		}
		
		if ((enemy & j) != 0) {
			for (int[] d : p.attack) {
				if (n + d[0] == dN && l + d[1] * side == dL)
					return NORMAL;
			}
		}
		
		return null;
	}

	public boolean canMoveFrom(int n, int l) {
		long i = 1L << (l * 8 + n);
		
		if (turn)
			return (white & i) != 0;
		else
			return (black & i) != 0;
	}

	/*
	 * expanded bc this is so unreadable
	 * implies king and rook are on starting squares,
	 * if the board has the right to castle.
	 * note: check for correct rook
	 */
	public boolean isCastleKing(int n, int l, int dN, int dL) {
		if (turn) {
			if (!rightToCastleK_W || !(dN == 6 && dL == 0))
				return false;
		}
		else {
			if (!rightToCastleK_B || !(dN == 6 && dL == 7))
				return false;
		}
			
		
		long s = 0b0110L << (l * 8 + 4);
		
		return (exists & s) == 0;
	}
	
	public boolean isCastleQueen(int n, int l, int dN, int dL) {
		if (turn) {
			if (!rightToCastleQ_W || !(dN == 2 && dL == 0))
				return false;
		}
		else {
			if (!rightToCastleQ_B || !(dN == 2 && dL == 7))
				return false;
		}
			
		
		long s = 0b01110L << (l * 8);
		
		return (exists & s) == 0;
	}

	public boolean isEnPassant(int n, int l, int dN, int dL) {
		int deltaL = dL - l;
		
		long i = 1L << (l * 8 + n);
		long k = 1L << (l * 8 + dN);
		
		long enemy = (white & k) != 0 ? black : white;
		
		return lastDouble == dN &&
				deltaL * deltaL == 1 && 
				(enemy & k) != 0 && board[l * 8 + dN] == PAWN;
	}

	public boolean isDouble(int n, int l, int dN, int dL) {
		long j = 1L << (dL * 8 + dN);
		
		return n == dN && 
				(exists & j) == 0 && 
				((l == 1 && dL == 3) || (l == 6 && dL == 4));
	}
	
	public boolean isPromoting(int n, int l) {
		long i = 1L << (l * 8 + n);
		
		if (board[n + l * 8] != PAWN) {
			return false;
		}
		
		if ((white & i) != 0) {
			return n == 6;
		}
		else {
			return n == 1;
		}
	}

	public static boolean isValidIndex(int n, int l) {
		return !(n < 0 || n >= 8 || l < 0 || l >= 8);
	}
	
	public boolean isValidSpace(Piece p, int dN, int dL, long enemy) {
		if (!isValidIndex(dN, dL))
			return false;
		
		long j = 1L << (dN + dL * 8);
		
		return (exists & j) == 0 || ((enemy & j) != 0 && p.canMoveAttack);
	}
	
	public boolean isValidCapture(int dN, int dL, long enemy) {
		if (!isValidIndex(dN, dL))
			return false;
		
		long j = 1L << (dN + dL * 8);
		
		return (enemy & j) != 0;
	}
	
	public long coverage(boolean side) {
		long out = 0L;
		
		long hero = side ? white : black;
		
		for (int n = 0; n < 8; n++) {
			for (int l = 0; l < 8; l++) {
				out |= coverage(n, l, side, hero, exists);
			}
		}
		
		return (out & ~findKing(side));
	}
	
	public long coverage(int n, int l, boolean side) {
		long hero = side ? white : black;
		
		return coverage(n, l, side, hero, exists);
	}
	
	public long coverage(boolean side, long hero, long exists) {
		long c = 0L;
		
		for (int n = 0; n < 8; n++) {
			for (int l = 0; l < 8; l++) {
				c |= coverage(n, l, side, hero, exists);
			}
		}
		
		return (c & ~findKing(side)); // no need to protect king
	}
	
	public long coverage(int n, int l, boolean side, long hero, long exists) {
		long i = 1L << (n + l * 8);
		
		if ((hero & i) == 0)
			return 0;
		
		long out = 0L;
		Piece p = board[l * 8 + n];
		int sign = side ? 1 : -1;
		
		if (p.canMoveAttack) {
			for (int[] d : p.single) {
				int dN = n + d[0], dL = l + d[1] * sign;
				
				long j = 1L << (dN + dL * 8);
				
				if (isValidIndex(dN, dL)) // empty spaces count
					out |= j;
			}
		}
			
		
		for (int[] d : p.repeat) {
			int iN = n + d[0], iL = l + d[1] * sign;
			
			long k = 1L << (iN + iL * 8);
			
			while (isValidIndex(iN, iL) && (exists & k) == 0) {
				out |= k;
				
				iN += d[0];
				iL += d[1] * sign;
				k = 1L << (iN + iL * 8);
			}
			
			if (isValidIndex(iN, iL))
				out |= k;
		}
		
		for (int[] d : p.attack) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			long j = 1L << (dN + dL * 8);
			
			if (isValidIndex(dN, dL))
				out |= j;
		}
		
		return out;
	}

	public long findKing(boolean side) {
		if (side && (rightToCastleK_W || rightToCastleQ_W))
			return 1L << (4 + 0 * 8);
		if (!side && (rightToCastleK_B || rightToCastleQ_B))
			return 1L << (4 + 7 * 8);
			
		int n, l;
		
		long hero = side ? white : black;
		
		for (n = 0; n < 8; n++) {
			for (l = 0; l < 8; l++) {
				long i = 1L << (n + l * 8);
				
				if (board[n + l * 8] == KING && (hero & i) != 0)
					return 1L << (n + l * 8);
			}
		}
		
		throw new IllegalStateException("Each color should always have a king on the board");
	}

	//DEBUG
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		for (int i = 0; i < 8; i++) {
			out.append("  " + i + " ");
		}
		
		out.append("\n");
		
		for (int i = 0; i < 8; i++) {
			out.append("____");
		}
		
		out.append("\n");
		
		for (int l = 7; l >= 0; l--) {
			out.append(l + "|");
			
			for (int n = 0; n < 8; n++) {
				long i = 1L << (l * 8 + n);
				
				if ((white & i) > 0)
					out.append(board[l * 8 + n].name().charAt(0) + "w, ");
				else if ((black & i) != 0)
					out.append(board[l * 8 + n].name().charAt(0) + "b, ");
				else
					out.append("  , ");
			}
			
			out.setCharAt(out.length() - 2, '|');
			out.setCharAt(out.length() - 1, '\n');
		}
		
		for (int i = 0; i < 8; i++) {
			out.append("‾‾‾‾");
		}
		
		return out.toString();
	}
	
	public static String toString(long exists) {
		StringBuilder out = new StringBuilder();
		
		for (int i = 0; i < 8; i++) {
			out.append("  " + i + " ");
		}
		
		out.append("\n");
		
		for (int i = 0; i < 8; i++) {
			out.append("____");
		}
		
		out.append("\n");
		
		for (int l = 7; l >= 0; l--) {
			out.append(l + "|");
			
			for (int n = 0; n < 8; n++) {
				long i = 1L << (l * 8 + n);
				
				if ((exists & i) > 0)
					out.append("xx, ");
				else
					out.append("  , ");
			}
			
			out.setCharAt(out.length() - 2, '|');
			out.setCharAt(out.length() - 1, '\n');
		}
		
		for (int i = 0; i < 8; i++) {
			out.append("‾‾‾‾");
		}
		
		return out.toString();
	}
	
//	public static void main(String[] aggs) {
//		var b = new Board(FILL);
//		var in = new Scanner(System.in);
//		
////		System.out.println(s.toString());
////		System.out.println(Long.toBinaryString(s.exists));
////		System.out.println("3210987654321098765432109876543210987654321098765432109876543210");
////		System.out.println(s.exists & (1));
//		
//		while (true) {
////			System.out.println(String.format("%64s", Long.toBinaryString(b.exists)).replace(' ', '0'));
////			System.out.println(String.format("%64s", Long.toBinaryString(b.black)).replace(' ', '0'));
////			System.out.println(String.format("%64s", Long.toBinaryString(b.white)).replace(' ', '0'));
////			System.out.println("3210987654321098765432109876543210987654321098765432109876543210");
//			
//			System.out.println(b.toString());
//			
//			System.out.println("nextMove:");
//			
//			int n = in.nextInt();
//			int l = in.nextInt();
//			int dN = in.nextInt();
//			int dL = in.nextInt();
//			
//			System.out.println(b.move(n, l, dN, dL));
//		}
//	}
}