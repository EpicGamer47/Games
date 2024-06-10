package connect4;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Scanner;

public class Board {
	final static int width = 7, height = 6;
	
	final static int[][] offsets = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
	final static int winLength = 4;
	
	public BitSet white, black;
	public int[] board;
	public boolean turn;
	
	public Board() {
		board = new int[width];
		white = new BitSet(width * height);
		black = new BitSet(width * height);
		
		reset();
	}

	private void reset() {
		Arrays.fill(board, 0);
		white.clear();
		black.clear();
		
		turn = true;
	}
	
	public boolean move(int row) {
		if (!isValidRow(row))
			return false;
		
		if (board[row] > height)
			return false;
		
		if (turn)
			white.set(row + width * board[row]++);
		else
			black.set(row + width * board[row]++);
		
		turn = !turn;
		
		return true;
	}

	private boolean isValidRow(int row) {
		return row >= 0 && row < width;
	}
	
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		for (int i = 1; i <= width; i++) {
			out.append("  " + i + " ");
		}
		
		out.append("\n");
		
		for (int i = 0; i < width; i++) {
			out.append("____");
		}
		
		out.append("\n");
		
		for (int h = height - 1; h >= 0; h--) {
			int dh = h + 1;
			
			out.append(dh + "|");
			
			for (int r = 0; r < width; r++) {
				if (board[r] > h) {
					if (white.get(r + h * width))
						out.append(" O, ");
					else
						out.append(" X, ");
				}
				else
					out.append("  , ");
			}
			
			out.setCharAt(out.length() - 2, '|');
			out.setCharAt(out.length() - 1, '\n');
		}
		
		for (int i = 0; i < width; i++) {
			out.append("‾‾‾‾");
		}
		
		return out.toString();
	}
	
	/**
	 * CALL DIRECTLY AFTER MOVE
	 */
	public boolean checkIfWon(int r) {
		if (!isValidRow(r))
			return false;

		int h = board[r] - 1;
		boolean turn = white.get(r + h * width);
		
		
	}
	
	public static void main(String[] aggs) {
		var b = new Board();
		var in = new Scanner(System.in);
		
		while (true) {
			System.out.println(b.toString());
			
			System.out.println("nextMove:");
			
			int r = in.nextInt() - 1;
			
			System.out.println(b.move(r));
		}
	}
}
