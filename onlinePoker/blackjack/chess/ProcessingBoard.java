package chess;

import static chess.Piece.WHITE;
import static chess.Piece.BLACK;


import java.awt.Point;

import processing.core.PApplet;
import processing.core.PShape;

public class ProcessingBoard extends Board {
	private static final int width = 75;
	
	private PApplet parent;
	private Point lastClick = null;
	private boolean isPlayerWhite;
	
	public ProcessingBoard(PApplet parent) {
		super(true);
		this.parent = parent;
		isPlayerWhite = true;
	}
	
	public ProcessingBoard(PApplet parent, boolean playerColor) {
		super(true);
		this.parent = parent;
		isPlayerWhite = playerColor;
	}
	
	public void draw() {
		parent.rectMode(PApplet.CORNER);
		parent.shapeMode(PApplet.CORNER);
		
		for (int l = 0; l < 8; l++) {
			for (int n = 0; n < 8; n++) {
				drawSquare(n, l);
			}
		}
	}

	private void drawSquare(int n, int l) {
		int dN, dL;
		
		if (isPlayerWhite) {
			dN = n;
			dL = 7 - l;
		}
		else {
			dN = 7 - n;
			dL = l;
		}
		
		int x1 = 100 + width * (dN);
		int y1 = 100 + width * (dL);
		
		int color = (l + n) % 2 == 0 ? 0xFFeeeed2 : 0xFF769656;
		
		parent.fill(color);
		parent.rect(x1, y1, width, width);
		
		if (lastClick != null && 
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
		int n = (mouseX - 100) / width, l = (mouseY - 100) / width;

		if (isPlayerWhite)
			l = 7 - l;
		else
			n = 7 - n;
		
		if (Board.isValidIndex(n, l)) {
			if (lastClick == null) {
				if (!canMoveFrom(n, l))
					return;
				
				lastClick = new Point(n, l);
			}
			else if (!(lastClick.x == n && lastClick.y == l)) {
				move(lastClick.x, lastClick.y, n, l);
				lastClick = null;
			}
		}
	}
}
