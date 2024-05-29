package singlePlayer;

import static common.Piece.BLACK;
import static common.Piece.WHITE;

import java.awt.Point;
import java.util.List;

import common.AI;
import common.Board;
import processing.core.PApplet;
import processing.core.PShape;

@SuppressWarnings("exports")
public class ProcessingBoard1 extends Board {
	public static final int width = 75;
	
	private PApplet parent;
	private Point lastClick;
	private int promotionClick;
	private List<Point>[] lastClickMoves;
	private boolean gameOver;
	public final boolean playerSide;
	public AI ai;

	public ProcessingBoard1(PApplet parent, boolean playerSide) {
		super();
		this.parent = parent;
		this.playerSide = playerSide;
		
		ai = new AlphaBetaAI(this, 5);
		
		setupProcessing();
	}
	
	private void setupProcessing() {
		parent.noStroke();
		parent.rectMode(PApplet.CORNER);
		parent.shapeMode(PApplet.CORNER);
		parent.ellipseMode(PApplet.CENTER);
		parent.textAlign(PApplet.CENTER, PApplet.CENTER);
	}

	public void draw() {
//		if (isPromoting(lastClick.x, lastClick.y)) {
			if (turn)
				drawPromotion(0);
			else
				drawPromotion(800);
//		}
		
		drawBackground();
		
		for (int l = 0; l < 8; l++) {
			for (int n = 0; n < 8; n++) {
				drawSquare(n, l, 100, 100, playerSide);
			}
		}
		
		drawDots(turn);
		
		if (lastClick != null && isPromoting(lastClick.x, lastClick.y)) {
			if (playerSide)
				drawPromotion(WHITE);
			else
				drawPromotion(BLACK);
		}
		
		if (isGameOver())
			gameOver();
		
		if (turn != playerSide) {
			ai.makeAMove();
		}
	}
	
	private void drawBackground() {
		if (playerSide)
			parent.background(0xFFDDDDDD);
		else
			parent.background(0xFF222222);
		
		if (playerSide)
			parent.fill(0xFFCCCCCC);
		else
			parent.fill(0xFF333333);
		
		int w = ProcessingBoard1.width;
		parent.rect(100 - w, 100 - w, w * 10, w * 10);
	}

	private void gameOver() {
		parent.fill(0x77000000);
		parent.rect(0, 0, parent.width, parent.height);
		parent.fill(0xFFFFFFFF);
		parent.textSize(100);
		
		long king = this.findKing(turn);
		long hero = turn ? white : black;
		
		String msg = "50 move rule - draw";
		
		if ((hero & king) == 0)
			msg = "Stalemate";
		else
			msg = "Checkmate: " + (turn ? "Black" : "White") + " wins!";
		
		parent.text(msg, parent.width / 2, parent.height / 2);
	}

	private void drawDots(boolean side) {	
		if (lastClickMoves == null)
			return;
		
		for (Point p : lastClickMoves[0]) {
			float x1, y1;
			
			if (side) {
				x1 = 100 + width * (p.x + 0.5f);
				y1 = 100 + width * (7 - p.y + 0.5f);
			}
			else {
				x1 = (parent.width - 100 - width * 8) + width * (7 - p.x + 0.5f);
				y1 = 100 + width * (p.y + 0.5f);
			}
			
			parent.fill(0x77000000);
			
			parent.ellipse(x1, y1, width * 0.33f, width * 0.33f);
		}
		
		for (Point p : lastClickMoves[1]) {
			float x1, y1;
			
			if (side) {
				x1 = 100 + width * (p.x + 0.5f);
				y1 = 100 + width * (7 - p.y + 0.5f);
			}
			else {
				x1 = (parent.width - 100 - width * 8) + width * (7 - p.x + 0.5f);
				y1 = 100 + width * (p.y + 0.5f);
			}
			
			parent.fill(0x77FF0000);
			
			parent.ellipse(x1, y1, width * 0.33f, width * 0.33f);
		}
	}

	private void drawPromotion(int color) {
		int x = 100 + 8 * width;
		int y = 100 + 2 * width;
		
		for (int i = 0; i < promotionPieces.length; i++) {
			var p = promotionPieces[i];
			
			int tile = (y) % 2 == 0 ? 0xFFb0b0a9 : 0xFF4d6138;
			
			parent.fill(tile);
			parent.rect(x, y, width, width);
			
			if (i == promotionClick) {
				parent.fill(0xbbe5f53d);
				parent.rect(x, y, width, width);
			}
			
			var img = parent.loadShape(p.getPieceFile(color));
			parent.shape(img, x, y, width, width);
			
			y += width;
		}
	}

	private void drawSquare(int n, int l, int x, int y, boolean side) {
		int dN, dL;
		
		if (side) {
			dN = n;
			dL = 7 - l;
		}
		else {
			dN = 7 - n;
			dL = l;
		}
		
		int x1 = x + width * (dN);
		int y1 = y + width * (dL);
		
		int color = (dL + dN) % 2 == 0 ? 0xFFeeeed2 : 0xFF769656;
		
		parent.fill(color);
		parent.rect(x1, y1, width, width);
		
		if (side == turn &&
				lastClick != null && 
				n == lastClick.x && l == lastClick.y) {
			parent.fill(0xbbe5f53d);
			parent.rect(x1, y1, width, width);
		}

		if (!lastMoves.isEmpty()) {
			var m = lastMoves.peek().p;
			
			if ((m[0].n == n && m[0].l == l) || 
						(m[1].n == n && m[1].l == l)) {
				parent.fill(0x88ecf76e);
				parent.rect(x1, y1, width, width);
			}
		}
		
		long i = 1L << (l * 8 + n);
		
		if ((exists & i) != 0) {
			PShape img;
			
			if ((white & i) != 0)
				img = parent.loadShape(board[l * 8 + n].getPieceFile(WHITE));
			else

				img = parent.loadShape(board[l * 8 + n].getPieceFile(BLACK));
			
			parent.shape(img, x1, y1, width, width);
		}
	}

	public void click(int mouseX, int mouseY) {
		int n = (mouseX - 100) / width;
		int l = (mouseY - 100) / width;
		
		if (isPromotionIndex(n, l)) {
			promotionClick = l - 2;
			return;
		}
		
		if (turn)
			l = 7 - l;
		else
			n = 7 - n;

		if (Board.isValidIndex(n, l)) {
			if (canMoveFrom(n, l, turn)) {
				lastClick = new Point(n, l);
				lastClickMoves = getAllEndPoints(n, l);
//				System.out.println(lastClickMoves[0] + ", " + lastClickMoves[1]);
			}
			else if (lastClick != null && !(lastClick.x == n && lastClick.y == l)) {
				long side = turn ? 1 : -1;
				
				l += isPromoting(lastClick.x, lastClick.y) ? (side * promotionClick + 1) : 0;
				
				boolean hasMoved = move(lastClick.x, lastClick.y, n, l) != null;
				
				if (hasMoved) {
					lastClick = null;
					lastClickMoves = null;
					promotionClick = 0;
					
					if (getAllMoves(turn).size() == 0) {
						gameOver = true;
					}
				}
			}
		}
	}
	
	public boolean undo() {
		if (lastMoves.isEmpty())
			return false;
		
		revert();
		
		return true;
	}
	
	public boolean redo() {
		if (futureMoves.isEmpty())
			return false;
		
		restore();
		
		return true;
	}
	
	private boolean isPromotionIndex(int n, int l) {
		return n == 8 && l >= 2 && l <= 5;
	}
}
