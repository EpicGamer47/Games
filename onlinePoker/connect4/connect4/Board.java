package connect4;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Scanner;

public class Board {
	final static int width = 1000, height = 1000;
	final static int winLength = 4;
	final static int[][] offsets = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
	
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
	
	private boolean isValidPos(int row, int col) {
		return (row >= 0 && row < width) && (col >= 0 && col < height);
	}
	
	/**
	 * CALL DIRECTLY AFTER MOVE
	 */
	public boolean checkIfWon(int r) {
		if (!isValidRow(r))
			return false;

		int h = board[r] - 1;
		
		return checkIfWon(r, h);
	}
	
	
	/**
	 * Precondition: valid input, (r, h) is filled, (r, h) is valid
	 */
	private boolean checkIfWon(int r, int h) {
		boolean turn = white.get(r + h * width);
		BitSet set = turn ? white : black;
		
		for (int[] o : offsets) {
			int sum = 0;
			
			int sR = r - (winLength - 1) * o[0],
				sL = h - (winLength - 1) * o[1];
			int fR = sR, fL = sL;
			
			int i = 0, j = 0;
			while (i < winLength && j < winLength * 2 - 1) {
				if (isValidPos(fR, fL)) {
					i++;
					if (set.get(fR + fL * width))
						sum++;
				}
				else {
					sR += o[0];
					sL += o[1];
				}
				
				j++;
				fR += o[0];
				fL += o[1];
			}
			
			if (i < winLength)
				continue;
			
			while (j < winLength * 2 - 1 && isValidPos(fR, fL)) {
				if (sum == winLength)
					return true;
				
				if (set.get(sR + sL * width))
					sum--;
				
				if (set.get(fR + fL * width))
					sum++;
				
				sR += o[0];
				sL += o[1];
				fR += o[0];
				fL += o[1];
			}
			
			if (sum == winLength)
				return true;
		}
		
		return false;
	}
	
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		int wlen = Integer.toString(width + 1).length();
		int hlen = Integer.toString(height + 1).length();
		
		for (int i = 1; i < wlen; i++) {
			out.append(" ");
		}
		
		for (int i = 1; i <= width; i++) {
			out.append(String.format("   %-" + wlen + "d", i));
		}
		
		out.append("\n");
		
		String underScore = "____";
		
		for (int i = 1; i < wlen; i++) {
			underScore += "_";
			out.append("_");
		}
		
		for (int i = 0; i < width; i++) {
			out.append(underScore);
		}
		
		out.append("\n");
		
		for (int h = height - 1; h >= 0; h--) {
			int dh = h + 1;
			
			out.append(String.format("%-" + hlen + "d|", dh));
			
			for (int r = 0; r < width; r++) {
				if (board[r] > h) {
					if (white.get(r + h * width))
						out.append(String.format("%-" + (wlen) + "sX, ", ""));
					else
						out.append(String.format("%-" + (wlen) + "sO, ", ""));
				}
				else
					out.append(String.format("%-" + (wlen + 1) + "s, ", ""));
			}
			
			out.setCharAt(out.length() - 2, '|');
			out.setCharAt(out.length() - 1, '\n');
		}
		
		String overScore = "‾‾‾‾";
		
		for (int i = 1; i < wlen; i++) {
			overScore += "‾";
			out.append("‾");
		}
		
		for (int i = 0; i < width; i++) {
			out.append(overScore);
		}
		
		return out.toString();
	}

	@SuppressWarnings("resource")
	public static void main(String[] aggs) {
		var b = new Board();
		var in = new Scanner(System.in);
		
		while (true) {
			buffer();
			System.out.println((b.turn ? "X's" : "O's") + " Turn");
			System.out.println(b.toString());
			System.out.println("nextMove:");
			
			int r = in.nextInt() - 1;
//			System.out.println(b.move(r));
			b.move(r);
			
			if (b.checkIfWon(r)) {
				System.out.println("You won! Congratulations!");
				break;
			}
			
			
		}
	}
	
	private static void buffer() {
		System.out.print("\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=\n\n");
	}
	
	public static String centerString(String text, int len){
	    String out = String.format("%"+len+"s%s%"+len+"s", "",text,"");
	    float mid = (out.length()/2);
	    float start = mid - (len/2);
	    float end = start + len; 
	    return out.substring((int)start, (int)end);
	}
}
