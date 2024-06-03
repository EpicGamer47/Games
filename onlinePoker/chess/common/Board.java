package common;

import static common.Move.*;
import static common.Piece.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayDeque;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Board {
	private static final Piece row1[] = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
	public static final Piece[] promotionPieces = {QUEEN, ROOK, BISHOP, KNIGHT};

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
	public int moveCount;
	public ArrayDeque<MoveData> lastMoves;
	public ArrayDeque<MoveData> futureMoves;
	
	public Board() {
		lastMoves = new ArrayDeque<MoveData>();
		futureMoves = new ArrayDeque<MoveData>();
		reset();
	}
	
	@SuppressWarnings("unchecked")
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
		moveCount = b.moveCount;
		lastMoves = (ArrayDeque<MoveData>) b.lastMoves.clone();
		futureMoves = (ArrayDeque<MoveData>) b.futureMoves.clone();
	}

	private void reset() {
		board = new Piece[8 * 8];
		
		for (int i = 0; i < 8; i++) {
			board[0 * 8 + i] = row1[i];
			board[1 * 8 + i] = PAWN;
			board[6 * 8 + i] = PAWN;
//			board[2 * 8 + i] = PAWN;
//			board[5 * 8 + i] = PAWN;
//			board[3 * 8 + i] = Piece.PAWN;
//			board[4 * 8 + i] = Piece.PAWN;
			board[7 * 8 + i] = row1[i];
		}
		
//		board[7 * 8 + 4] = KING;
//		board[0 * 8 + 4] = KING;
		
			black = 0xFFFF_0000_0000_0000L;
//		black = 0xFF00_0000_0000_0000L;
			white = 0x0000_0000_0000_FFFFL;
//		black = 0xFF00_00FF_0000_0000L;
//		white = 0x0000_0000_FF00_00FFL;
		
//		white = 0x0000_0000_00FF_FF00L | (1L << 0 * 8 + 4);
//		black = 0x00FF_FF00_0000_0000L | (1L << 7 * 8 + 4);
		
		exists = black | white;
		
		resetFlags();
		rightToCastleK_W = false;
		rightToCastleQ_W = false;
		rightToCastleK_B = false;
		rightToCastleQ_B = false;
	}
	
	private void resetFlags() {
		turn = true;
		rightToCastleK_W = true;
		rightToCastleQ_W = true;
		rightToCastleK_B = true;
		rightToCastleQ_B = true;
		lastDouble = -99;
		moveCount = 0;
		movesSinceLastCapture = 0;
		lastMoves.clear();
		futureMoves.clear();
	}
	
	public List<int[]> getAllMoves(boolean turn) {
		ArrayList<int[]> out = new ArrayList<int[]>();
		
		for (int n = 0; n < 8; n++) {
			for (int l = 0; l < 8; l++) {
				out.addAll(getAllMoves(n, l, turn));
			}
		}
		
		return sortMoveList(out);
//		return out;
	}
	
	public List<int[]> getAllMoves(int n, int l) {
		long i = 1L << (n + l * 8);
		
		return sortMoveList(getAllMoves(n, l, (white & i) != 0));
	}
	
	//TODO reverse order when makeObviousMoves is implemented
	public List<int[]> sortMoveList(List<int[]> moves) {
		return moves.stream()
				.map(m -> new MoveInfo(m))
				.sorted((m1, m2) -> {
					if (m1.isCapture)
						return m2.isCapture ? m2.p.compareTo(m1.p) : 1;
					if (m2.isCapture)
						return -1;
					
					return m2.p.compareTo(m1.p);
				})
				.map(m -> m.move)
				.toList();
	}
	
	private class MoveInfo {
		int[] move;
		Piece p;
		boolean isCapture;
		
		MoveInfo(int[] move) {
			this.move = move;
			this.p = Board.this.board[move[0] + move[1] * 8];
			
			long j = 1L << (move[2] + move[3] * 8);
			isCapture = (exists & j) != 0;
		}
	}
	
	public List<int[]> getAllMoves(int n, int l, boolean turn) {
		long i = 1L << (n + l * 8);
		
		long enemy = turn ? black : white;
		long hero = turn ? white : black;
		
		if ((hero & i) == 0)
			return Collections.emptyList();
		
		ArrayList<int[]> out = new ArrayList<int[]>();

		int sign = turn ? 1 : -1;
		Piece p = board[l * 8 + n];
		
		if (isPromoting(n, l)) {
			for (int[] d : p.single) {
				int dN = n + d[0], dL = l + d[1] * sign;
				
				if (isValidSpace(p, dN, dL, enemy) &&
						checkMove(n, l, dN, dL, turn, NORMAL)) {
					for (int ind = 0; ind <= 4; ind++) {
						out.add(new int[] {n, l, dN, dL + ind * sign});
					}
				}	
			}
			
			for (int[] d : p.attack) {
				int dN = n + d[0], dL = l + d[1] * sign;
				
				if (isValidCapture(dN, dL, enemy) && checkMove(n, l, dN, dL, turn, NORMAL)) {
					for (int ind = 0; ind <= 4; ind++) {
						out.add(new int[] {n, l, dN, dL + ind * sign});
					}
				}	
			}
			
			return out;
		}
		
		for (int[] d : p.single) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			if (isValidSpace(p, dN, dL, enemy) &&
					checkMove(n, l, dN, dL, turn, NORMAL))
				out.add(new int[] {n, l, dN, dL});
		}
		
		for (int[] d : p.repeat) {
			int iN = n + d[0], iL = l + d[1] * sign;
			
			long k = 1L << (iN + iL * 8);
			
			while ((exists & k) == 0) {
				if (checkMove(n, l, iN, iL, turn, NORMAL))
					out.add(new int[] {n, l, iN, iL});
				
				iN += d[0];
				iL += d[1] * sign;
				k = 1L << (iN + iL * 8);
			}
			
			if ((enemy & k) != 0  && checkMove(n, l, iN, iL, turn, NORMAL))
				out.add(new int[] {n, l, iN, iL});
		}
		
		for (int[] d : p.attack) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			if (isValidCapture(dN, dL, enemy) && checkMove(n, l, dN, dL, turn, NORMAL))
				out.add(new int[] {n, l, dN, dL});	
		}
		
		if (p == PAWN) {
			if (isDouble(n, l, n, l + 2 * sign) && checkMove(n, l, n, l + 2 * sign, turn, DOUBLE))
				out.add(new int[] {n, l, n, l + 2 * sign});	
			
			if (isEnPassant(n, l, n + 1, l + 1 * sign))
				out.add(new int[] {n, l, n + 1, l + 1 * sign});
			
			if (isEnPassant(n, l, n - 1, l + 1 * sign))
				out.add(new int[] {n, l, n - 1, l + 1 * sign});
		}
		
		if (p == KING) {
			if (isCastleKing(n, l, 6, l) && checkMove(n, l, 6, l, turn, CASTLE_KING)) // castle king
				out.add(new int[] {n, l, 6, l});
			
			if (isCastleQueen(n, l, 2, l) && checkMove(n, l, 2, l, turn, CASTLE_QUEEN)) // castle queen
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
				.filter(p -> isValidIndex(p.x, p.y))
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
				.filter(p -> isValidIndex(p.x, p.y))
				.filter(p -> !captures.contains(p))
				.collect(Collectors.toList());
		
		return new List[] {
				normal, 
				captures
					};			
	}
	
	public MoveData move(int n, int l, int dN, int dL) {
		var out = move(n, l, dN, dL, turn);
		
		if (out != null) {
			turn = !turn;
			movesSinceLastCapture++;
		}
		
		return out;
	}
	
	public MoveData move(int n, int l, int dN, int dL, boolean turn) {
		var move = isValidMove(n, l, dN, dL, turn);
		
		if (move == null) {
//			System.out.println(String.format("ILLEGALFAIL %d %d %d %d", n, l, dN, dL));
			return null;
		}
		
		switch (move) {
		case PROMOTION_B:
		case PROMOTION_N:
		case PROMOTION_Q:
		case PROMOTION_R:
			dL = Math.max(0, Math.min(dL, 7));
			break;
		default:
			break;
		}
		
		if (!checkMove(n, l, dN, dL, turn, move)) {
//			System.out.println(String.format("CHECKFAIL %d %d %d %d", n, l, dN, dL));
			return null;
		}
		
//		System.out.println(
//				getAllMoves(n, l).stream()
//					.map(a -> Arrays.toString(a) + ", ")
//					.reduce("", (curr, next) -> curr + next));
		
		long sk1 = 0x7L << (l * 8 + 4); // include king & 2 spaces
		long sq1 = 0x7L << (l * 8 + 1);
		
		long i = 1L << (n + l * 8);
		long j = 1L << (dN + dL * 8);
		
		MoveData out = new MoveData();

		Position from = new Position(n, l);
		Position to = new Position(dN, dL);
		
		if (board[dL * 8 + dN] != null || board[l * 8 + n] == PAWN)
			movesSinceLastCapture = 0;
		
		switch (move) {
		case DOUBLE:
		case NORMAL:
			out.p = new Position[] {from, to};
			
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
			out.p = new Position[] {from, to, new Position(dN, l)};
			
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
			out.p = new Position[] {from, to, new Position(5, l), new Position(6, l)};
			
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
			out.p = new Position[] {from, to, new Position(2, l), new Position(3, l)};
			
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
		case PROMOTION_B:
		case PROMOTION_N:
		case PROMOTION_Q:
		case PROMOTION_R:
			out.p = new Position[] {from, to};
			
			board[dL * 8 + dN] = promotionPieces[move.ordinal() - Move.PROMOTION_Q.ordinal()];
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
		}
		
		if (move == DOUBLE)
			lastDouble = n;
		else
			lastDouble = -99;
		
		if (board[dL * 8 + dN] == ROOK) {
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
		
		if (board[dL * 8 + dN] == KING) {
			if (turn) {
				rightToCastleQ_W = false;
				rightToCastleK_W = false;
			}
			else {
				rightToCastleQ_B = false;
				rightToCastleK_B = false;
			}
		}
		
//		long hero = turn ? white : black;
		
//		long c = coverage(true, white, exists);
//		long c2 = coverage(false, black, exists);
		
//		System.out.println(String.format("%s %d %d %d %d %b", 
//				move.toString(), n, l, dN, dL, 
//				//String.format("%64s", Long.toBinaryString((c & findKing(!turn)))).replace(' ', '0')
//				(c & findKing(!turn)) == 0)
//				);
		
////		if ((c & findKing(!turn)) != 0)
//			System.out.println(toString(c));
//			System.out.println(toString(c2));
		
		lastMoves.addFirst(out);
		futureMoves.clear();
		moveCount++;

		return out;
	}
	
	public void forceMove(int n, int l, int dN, int dL, boolean turn) {
		var move = isValidMove(n, l, dN, dL, turn);
		
		forceMove(n, l, dN, dL, turn, move);
	}
	
	public void forceMove(int n, int l, int dN, int dL, boolean turn, Move move) {
		switch (move) {
		case PROMOTION_B:
		case PROMOTION_N:
		case PROMOTION_Q:
		case PROMOTION_R:
			dL = Math.max(0, Math.min(dL, 7));
			break;
		default:
			break;
		}
		
		long i = 1L << (n + l * 8);
		long j = 1L << (dN + dL * 8);
		
		MoveData out = new MoveData();

		Position from = new Position(n, l);
		Position to = new Position(dN, dL);
		
		if (board[dL * 8 + dN] != null || board[l * 8 + n] == PAWN)
			movesSinceLastCapture = 0;
		
		switch (move) {
		case DOUBLE:
		case NORMAL:
			out.p = new Position[] {from, to};
			
			board[dL * 8 + dN] = board[l * 8 + n];
			board[l * 8 + n] = null;
			
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
			
			exists = black | white;
			
			break;
		case EN_PASSANT: // not gonna bother with discovered check detection
			out.p = new Position[] {from, to, new Position(dN, l)};
			
			board[dL * 8 + dN] = board[l * 8 + n];
			board[l * 8 + n] = null;
			board[l * 8 + dN] = null;
			
			long k1 = 1L << (l * 8 + dN); // en-passanted pawn
			
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
			
			exists = black | white;
			
			break;
		case CASTLE_KING:
			out.p = new Position[] {from, to, new Position(5, l), new Position(6, l)};
			
			board[l * 8 + n] = null;
			board[dL * 8 + dN] = null;
			board[l * 8 + 6] = KING;
			board[l * 8 + 5] = ROOK;
			
			long sk1 = 0b0110L << (l * 8 + 4);
			long sk2 = 0b1001L << (l * 8 + 4);
			
			if (turn) {
				white &= ~sk2;
				white |= sk1;
			}
			else {
				black &= ~sk2;
				black |= sk1;
			}
			
			exists = black | white;
			
			break;
		case CASTLE_QUEEN:
			out.p = new Position[] {from, to, new Position(2, l), new Position(3, l)};
			
			board[l * 8 + n] = null;
			board[dL * 8 + dN] = null;
			board[l * 8 + 2] = KING;
			board[l * 8 + 3] = ROOK;
			
			long sq1 = 0b01100L << (l * 8 + 0);
			long sq2 = 0b10001L << (l * 8 + 0);
			
			if (turn) {
				white &= ~sq2;
				white |= sq1;
			}
			else {
				black &= ~sq2;
				black |= sq1;
			}
			
			exists = black | white;
			
			break;
		case PROMOTION_B:
		case PROMOTION_N:
		case PROMOTION_Q:
		case PROMOTION_R:
			out.p = new Position[] {from, to};
			
			board[dL * 8 + dN] = promotionPieces[move.ordinal() - Move.PROMOTION_Q.ordinal()];
			board[l * 8 + n] = null;
			
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
			
			exists = black | white;
			
			break;
		}
		
		if (move == DOUBLE)
			lastDouble = n;
		else
			lastDouble = -99;
		
		if (board[dL * 8 + dN] == ROOK) {
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
		
		if (board[dL * 8 + dN] == KING) {
			if (turn) {
				rightToCastleQ_W = false;
				rightToCastleK_W = false;
			}
			else {
				rightToCastleQ_B = false;
				rightToCastleK_B = false;
			}
		}
		
//		long hero = turn ? white : black;
		
//		long c = coverage(true, white, exists);
//		long c2 = coverage(false, black, exists);
		
//		System.out.println(String.format("%s %d %d %d %d %b", 
//				move.toString(), n, l, dN, dL, 
//				//String.format("%64s", Long.toBinaryString((c & findKing(!turn)))).replace(' ', '0')
//				(c & findKing(!turn)) == 0)
//				);
		
////		if ((c & findKing(!turn)) != 0)
//			System.out.println(toString(c));
//			System.out.println(toString(c2));
		
		lastMoves.addFirst(out);
//		futureMoves.clear();
		moveCount++;
	}
	
	public class Position {
		public Position() { }
		
		public Position(int n, int l) {
			this.n = n;
			this.l = l;
			this.p = board[n + l * 8];
		}
		
		public Position(int n, int l, Piece p) {
			this.n = n;
			this.l = l;
			this.p = p;
		}

		public int n, l;
		public Piece p;
	}
	
	public class MoveData {
		public MoveData() {
			white = Board.this.white;
			black = Board.this.black;
			exists = Board.this.exists;
			cKW = Board.this.rightToCastleK_W;
			cQW = Board.this.rightToCastleQ_W;
			cKB = Board.this.rightToCastleK_B;
			cQB = Board.this.rightToCastleQ_B;
			d = Board.this.lastDouble;
		}
		
		public MoveData(MoveData md) {
			this();
			p = new Position[md.p.length];
			
			for (int i = 0; i < p.length; i++) {
				p[i] = new Position(md.p[i].n, md.p[i].l);
			}
		}

		public long white, black, exists;
		public Position[] p;
		boolean cKW, cQW, cKB, cQB;
		public int d;
	}
	
	public void revert() {
		if (lastMoves.isEmpty())
			return;
		
		var md = lastMoves.pop();
		var current = new MoveData(md);
		
		white = md.white;
		black = md.black;
		exists = md.exists;
		
		rightToCastleK_W = md.cKW;
		rightToCastleQ_W = md.cQW;
		rightToCastleK_B = md.cKB;
		rightToCastleQ_B = md.cQB;
		lastDouble = md.d;
		
		for (var pos : md.p) {
			board[pos.n + pos.l * 8] = pos.p;
		}
		moveCount--;
		
		futureMoves.addFirst(current);
	}
	
	public void noPushRevert() {
		if (lastMoves.isEmpty())
			return;
		
		var md = lastMoves.pop();
		
		white = md.white;
		black = md.black;
		exists = md.exists;
		
		rightToCastleK_W = md.cKW;
		rightToCastleQ_W = md.cQW;
		rightToCastleK_B = md.cKB;
		rightToCastleQ_B = md.cQB;
		lastDouble = md.d;
		
		for (var pos : md.p) {
			board[pos.n + pos.l * 8] = pos.p;
		}
		moveCount--;
	}
	
	public void restore() {
		if (futureMoves.isEmpty())
			return;
		
		var md = futureMoves.pop();
		var current = new MoveData(md);
		
		white = md.white;
		black = md.black;
		exists = md.exists;
		
		rightToCastleK_W = md.cKW;
		rightToCastleQ_W = md.cQW;
		rightToCastleK_B = md.cKB;
		rightToCastleQ_B = md.cQB;
		lastDouble = md.d;
		moveCount++;
		
		for (var pos : md.p) {
			board[pos.n + pos.l * 8] = pos.p;
		}
		
		lastMoves.addFirst(current);
	}

	
	public boolean checkMove(int n, int l, int dN, int dL, boolean turn, Move move) {
		if (!isValidIndex(dN, dL)) {
			return false;
		}
		
		switch (move) {
		case CASTLE_KING:
			return !isBeingAttacked(n, l, turn) && 
					!isBeingAttacked(5, l, turn) && 
					!isBeingAttacked(6, l, turn);
		case CASTLE_QUEEN:
			return !isBeingAttacked(2, l, turn) && 
					!isBeingAttacked(3, l, turn) && 
					!isBeingAttacked(n, l, turn);
		default:
			forceMove(n, l, dN, dL, turn, move);
			boolean out = !isInCheck(turn);
			noPushRevert();
			
			return out;
		}
	}
	
	public Move isValidMove(int n, int l, int dN, int dL) {
		return isValidMove(n, l, dN, dL, turn);
	}
	
	public Move isValidMove(int n, int l, int dN, int dL, boolean turn) {
		if (!(isValidIndex(n, l)))
			return null;
		
		Piece p = board[l * 8 + n];
		long i = 1L << (l * 8 + n);
		long side = ((white & i) != 0) ? 1 : -1;
		
		if (p == PAWN && isPromoting(n, l) && !(isValidIndex(dN, dL))) {
			return isValidPromotionMove(n, l, dN, dL, turn);
		}
	
		if (!isValidIndex(dN, dL))
			return null;

		long j = 1L << (dL * 8 + dN);
		
		if (!canMoveFrom(n, l, turn))
			return null;
		
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
		
		long enemy = ((white & i) != 0) ? black : white;
		
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

	private Move isValidPromotionMove(int n, int l, int dN, int dL, boolean turn) {
		long i = 1L << (l * 8 + n);
		long side = turn ? 1 : -1;
		
		int pI = dL - 7 - 1;
		
		if (!turn)
			pI = 0 - dL - 1;
		
		dL -= (pI + 1) * side;
		
		var check = isValidMove(n, l, dN, dL, turn);
		
		if (check == null) {
			return null;
		}
		
		Move[] promotions = {PROMOTION_Q, PROMOTION_R, PROMOTION_B, PROMOTION_N};
		
		return promotions[pI];
	}

	public boolean canMoveFrom(int n, int l, boolean turn) {
		long i = 1L << (l * 8 + n);
		
		if (turn)
			return (white & i) != 0;
		else
			return (black & i) != 0;
	}

	/*
	 * implies king and rook are on starting squares
	 * check for correct rook
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
		if (!isValidIndex(dN, dL))
			return false;
		
		int deltaL = dL - l;
		int deltaN = dN - n;
		
		long i = 1L << (l * 8 + n);
		long k = 1L << (l * 8 + dN);
		
		long enemy = (white & i) != 0 ? black : white;
		
		return lastDouble == dN &&
				((white & i) != 0 ? l == 4 : l == 3) &&
				deltaL * deltaL == 1 && 
				deltaN * deltaN == 1 &&
				(enemy & k) != 0 && board[l * 8 + dN] == PAWN;
	}

	public boolean isDouble(int n, int l, int dN, int dL) {
		long i = 1L << (l * 8 + n);
		long j = 1L << (dL * 8 + dN);
		
		int side = (white & i) != 0 ? 1 : -1;
		long k = 1L << ((l + side) * 8 + n);
		
		return n == dN && 
				(exists & j) == 0 && 
				((l == 1 && dL == 3) || (l == 6 && dL == 4)) &&
				(exists & k) == 0;
	}
	
	public boolean isPromoting(int n, int l) {
		long i = 1L << (l * 8 + n);
		
		if (board[n + l * 8] != PAWN) {
			return false;
		}
		
		if ((white & i) != 0) {
			return l == 6;
		}
		else {
			return l == 1;
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
	
	public long coverage(long i) {
		int k = Long.numberOfTrailingZeros(i);
		int n = k % 8, l = k / 8;
		boolean side = (white & i) != 0;
		
		long hero = side ? white : black;
		
		return coverage(n, l, side, hero, exists);
	}
	
	public long coverage(int n, int l) {
		long i = 1L << (n + l * 8);
		boolean side = (white & i) != 0;
		
		long hero = side ? white : black;
		
		return coverage(n, l, side, hero, exists);
	}
	
	public long coverage(int n, int l, boolean side) {
		long hero = side ? white : black;
		
		return coverage(n, l, side, hero, exists);
	}
	
	public long[] coverageSeperated(int n, int l, boolean side) {
		long i = 1L << (n + l * 8);
		
		long hero = side ? white : black;
		
		if ((hero & i) == 0)
			return new long[] {0, 0, 0};
		
		long sees = 0L, protects = 0L, attacks = 0L;
		Piece p = board[l * 8 + n];
		int sign = side ? 1 : -1;
		
		if (p.canMoveAttack) {
			for (int[] d : p.single) {
				int dN = n + d[0], dL = l + d[1] * sign;
				
				long j = 1L << (dN + dL * 8);
				
				if (isValidIndex(dN, dL)) {
					if ((exists & j) == 0)
						sees |= j;
					else if ((hero & j) != 0)
						protects |= j;
					else
						attacks |= j;
				}
			}
		}	
		
		for (int[] d : p.repeat) {
			int iN = n + d[0], iL = l + d[1] * sign;
			
			long k = 1L << (iN + iL * 8);
			
			while (isValidIndex(iN, iL) && (exists & k) == 0) {
				sees |= k;
				
				iN += d[0];
				iL += d[1] * sign;
				k = 1L << (iN + iL * 8);
			}
			
			if (isValidIndex(iN, iL)) { // if isValidIndex, can never see k
				if ((hero & k) != 0)
					protects |= k;
				else
					attacks |= k;
			}
		}
		
		for (int[] d : p.attack) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			long j = 1L << (dN + dL * 8);
			
			if (isValidIndex(dN, dL)) {
				if ((exists & j) == 0)
					sees |= j;
				else if ((hero & j) != 0)
					protects |= j;
				else
					attacks |= j;
			}
		}
		
		return new long[] {sees, protects, attacks};
	}

	public long coverage(boolean side, long hero, long exists) {
		long c = 0L;
		
		for (int n = 0; n < 8; n++)
			for (int l = 0; l < 8; l++)
				c |= coverage(n, l, side, hero, exists);
		
		return (c & ~findKing(side)); // no need to protect king
//		return ~findKing(!side);
	}
	
	private long coverage(int n, int l, boolean side, long hero, long exists) {
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
		// Premature optimization is the root of all evil
		
//		if (side && (rightToCastleK_W || rightToCastleQ_W))
//			return 1L << (4 + 0 * 8);
//		if (!side && (rightToCastleK_B || rightToCastleQ_B))
//			return 1L << (4 + 7 * 8);
			
		int n, l;
		
		long hero = side ? white : black;
		
		for (n = 0; n < 8; n++) {
			for (l = 0; l < 8; l++) {
				long i = 1L << (n + l * 8);
				
				if (board[n + l * 8] == KING && (hero & i) != 0)
					return 1L << (n + l * 8);
			}
		}
		
		return 0xFFFF_FFFF_FFFF_FFFFL;
		// returning a full string makes most bitmasks fail 
	}

	public boolean isGameOver() {
		return getAllMoves(turn).isEmpty() || movesSinceLastCapture >= 50;
	}
	
	// does NOT confirm if the square is attack due to en passant
	public boolean isBeingAttacked(int n, int l, boolean turn) {
		long enemy = turn ? black : white;
		int sign = turn ? 1 : -1;
		
		for (int[] d : AllMoves.single) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			long j = 1L << (dN + dL * 8);
			
			if (isValidIndex(dN, dL) && (enemy & j) != 0 && 
					board[dN + dL * 8].canMoveAttack && board[dN + dL * 8].hasMove(SINGLE, d))
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
			
			if (isValidIndex(iN, iL) && (enemy & k) != 0 && board[iN + iL * 8].hasMove(REPEAT, d))
				return true;
		}
		
		for (int[] d : AllMoves.attack) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			long j = 1L << (dN + dL * 8);
			
			if (isValidIndex(dN, dL) && (enemy & j) != 0 && board[dN + dL * 8].hasMove(ATTACK, d))
				return true;
		}
		
		return false;
	}
	
	public boolean isInCheck(boolean turn) {
		int k = Long.numberOfTrailingZeros(findKing(turn));
		int n = k % 8, l = k / 8;
		
		return isBeingAttacked(n, l, turn);
	}
	
	public long attackers(int n, int l, boolean turn) {
		long enemy = turn ? black : white;
		int sign = turn ? 1 : -1;
	
		long attackers = 0L;
		
		for (int[] d : AllMoves.single) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			long j = 1L << (dN + dL * 8);
			
			if (isValidIndex(dN, dL) && (enemy & j) != 0 && 
					board[dN + dL * 8].canMoveAttack && board[dN + dL * 8].hasMove(SINGLE, d))
				attackers &= ~j;
		}
		
		for (int[] d : AllMoves.repeat) {
			int iN = n + d[0], iL = l + d[1];
			
			long k = 1L << (iN + iL * 8);
			
			while (isValidIndex(iN, iL) && (exists & k) == 0) {
				iN += d[0];
				iL += d[1] * sign;
				k = 1L << (iN + iL * 8);
			}
			
			if (isValidIndex(iN, iL) && (enemy & k) != 0 && board[iN + iL * 8].hasMove(REPEAT, d))
				attackers &= ~k;
		}
		
		for (int[] d : AllMoves.attack) {
			int dN = n + d[0], dL = l + d[1] * sign;
			
			long j = 1L << (dN + dL * 8);
			
			if (isValidIndex(dN, dL) && (enemy & j) != 0 && board[dN + dL * 8].hasMove(ATTACK, d))
				attackers &= ~j;
		}
		
		return attackers;
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
				
				if ((white & i) != 0)
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
	
	public String toUncoloredString() {
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
				if (board[l * 8 + n] != null)
					out.append(board[l * 8 + n].name().charAt(0) + " , ");
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
				
				if ((exists & i) != 0)
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
