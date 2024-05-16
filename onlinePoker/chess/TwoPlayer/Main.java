package TwoPlayer;

import java.awt.Point;

import processing.core.PApplet;

public class Main extends PApplet {
	private ProcessingBoard b;
	
	public static void main(String[] args) {
		PApplet.main("TwoPlayer.Main");
	}
	
	@Override
	public void settings() {
		size(1600, 800);
	}

	@Override
	public void setup() {
		b = new ProcessingBoard(this);
		frameRate(30);
		rectMode(CORNER);
	}
	
	@Override
	public void draw() {
		fill(0xFFDDDDDD);
		rect(0, 0, width / 2, height);
		fill(0xFF222222);
		rect(width / 2, 0, width / 2, height);
		
		int w = ProcessingBoard.width;
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
