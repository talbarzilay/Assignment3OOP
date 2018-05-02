
/**
 * This interface represent the game
 */
public interface IGame extends IBoard {
	
	/**
	 * check if there is a move to be undo
	 * @return true id can undo, false otherwise
	 */
	boolean undoable();
	
	/**
	 * Undo the last move
	 */
	void undo();
	
	/**
	 * get the amount of moves that were made in this board
	 * @returnThe amount of moves that were made in this board
	 */
	int moves();
}