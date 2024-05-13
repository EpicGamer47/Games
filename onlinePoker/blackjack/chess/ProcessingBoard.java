package chess;

import static chess.Piece.BLACK;
import static chess.Piece.WHITE;

import java.awt.Point;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class ProcessingBoard extends Board {
	PApplet parent;
	Point p = null;
	
	public ProcessingBoard(PApplet parent) {
		super();
		this.parent = parent;
	}
	
	public ProcessingBoard(PApplet parent, boolean fill) {
		super(fill);
		this.parent = parent;
	}
	
	public void draw() {
		int width = 75;
		
		parent.rectMode(PApplet.CORNER);
		parent.shapeMode(PApplet.CORNER);
		
		
		
		for (int l = 7; l >= 0; l--) {
			for (int n = 0; n < 8; n++) {
				int x1 = 100 + width * n;
				int y1 = 100 + width * l;
				
				int color = (l + n) % 2 == 0 ? 0xFFeeeed2 : 0xFF769656;
				
				parent.fill(color);
				parent.rect(x1, y1, width, width);
				
				if (p != null && n == p.x && l == p.y) {
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
		}
	}
}
