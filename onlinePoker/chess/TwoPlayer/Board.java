package TwoPlayer;

import static TwoPlayer.Moves.*;
import static TwoPlayer.Piece.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {
	private static Piece row1[] = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};

	public static int MOVE_NUM = 0x000F; // shift 0
	public static int MOVE_LETTER = 0x00F0; // shift 4
	public static int MOVE_NUM_2 = 0x0F00; // shift 8 
	public static int MOVE_LETTER_2 = 0xF000; // shift 12
	
	
	protected Piece[] board;
	protected long white;
	protected long black;
	protected long exists;
	
	protected boolean turn; // true = white, false = black
	protected boolean rightToCastleK_W;
	protected boolean rightToCastleQ_W;
	protected boolean rightToCastleK_B;
	protected boolean rightToCastleQ_B;
	protected int lastDouble;
	protected boolean inCheck;
	
	public Board() {
		board = new Piece[8 * 8];
		resetFlags();
	}
	
	public Board(boolean fill) {
		if (fill) {
			reset();
			return;
		}

		board = new Piece[8 * 8];
		resetFlags();
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
		inCheck = false;
	}
	
	public List<int[]> getAllMoves() {
		ArrayList<int[]> out = new ArrayList<int[]>();
		
		for (int n = 0; n < 8; n++) {
			for (int l = 0; l < 8; l++) {
				out.addAll(getAllMoves(n, l));
			}
		}
		
		return out;
	}
	
	public List<int[]> getAllMoves(int n, int l) {
		long i = 1L << (n + l * 8);
		
		if ((exists & i) == 0)
			return Collections.emptyList();
		
		ArrayList<int[]> out = new ArrayList<int[]>();
		
		long enemy = turn ? black : white;
		int sign = turn ? 1 : -1;
		Piece p = board[l * 8 + n];
		
		for (int[] d : p.single) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			if (isValidSpace(p, dN, dL, enemy))
				out.add(new int[] {n, l, dN, dL});
		}
		
		for (int[] d : p.repeat) {
			int iN = n + d[0], iL = l + d[1] * sign;
			
			while (isValidSpace(p, iN, iL, enemy) && 
					!isValidCapture(iN, iL, enemy)) {
				out.add(new int[] {n, l, iN, iL});
				iN += d[0];
				iL += d[1] * sign;
			}
			
			if (isValidCapture(iN, iL, enemy))
				out.add(new int[] {n, l, iN, iL});
		}
		
		for (int[] d : p.attack) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			if (isValidCapture(dN, dL, enemy))
				out.add(new int[] {n, l, dN, dL});	
		}
		
		if (p == PAWN) {
			if (isDouble(n, l, n, l + 2 * sign))
				out.add(new int[] {n, l, n, l + 2 * sign});	
			
			if (isEnPassant(n, l, n + 1, l + 1 * sign))
				out.add(new int[] {n, l, n + 1, l + 1 * sign});
			
			if (isEnPassant(n, l, n - 1, l + 1 * sign))
				out.add(new int[] {n, l, n - 1, l + 1 * sign});
		}
		
		if (p == KING) {
			if (isCastleKing(n, l, 6, l))
				out.add(new int[] {n, l, 6, l});
			
			if (isCastleQueen(n, l, 2, l))
				out.add(new int[] {n, l, 2, l});
		}
		
		return out;
	}

	@SuppressWarnings("unchecked")
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
		
		if (move == null || 
				(inCheck && (move == CASTLE_KING || move == CASTLE_QUEEN))) {
			System.out.println(String.format("FAIL %d %d %d %d", n, l, dN, dL));
			return false;
		}
		
		long oldBlack = black, oldWhite = white;
		Piece[] oldBoard = board.clone();
		

			
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
		
			System.out.println(String.format("%b %b %b %b",
					rightToCastleK_W,
					rightToCastleQ_W,
					rightToCastleK_B,
					rightToCastleQ_B));
		}
		
		long i = 1L << (n + l * 8);
		long j = 1L << (dN + dL * 8);
		long k1, s1, s2;
		
		switch (move) {
		case DOUBLE:
		case NORMAL:
			board[dL * 8 + dN] = board[l * 8 + n];
			board[l * 8 + n] = null;
			
			exists = exists & (~i);
			exists = exists | j;
			
			if (turn) {
				black = black & (~j);
				white = white & (~i);
				white = white | j;
			}
			else {
				white = white & (~j);
				black = black & (~i);
				black = black | j;
			}
			break;
		case EN_PASSANT:
			board[dL * 8 + dN] = board[l * 8 + n];
			board[l * 8 + n] = null;
			board[l * 8 + dN] = null;
			
			k1 = 1L << (l * 8 + dN); // en-passanted pawn
			
			exists = exists & (~i);
			exists = exists & (~k1);
			exists = exists | j;
			
			if (turn) {
				black = black & (~k1);
				white = white & (~i);
				white = white | j;
			}
			else {
				white = white & (~k1);
				black = black & (~i);
				black = black | j;
			}
			break;
		case CASTLE_KING:
			board[l * 8 + n] = null;
			board[dL * 8 + dN] = null;
			board[l * 8 + 6] = KING;
			board[l * 8 + 5] = ROOK;
			
			s1 = 0b0110L << (l * 8 + 4);
			s2 = 0b1001L << (l * 8 + 4);
			
			exists = exists & (~s2);
			exists = exists | s1;
			
			if (turn) {
				white = white & (~s2);
				white = white | s1;
			}
			else {
				black = black & (~s2);
				black = black | s1;
			}
			break;
		case CASTLE_QUEEN:
			board[l * 8 + n] = null;
			board[dL * 8 + dN] = null;
			board[l * 8 + 2] = KING;
			board[l * 8 + 3] = ROOK;
			
			s1 = 0b01100L << (l * 8 + 0);
			s2 = 0b10001L << (l * 8 + 0);
			
			exists = exists & (~s2);
			exists = exists | s1;
			
			if (turn) {
				white = white & (~s2);
				white = white | s1;
			}
			else {
				black = black & (~s2);
				black = black | s1;
			}
			break;
		}
		
		if (move == DOUBLE)
			lastDouble = n;
		else
			lastDouble = -1;
		
		if (board[l * 8 + n] == KING) {
			if (turn) {
				rightToCastleK_W = false;
				rightToCastleQ_W = true;
			}
			else {
				rightToCastleK_B = false;
				rightToCastleQ_B = true;
			}
		}

		turn = !turn;
		
		inCheck = isInCheck(turn);
		
		System.out.println(String.format("%s %d %d %d %d %b", move.toString(), n, l, dN, dL, inCheck));
