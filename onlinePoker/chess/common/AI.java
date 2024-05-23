package common;

public interface AI {
	/**
	 * @return true if a move was made and the game continues,
	 * false if the ai refuses to make a move 
	 * (for checkmate, stalemate, insufficient material, etc)
	 */
	public boolean makeAMove();
}
