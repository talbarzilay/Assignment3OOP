package GameLogic;

import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * This class represent a board
 */
public class Board implements IBoard
{
	/**
	 * holds the tiles if this board
	 */
	private ITile[][] _boardMatrix;
	
	/**
	 * Does this board was solved already?
	 */
	private boolean _solved;
	
	/**
	 * A constructor
	 * @param tiles the boards
	 * @param picOfEmpty the image that should go to the empty tile
	 * @param shuffleRandonly true of this board should shuffle itself, false otherwise
	 */
	public Board(ITile[][] tiles, boolean shuffleRandonly){
			
		VerifyBoard(tiles); // Input checking for the board
		
		_boardMatrix = tiles;	
		_solved = false;
		
		boolean shuffled = false;	
		
		if(shuffleRandonly)
		{			
			while(!shuffled) // just to make sure the board is truly shuffled
			{		
				shuffle();
				shuffled = !isSolved();                          
			}
		}
		else 
		{
			if(isComplete())
				throw new IllegalArgumentException("The boaard is already solved!"); 
		}
	}
	
	/**
	 * A constructor
	 * @param tiles the boards
	 * @param picOfEmpty the image that should go to the empty tile
	 */
	public Board(ITile[][] tiles){
		this(tiles, false);
	}
	/**
         * 
         * @return the board size
         */
	@Override
	public int boardSize() {
		return _boardMatrix.length;
	}
        /**
         * gets a tile position and swaps it with the empty tile
         * @param tile to move
         * @return the emptyTile Position
         */
	@Override
	public int moveTile(int tile) {
		if(isLocked())
			throw new IllegalStateException("A change cannot be made to this board since it is already solved!"); 
		
		List<Integer> neighbors = neighborsOf(tile);
		
		for(int neighbor : neighbors)
		{
			if(isTheEmptyTile(neighbor))
			{
				swap(tile, neighbor);						
				isComplete(); //Lock if solved				
				return neighbor;
			}
		}
            
		throw new IllegalArgumentException("This tile is not a neighbor of the empty tile, and therefore, could not be moved!"); 
           
	}
        
        /**
         * 
         * @return true if the board is solved
         */
	@Override
	public boolean isComplete() {
			
		if(isSolved())
		{
			lock(); 	 // Lock the board - it is solved           
			return true; // All tiles in place
		}
		
		return false;
	}
	
	// Check ifthe board is solved
	private boolean isSolved() {
		
		int currentPlace = 0;
		
		if(isLocked())
			return true;
		
		for(ITile[] row : _boardMatrix)
		{		
			for(ITile tile : row)
			{							
				if(tile.desirablePosition() != currentPlace)
					return false;
				
				currentPlace++;
			}
		}    
		return true; // All tiles in place
	}
	
        /**
         * @return an array of Tiles that reprresent the board
         */
	@Override
	public ITile[][] boardMap() {
		return _boardMatrix;
	}
	
	/**
	 * check if this board can be changed
	 * @return true if this board was not solved yet, false otherwise
	 */
	private boolean isLocked(){
		return _solved;
	}
	
	/**
	 * lock this board to changes
	 */
	private void lock(){
		_solved = true;
		int size = boardSize() - 1;
		((EmptyTile)_boardMatrix[size][size]).gameEnded();
	}
	
	/**
	 * check if a board is legal
	 * @param board the board to check
	 * @throws IllegalArgumentException if the board is not legal
	 */
	private void VerifyBoard(ITile[][] board) throws IllegalArgumentException
	{
		int size;
		int min; //We want to verify that all tiles are in the range of the board
		int max;
		boolean emptyFound = false;							// Exactly one empty tile is allowed
		List<Integer> usedDesirablePlaces = new Vector<>(); // save the Desirable already taken. two tiles can't go to the same place
				
		if(board == null)
			throw new IllegalArgumentException("The board is null!");
		
		size = board.length;
		max = -1;
		min = size*size + 5;
		
		if(size == 0)
			throw new IllegalArgumentException("The board is empty!");
		
		
		for(ITile[] row : board)
		{
			if(row == null)
				throw new IllegalArgumentException("The board have a row that is null!");
			
			if(row.length != size)
				throw new IllegalArgumentException("The board have a row that is have a diffrent leangth that the baord size!");
			
			
			for(ITile tile : row)
			{
				if(tile == null)
					throw new IllegalArgumentException("The board have a null tile!");
				
				if(usedDesirablePlaces.contains(tile.desirablePosition()))
					throw new IllegalArgumentException("The board have two tiles with the same desirable position!");
				
				usedDesirablePlaces.add(tile.desirablePosition());
				min = Math.min(min, tile.desirablePosition());
				max = Math.max(max, tile.desirablePosition());
				
				if(isTheEmptyTile(tile))
				{
					if(emptyFound)
						throw new IllegalArgumentException("The board have more than one empty tiles!");
					
					if(tile.desirablePosition() != board.length*board.length -1) //not the right-down corner
						throw new IllegalArgumentException("The desirable position of the empty tile is nit the right corner!");
					
					emptyFound = true;
				}
			}
		}
		
		if(!emptyFound)
			throw new IllegalArgumentException("The board have no empty tile!");
		
		if(min != 0 | max != size*size - 1)
			throw new IllegalArgumentException("The tiles on this board are not in it's range!!");
	}
	
