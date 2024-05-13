package chess;

import java.awt.Point;

import processing.core.PApplet;

public class Main extends PApplet {
	private ProcessingBoard b;
	
	public static void main(String[] args) {
		PApplet.main("chess.Main");
	}
	
	@Override
	public void settings() {
		size(1600, 900);
	}

	@Override
	public void setup() {
		b = new ProcessingBoard(this, true);
		frameRate(30);
	}
	
	@Override
	public void draw() {	
		background(0xFF222222);
		b.draw();
	}
	
	@Override
	public void mouseClicked() {
		int n = (mouseX - 100) / 75, l = (mouseY - 100) / 75;
		
		if (Board.isValidIndex(n, l)) {
			if (b.p == null || (b.p.x == n && b.p.y == l)) {
				b.p = new Point(n, l);
			}
			else {
				b.move(b.p.x, b.p.y, n, l);
				b.p = null;
			}
		}

	}
	
	@Override
	public void keyPressed() {

	}
}