//		System.out.println(String.format("%s %d %d %d %d", move.toString(), n, l, dN, dL));

		return true;
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
	private boolean isCastleKing(int n, int l, int dN, int dL) {
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
	
	private boolean isCastleQueen(int n, int l, int dN, int dL) {
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

	private boolean isEnPassant(int n, int l, int dN, int dL) {
		int deltaL = dL - l;
		
		long k = 1L << (l * 8 + dN);
		
		return lastDouble == dN &&
				deltaL * deltaL == 1 && 
				(exists & k) != 0 && board[l * 8 + dN] == PAWN;
	}

	private boolean isDouble(int n, int l, int dN, int dL) {
		long j = 1L << (dL * 8 + dN);
		
		return n == dN && 
				(exists & j) == 0 && 
				((l == 1 && dL == 3) || (l == 6 && dL == 4));
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
		long hero = side ? white : black;
		long enemy = side ? black : white;
		
		long out = 0L;
		
		for (int n = 0; n < 8; n++) {
			for (int l = 0; l < 8; l++) {
				out |= coverage(n, l, hero, enemy);
			}
		}
		
		return out;
	}
	
	private long coverage(int n, int l, long hero, long enemy) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Deprecated
	public boolean isInCheck(boolean side) {
		Point p = findKing(side);
		int n = p.x, l = p.y;
		long enemy = side ? black : white;
		int sign = side ? 1 : -1; // backtrack
		
		for (int[] d : AllMoves.single) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			long j = 1L << (dN + dL * 8);
			
			if (isValidIndex(dN, dL) && (enemy & j) != 0)
				for (int[] D : board[dN + dL * 8].single)
					if (Arrays.equals(d, D))
						return true;
		}
		
		for (int[] d : AllMoves.repeat) {
			int iN = n + d[0], iL = l + d[1] * sign;
			
			long k = 1L << (iN + iL * 8);
			
			while (isValidIndex(iN, iL) && (exists & k) == 0) {
				iN += d[0];
				iL += d[1] * sign;
				k = 1L << (iN + iL * 8);
			}
			
			if (isValidIndex(iN, iL) && (enemy & k) != 0)
				for (int[] D : board[iN + iL * 8].repeat)
					if (Arrays.equals(d, D))
						return true;
		}
		
		for (int[] d : AllMoves.attack) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			long j = 1L << (dN + dL * 8);
			
			if (isValidIndex(dN, dL) && (enemy & j) != 0)
				for (int[] D : board[dN + dL * 8].attack)
					if (Arrays.equals(d, D))
						return true;
		}
		
		return false;
	}

	public Point findKing(boolean side) {
		int n, l;
		
		long hero = side ? white : black;
		
		for (n = 0; n < 8; n++) {
			for (l = 0; l < 8; l++) {
				long i = 1L << (n + l * 8);
				
				if ((hero & i) != 0 && board[n + l * 8] == KING)
					return new Point(n, l);
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
