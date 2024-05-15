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
				drawSquare(n, l, 100, 100, true);
				
				drawSquare(n, l, parent.width - 100 - width * 8, 100, false);
			}
		}
		
		parent.fill(0x55000000);
		
		if (turn)
			parent.rect(parent.width - 100 - width * 8, 100, width * 8, width * 8);
		else
			parent.rect(100, 100, width * 8, width * 8);
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
