package chess;

import static chess.Piece.*;
import static chess.Moves.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Board {
	private static Piece row1[] = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
	
	public static boolean FILL = true;

	public static int MOVE_NUM = 0x000F; // shift 0
	public static int MOVE_LETTER = 0x00F0; // shift 4
	public static int MOVE_NUM_2 = 0x0F00; // shift 8 
	public static int MOVE_LETTER_2 = 0xF000; // shift 12
	
	
	protected Piece[] board;
	protected long white;
	protected long black;
	protected long exists;
	
	protected boolean rightToCastleK_W;
	protected boolean rightToCastleQ_W;
	protected boolean rightToCastleK_B;
	protected boolean rightToCastleQ_B;
	protected int lastDouble;
	
	public Board() {
		board = new Piece[8 * 8];
		resetFlags();
	}
	
	public Board(boolean fill) {
		if (!fill) {
			board = new Piece[8 * 8];
			resetFlags();
			return;
		}

		reset();
	}

	private void reset() {
		board = new Piece[8 * 8];
		
		for (int i = 0; i < 8; i++) {
			board[i] = row1[i];
			board[1 * 8 + i] = Piece.PAWN;
			board[6 * 8 + i] = Piece.PAWN;
			board[7 * 8 + i] = row1[i];
		}
		
		black = 0xFFFF_0000_0000_0000L;
		white = 0x0000_0000_0000_FFFFL;
		exists = black | white;
		
		resetFlags();
	}
	
	private void resetFlags() {
		rightToCastleK_W = true;
		rightToCastleQ_W = true;
		rightToCastleK_B = true;
		rightToCastleQ_B = true;
		lastDouble = -1;
	}
	
	public List<Integer> getAllMoves() {
		var out = new ArrayList<Integer>();
		
		for (int n = 0; n < 8; n++) {
			for (int l = 0; l < 8; l++) {
				out.addAll(getAllMoves(n, l));
			}
		}
		
		return out;
	}
	
	private List<Integer> getAllMoves(int n, int l) {
		int i = 1 << (n + l * 8);
		
		if ((exists & i) == 0)
			return Collections.emptyList();
		
		var out = new ArrayList<Integer>();
		
		long enemy = ((white & i) > 0) ? black : white;
		int side = ((white & i) > 0) ? 1 : -1;
		Piece p = board[l * 8 + n];
		
		for (int[] d : p.single) {
			int j = isValidSpace(n, d[0], l, d[1] * side, p, enemy);
			
			if (j != -1)
				out.add(j);
		}
		
		for (int[] d : p.repeat) {
			int iN = n, iL = l;
			
			int j = isValidSpace(n, d[0], l, d[1] * side, p, enemy);
			
			while (j > 0) {
				out.add(j);
			}
		}
		
		for (int[] d : p.attack) {
			int j = isValidAttackSpace(n, d[0], l, d[1] * side, enemy);
			
			if (j != -1)
				out.add(j);
		}
		
		return out;
	}

	public boolean move(int n, int l, int dN, int dL) {
		var move = isValidMove(n, l, dN, dL);
		
		if (move == null)
			return false;
		
		if (move == DOUBLE)
			lastDouble = n;
		else
			lastDouble = -1;
		
		long i = 1L << (n + l * 8);
		long j = 1L << (dN + dL * 8);
		
		switch (move) {
		case DOUBLE:
		case NORMAL:
			board[dL * 8 + dN] = board[l * 8 + n];
			board[l * 8 + n] = null;
			
			exists = exists & (~i);
			exists = exists | j;
			
			if ((white & i) > 0) {
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
			
			long k = 1L << (l * 8 + dN);
			
			exists = exists & (~i);
			exists = exists & (~k);
			exists = exists | j;
			
			if ((white & i) > 0) {
				black = black & (~k);
				white = white & (~i);
				white = white | j;
			}
			else {
				white = white & (~k);
				black = black & (~i);
				black = black | j;
			}
			break;
		case CASTLE_KING:
			break;
		case CASTLE_QUEEN:
			break;
		}
		
		return true;
	}
	
	public Moves isValidMove(int n, int l, int dN, int dL) {
		if (!(isValidIndex(n, l) && isValidIndex(dN, dL)))
			return null;
		
		long i = 1L << (l * 8 + n);
		long j = 1L << (dL * 8 + dN);
		
		if ((exists & i) == 0)
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

	private boolean isCastleKing(int n, int l, int dN, int dL) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private boolean isCastleQueen(int n, int l, int dN, int dL) {
		// TODO Auto-generated method stub
		return false;
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
		return !(n < 0 && n >= 8 && l < 0 && l >= 8);
	}
	
	public int isValidSpace(int n, int l, int incN, int incL, Piece p, long enemy) {
		if (!isValidIndex(n + incN, l + incL))
			return -1;
		
		int j = 1 << (n + l * 8);
		
		if ((exists & j) == 0 || ((enemy & j) != 0 && p.canMoveAttack)) {
			return (n +
					(l >> 4) + 
					((n + incN) >> 8) +
					((l + incL) >> 12));
		}
		
		return -1;
	}
	
	public int isValidAttackSpace(int n, int l, int incN, int incL, long enemy) {
		if (!isValidIndex(n + incN, l + incL))
			return -1;
		
		int j = 1 << (n + l * 8);
		
		if ((enemy & j) != 0) {
			return (n +
					(l >> 4) + 
					((n + incN) >> 8) +
					((l + incL) >> 12));
		}
		
		return -1;
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
