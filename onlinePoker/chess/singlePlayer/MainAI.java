package singlePlayer;

import processing.core.PApplet;

public class MainAI extends PApplet {
	private ProcessingBoard1 b;
	
	public static void main(String[] args) {
		PApplet.main("singlePlayer.MainAI");
//		System.out.println(Arrays.deepToString(AllMoves.moves.single));
//		System.out.println(Arrays.deepToString(AllMoves.moves.repeat));
//		System.out.println(Arrays.deepToString(AllMoves.moves.attack));
	}
	
	@Override
	public void settings() {
		size(800, 800);
	}

	@Override
	public void setup() {
		b = new ProcessingBoard1(this, true);
		frameRate(30);
		rectMode(CORNER);
	}
	
	@Override
	public void draw() {		
		b.draw();
	}
	
	@Override
	public void mouseClicked() {
		b.click(mouseX, mouseY);
	}
	
	@Override
	public void keyPressed() {
		if (key == 'u')
			b.undo();
		else if (key == 'r')
			b.restore();
	}
}
