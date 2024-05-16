package TwoPlayer;

import processing.core.PApplet;

public enum Piece {
	PAWN(new int[][] {}, new int[][] {{0, 1}}, new int[][] {{-1, 1}, {1, 1}}, false),
	KNIGHT(new int[][] {}, new int[][] {{1, 2}, {-1, 2}, {1, -2}, {-1, -2}, {2, 1}, {-2, 1}, {2, -1}, {-2, -1}}),
	BISHOP(new int[][] {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}}),
	ROOK(new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}),
	QUEEN(new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}}),
	KING(new int[][] {}, new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, 1}, {1, -1}, {-1, -1}});
	
	// double as ascii constants
	public static final int WHITE = 0;
	public static final int BLACK = 6;
	
	public int[][] repeat;
	public int[][] single;
	public int[][] attack; // only supports single
	public boolean canMoveAttack;
	
	private Piece(int[][] repeat) {
		this.repeat = repeat;
		this.single = new int[][] {};
		this.attack = new int[0][];
		this.canMoveAttack = true;
	}
	
	private Piece(int[][] repeat, int[][] single) {
		this.repeat = repeat;
		this.single = single;
		this.attack = new int[0][];
		this.canMoveAttack = true;
	}
	
	private Piece(int[][] repeat, int[][] single, int[][] attack, boolean canMoveAttack) {
		this.repeat = repeat;
		this.single = single;
		this.attack = attack;
		this.canMoveAttack = canMoveAttack;
	}
	
	public static Piece getPiece(int ordinal) {
		return Piece.values()[ordinal];
	}
	
	public String getPieceFile(int color) {
		return "/chessSet/" + (color == WHITE ? "w" : "b") + this.toString() + ".svg";
	}
}
