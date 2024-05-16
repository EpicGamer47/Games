package TwoPlayer;

import static TwoPlayer.Piece.BLACK;
import static TwoPlayer.Piece.WHITE;

import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import processing.core.PApplet;
import processing.core.PShape;

public class ProcessingBoard extends Board {
	static final int width = 75;
	
	private PApplet parent;
	private Point lastClick;
	private List<Point> lastClickMoves;
	private boolean isPlayerWhite;
	
	public ProcessingBoard(PApplet parent) {
		super(true);
		this.parent = parent;
		isPlayerWhite = true;
		
		lastClick = null;
		lastClickMoves = Collections.emptyList();
	}
	
	public ProcessingBoard(PApplet parent, boolean playerColor) {
		super(true);
		this.parent = parent;
		isPlayerWhite = playerColor;

		lastClick = null;
		lastClickMoves = Collections.emptyList();
	}
	
	public void draw() {
		parent.rectMode(PApplet.CORNER);
		parent.shapeMode(PApplet.CORNER);
		parent.ellipseMode(PApplet.CENTER);
		parent.noStroke();
		
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
	}

	private void drawDots(boolean side) {
		for (Point p : lastClickMoves) {
			float x1, y1;
			
			if (side) {
				x1 = 100 + width * (p.x + 0.5f);
				y1 = 100 + width * (7 - p.y + 0.5f);
			}
			else {
				x1 = (parent.width - 100 - width * 8) + width * (7 - p.x + 0.5f);
				y1 = 100 + width * (p.y + 0.5f);
			}
			
			long i = 1L << (p.x + p.y * 8);
			
			if ((exists & i) != 0)
				parent.fill(0x77FF0000);
			else
				parent.fill(0x77000000);
			
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
		
		int color = (l + n) % 2 == 0 ? 0xFFeeeed2 : 0xFF769656;
		
		parent.fill(color);
		parent.rect(x1, y1, width, width);
		
		if (side == turn &&
				lastClick != null && 
				n == lastClick.x && l == lastClick.y) {
			parent.fill(0xFF03ac14);
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
			l = 7 - l;
		}
		else {
			n = (mouseX - (parent.width - 100 - width * 8)) / width;
			l = (mouseY - 100) / width;
			n = 7 - n;
		}

		if (Board.isValidIndex(n, l)) {
			if (canMoveFrom(n, l)) {
				lastClick = new Point(n, l);
				lastClickMoves = getAllEndPoints(n, l);
			}
			else if (lastClick != null && !(lastClick.x == n && lastClick.y == l)) {
				if (move(lastClick.x, lastClick.y, n, l)) {
					lastClick = null;
					lastClickMoves = Collections.emptyList();
				}
			}
		}
	}
}
