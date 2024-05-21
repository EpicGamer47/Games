package twoPlayer;

import static common.Piece.*;

import java.awt.Point;
import java.util.List;

import common.Board;
import common.Piece;
import processing.core.PApplet;
import processing.core.PShape;

@SuppressWarnings("exports")
public class ProcessingBoard2 extends Board {
	public static final int width = 75;
	public static final Piece[] promotionPieces = {QUEEN, ROOK, BISHOP, KNIGHT};
	
	private PApplet parent;
	private Point lastClick;
	private Piece promotionClick;
	private List<Point>[] lastClickMoves;
	private boolean gameOver;
	
	public ProcessingBoard2(PApplet parent) {
		super();
		this.parent = parent;
		
		lastClick = null;
		lastClickMoves = null;
		
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
		drawBackground();
		
		for (int l = 0; l < 8; l++) {
			for (int n = 0; n < 8; n++) {
				drawSquare(n, l, 100, 100, true);
				drawSquare(n, l, parent.width - 100 - width * 8, 100, false);
			}
		}
		
		parent.fill(0x77000000);
		
		if (turn)
			parent.rect(parent.width - 100 - width * 8, 100, width * 8, width * 8);
		else
			parent.rect(100, 100, width * 8, width * 8);
		
		drawDots(turn);
		
		if (lastClick != null &&  isPromoting(lastClick.x, lastClick.y)) {
			if (turn)
				drawPromotion(100, 100, WHITE);
			else
				drawPromotion(parent.width - 100 - width * 8, 100, BLACK);
		}
		
		if (gameOver)
			gameOver();
	}

	private void drawPromotion(int x, int y, int color) {
		x += 8 * width;
		y += 2 * width;
		
		for (var p : promotionPieces) {
			int tile = (y) % 2 == 0 ? 0xFFb0b0a9 : 0xFF4d6138;
			
			parent.fill(tile);
			parent.rect(x, y, width, width);
			
			var img = parent.loadShape(p.getPieceFile(color));
			parent.shape(img, x, y, width, width);
			
			y += width;
		}
	}

	private void drawBackground() {
		parent.fill(0xFFDDDDDD);
		parent.rect(0, 0, parent.width / 2, parent.height);
		parent.fill(0xFF222222);
		parent.rect(parent.width / 2, 0, parent.width / 2, parent.height);
		
		int w = width;
		parent.fill(0xFF333333);
		parent.rect(parent.width - 100 - w * 9, 100 - w, w * 10, w * 10);
		parent.fill(0xFFCCCCCC);
		parent.rect(100 - w, 100- w, w * 10, w * 10);
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
			
//			long i = 1L << (p.x + p.y * 8);
			
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
			
//			long i = 1L << (p.x + p.y * 8);
			
			parent.fill(0x77FF0000);
			
			parent.ellipse(x1, y1, width * 0.33f, width * 0.33f);
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
		
		if (lastMove != null && 
				((lastMove[0] == n && lastMove[1] == l) || 
						(lastMove[2] == n && lastMove[3] == l))) {
			parent.fill(0x88ecf76e);
			parent.rect(x1, y1, width, width);
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
		int n, l;

		if (turn) {
			n = (mouseX - 100) / width;
			l = (mouseY - 100) / width;
		}
		else {
			n = (mouseX - (parent.width - 100 - width * 8)) / width;
			l = (mouseY - 100) / width;
		}
		
		if (isPromotionIndex(n, l)) {
			promotionClick = promotionPieces[l - 2];
			return;
		}
		
		if (turn)
			l = 7 - l;
		else
			n = 7 - n;

		if (Board.isValidIndex(n, l)) {
			if (canMoveFrom(n, l)) {
				lastClick = new Point(n, l);
				lastClickMoves = getAllEndPoints(n, l);
//				System.out.println(lastClickMoves[0] + ", " + lastClickMoves[1]);
			}
			else if (lastClick != null && !(lastClick.x == n && lastClick.y == l)) {
				if (move(lastClick.x, lastClick.y, n, l)) {
					lastClick = null;
					lastClickMoves = null;
					
					if (getAllMoves(turn).size() == 0) {
						gameOver = true;
					}
				}
			}
		}
	}
	
	private boolean isPromotionIndex(int n, int l) {
		return n == 8 && l >= 2 && l <= 5;
	}
}
