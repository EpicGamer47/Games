package common;

public abstract class AI {
	protected Board b;
	
	public AI(Board b) {
		this.b = b;
	}
	
	/**
	 * @return true if a move was made and the game continues,
	 * false if the ai refuses to make a move 
	 * (for checkmate, stalemate, insufficient material, etc)
	 */
	public abstract boolean makeAMove();
}