	/**
	 * get the tile with the given position
	 * @param num the position of the tile
	 * @return the tile at the given position
	 */
	private ITile getTile(int num){		
		return _boardMatrix[getTileRow(num)][getTileCol(num)];
	}
	
	/**
	 * calculate the row of the n tile
	 * @param num the position of the tile
	 * @return the row that this tile is in
	 */
	public int getTileRow(int num){
		return num / boardSize();
	}
	
	/**
	 * calculate the column of the n tile
	 * @param num the position of the tile
	 * @return the column that this tile is in
	 */
	public int getTileCol(int num){
		return num % boardSize();
	}
	
	/**
	 * Check if a given tile is the empty tile
	 * @param tile the tile to check
	 * @return true if this position is not occupied by any tile, false otherwise
	 */
	private boolean isTheEmptyTile(ITile tile){
		return tile instanceof EmptyTile;
	}
	
	/**
	 * Check if a given position is the empty tile
	 * @param pos the position to check
	 * @return true if this position is the empty tile, false otherwise
	 */
	private boolean isTheEmptyTile(int pos){
		return isTheEmptyTile(getTile(pos));
	}
	
	/**
	 * return the neighbors of a certain tile
	 * @param tile the number of the tile
	 * @return a list that contains the neighbors of the tile
	 */
	private List<Integer> neighborsOf(int tile){
			
		int size = boardSize();
		List<Integer> neighbors = new Vector<>();	
		
		if(tile < 0 | tile  > size*size - 1 )
			throw new IllegalArgumentException("The input is out of range!");
		
		if(getTileCol(tile) > 0)
			neighbors.add(tile - 1); // Left
		
		if(getTileCol(tile) < size-1)
			neighbors.add(tile + 1); // Right
		
		if(getTileRow(tile) > 0)
			neighbors.add(tile - size); // Up
		
		if(getTileRow(tile) < size -1)
			neighbors.add(tile + size); // Down
		
		return neighbors;
	}
	
	/**
	 * swap two tiles in the board
	 * @param tileNum
	 * @param newPos
	 */
	private void swap(int tile1, int tile2) {
		
		ITile tTile1 =  _boardMatrix[getTileRow(tile1)][getTileCol(tile1)];
		ITile tTile2 =  _boardMatrix[getTileRow(tile2)][getTileCol(tile2)];
		
		_boardMatrix[getTileRow(tile1)][getTileCol(tile1)] = tTile2;
		_boardMatrix[getTileRow(tile2)][getTileCol(tile2)] = tTile1;
	}
	
	/**
	 * Randomly shuffle The board
	 */
	private void shuffle() {
		
		int empty = boardSize()*boardSize() - 1;
		
		if(!isTheEmptyTile(empty))
			throw new IllegalArgumentException("This board is alreadt shuffuled!");
		
		Random rnd = new Random();
		List<Integer> neighbors;
		int swapWith;
		int numOfShuffles = rnd.nextInt(boardSize()*boardSize()*boardSize()*boardSize()*boardSize()); // number of times an automatic move will be made
		
		for(int i=1; i<= numOfShuffles; i++)
		{
			neighbors = neighborsOf(empty);
			swapWith = neighbors.get(rnd.nextInt(neighbors.size())); //Getting a random neighbor of the empty tile
			
			swap(swapWith, empty);			
			empty = swapWith; // Empty Tile moved
		}	
		
	}
        
        /**
         * 
         * @return the position of the emptyTile
         */
        public int getEmptyTilePosition(){
            for(int i=0;i<_boardMatrix.length;i++)
                for(int j=0;j<_boardMatrix.length;j++)
                    if(isTheEmptyTile(_boardMatrix[i][j]))
                        return i*_boardMatrix.length+j;
            throw new IllegalArgumentException("no such tile");
        }
}
