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
		b = new ProcessingBoard(this);
		frameRate(30);
	}
	
	@Override
	public void draw() {	
		background(0xFF222222);
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
