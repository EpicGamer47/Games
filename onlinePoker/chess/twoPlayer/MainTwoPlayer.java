package twoPlayer;

import processing.core.PApplet;

public class MainTwoPlayer extends PApplet {
	private ProcessingBoard2 b;
	
	public static void main(String[] args) {
		PApplet.main("twoPlayer.MainTwoPlayer");
//		System.out.println(Arrays.deepToString(AllMoves.moves.single));
//		System.out.println(Arrays.deepToString(AllMoves.moves.repeat));
//		System.out.println(Arrays.deepToString(AllMoves.moves.attack));
	}
	
	@Override
	public void settings() {
		size(1600, 800);
	}

	@Override
	public void setup() {
		b = new ProcessingBoard2(this);
		frameRate(30);
		rectMode(CORNER);
	}
	
	@Override
	public void draw() {
		fill(0xFFDDDDDD);
		rect(0, 0, width / 2, height);
		fill(0xFF222222);
		rect(width / 2, 0, width / 2, height);
		
		int w = ProcessingBoard2.width;
		fill(0xFF333333);
		rect(width - 100 - w * 9, 100 - w, w * 10, w * 10);
		fill(0xFFCCCCCC);
		rect(100 - w, 100- w, w * 10, w * 10);
		
		b.draw();
	}
	
	@Override
	public void mouseClicked() {
		b.click(mouseX, mouseY);
	}
	
	@Override
	public void keyPressed() {

	}
}
