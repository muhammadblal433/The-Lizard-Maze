package hw3;

import java.util.ArrayList;
import api.BodySegment;
import api.Cell;
import api.Direction;
import api.Exit;
import api.ScoreUpdateListener;
import api.ShowDialogListener;
import api.Wall;

/**
 * Class that models a game.
 * 
 * @author Muhammad Blal
 */
public class LizardGame {
	/**
	 * Listener for displaying dialogs to the user, such as game win messages.
	 */
	private ShowDialogListener dialogListener;

	/**
	 * Listener for updating the player's score, typically used when the number of
	 * lizards changes.
	 */
	private ScoreUpdateListener scoreListener;

	/**
	 * A 2D array representing the game grid. Each element is a Cell object that
	 * holds information about its state, like whether it contains a lizard, a wall,
	 * or an exit.
	 */
	private Cell[][] grid;

	/**
	 * A list that stores all the Lizard objects currently in the game. This list is
	 * used to track and manage lizards within the game environment.
	 */
	private ArrayList<Lizard> lizards;

	/**
	 * The width of the game grid, measured in the number of columns.
	 */
	private int width;

	/**
	 * The height of the game grid, measured in the number of rows.
	 */
	private int height;

	/**
	 * Constructs a new LizardGame object with given grid dimensions.
	 * 
	 * @param width  number of columns
	 * @param height number of rows
	 */
	public LizardGame(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new Cell[height][width];
		lizards = new ArrayList<>();
		inputGrid(); // Inputs the grid with empty cells
	}

	/**
	 * Private helper method that inputs the game grid by populating it with Cell
	 * objects. Each cell is created at its respective location within the grid.
	 */
	private void inputGrid() {
		// Iterate over each row of the grid
		for (int i = 0; i < width; ++i) {
			// Iterate over each column within the current row
			for (int j = 0; j < height; ++j) {
				// Create a new Cell object for the current row and column
				// and assign it to the appropriate location in the grid
				grid[j][i] = new Cell(i, j);
			}
		}
	}

	/**
	 * Get the grid's width.
	 * 
	 * @return width of the grid
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the grid's height.
	 * 
	 * @return height of the grid
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Adds a wall to the grid.
	 * <p>
	 * Specifically, this method calls placeWall on the Cell object associated with
	 * the wall (see the Wall class for how to get the cell associated with the
	 * wall). This class assumes a cell has already been set on the wall before
	 * being called.
	 * 
	 * @param wall to add
	 */
	public void addWall(Wall wall) {
		wall.getCell().placeWall(wall);
	}

	/**
	 * Adds an exit to the grid.
	 * <p>
	 * Specifically, this method calls placeExit on the Cell object associated with
	 * the exit (see the Exit class for how to get the cell associated with the
	 * exit). This class assumes a cell has already been set on the exit before
	 * being called.
	 * 
	 * @param exit to add
	 */
	public void addExit(Exit exit) {
		exit.getCell().placeExit(exit);
	}

	/**
	 * Gets a list of all lizards on the grid. Does not include lizards that have
	 * exited.
	 * 
	 * @return lizards list of lizards
	 */
	public ArrayList<Lizard> getLizards() {
		return lizards;
	}

	/**
	 * Adds the given lizard to the grid.
	 * <p>
	 * The scoreListener to should be updated with the number of lizards.
	 * 
	 * @param lizard to add
	 */
	public void addLizard(Lizard lizard) {
		lizards.add(lizard);
		if (scoreListener != null) {
			scoreListener.updateScore(lizards.size()); // assuming ScoreUpdateListener has updateScore method
		}
	}

	/**
	 * Removes the given lizard from the grid. Be aware that each cell object knows
	 * about a lizard that is placed on top of it. It is expected that this method
	 * updates all cells that the lizard used to be on, so that they now have no
	 * lizard placed on them.
	 * <p>
	 * The scoreListener to should be updated with the number of lizards using
	 * updateScore().
	 * 
	 * @param lizard to remove
	 */
	public void removeLizard(Lizard lizard) {
		// Iterate through all segments of the lizard.
		for (BodySegment segment : lizard.getSegments()) {
			// For each segment, get the cell it occupies and remove the lizard from that
			// cell.
			segment.getCell().removeLizard();
		}

		// Remove the lizard from the list of lizards in the game.
		// This effectively removes the lizard from the game's tracking.
		lizards.remove(lizard);

		// If there is a score listener set (which might be responsible for keeping
		// track of game score), update the score based on the new number of lizards in
		// the game.
		// This could be relevant for game mechanics or UI updates.
		if (scoreListener != null) {
			scoreListener.updateScore(lizards.size());
		}
	}

	/**
	 * Gets the cell for the given column and row.
	 * <p>
	 * If the column or row are outside of the boundaries of the grid the method
	 * returns null.
	 * 
	 * @param col column of the cell
	 * @param row of the cell
	 * @return the cell or null
	 */
	public Cell getCell(int col, int row) {
		// Check if the given column or row is out of the grid's boundaries.
		// This includes checking if the row or column is negative,
		// or if they exceed the grid's dimensions.
		if (col < 0 || row < 0 || row >= getHeight() || col >= getWidth()) {
			// If the cell is out of bounds, return null.
			// This is a safeguard to prevent accessing an invalid array index.
			return null;
		}

		// If the column and row are within the grid's boundaries,
		// return the cell located at that position in the grid.
		// The grid is accessed with row and column indices.
		return grid[row][col];
	}

	/**
	 * Gets the cell that is adjacent to (one over from) the given column and row,
	 * when moving in the given direction. For example (1, 4, UP) returns the cell
	 * at (1, 3).
	 * <p>
	 * If the adjacent cell is outside of the boundaries of the grid, the method
	 * returns null.
	 * 
	 * @param col the given column
	 * @param row the given row
	 * @param dir the direction from the given column and row to the adjacent cell
	 * @return the adjacent cell or null
	 */
	public Cell getAdjacentCell(int col, int row, Direction dir) {
		// Initialize new row and column values as current ones
		int newRow = row;
		int newCol = col;

		// Adjust the new row or column based on the given direction
		if (dir == Direction.UP) {
			newRow -= 1; // Move up: decrease row index
		} else if (dir == Direction.DOWN) {
			newRow += 1; // Move down: increase row index
		} else if (dir == Direction.LEFT) {
			newCol -= 1; // Move left: decrease column index
		} else if (dir == Direction.RIGHT) {
			newCol += 1; // Move right: increase column index
		}

		// Retrieve the cell at the new position. This will return null if out of
		// bounds.
		return getCell(newCol, newRow);
	}

	/**
	 * Resets the grid. After calling this method the game should have a grid of
	 * size width x height containing all empty cells. Empty means cells with no
	 * walls, exits, etc.
	 * <p>
	 * All lizards should also be removed from the grid.
	 * 
	 * @param width  number of columns of the resized grid
	 * @param height number of rows of the resized grid
	 */
	public void resetGrid(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new Cell[height][width];
		lizards.clear();
		inputGrid();
	}

	/**
	 * Returns true if a given cell location (col, row) is available for a lizard to
	 * move into. Specifically the cell cannot contain a wall or a lizard. Any other
	 * type of cell, including an exit is available.
	 * 
	 * @param row of the cell being tested
	 * @param col of the cell being tested
	 * @return true if the cell is available, false otherwise
	 */
	public boolean isAvailable(int col, int row) {
	    // Retrieve the cell at the specified column and row.
	    // This is done by calling the getCell method, which returns the cell if it's within the grid,
	    // or null if the specified location is outside the grid boundaries.
	    Cell cell = getCell(col, row);

	    // Check the availability of the cell.
	    // Three conditions are checked:
	    // 1. The cell is not null (meaning it's a valid cell within the grid).
	    // 2. The cell does not contain a wall (cell.getWall() returns null if there's no wall).
	    // 3. The cell does not contain a lizard (cell.getLizard() returns null if there's no lizard).
	    // If all three conditions are true, the method returns true, indicating the cell is available for movement.
	    // Otherwise, it returns false.
	    return (cell != null && cell.getWall() == null && cell.getLizard() == null);
	}


	/**
	 * Move the lizard specified by its body segment at the given position (col,
	 * row) one cell in the given direction. The entire body of the lizard must move
	 * in a snake like fashion, in other words, each body segment pushes and pulls
	 * the segments it is connected to forward or backward in the path of the
	 * lizard's body. The given direction may result in the lizard moving its body
	 * either forward or backward by one cell.
	 * <p>
	 * The segments of a lizard's body are linked together and movement must always
	 * be "in-line" with the body. It is allowed to implement movement by either
	 * shifting every body segment one cell over or by creating a new head or tail
	 * segment and removing an existing head or tail segment to achieve the same
	 * effect of movement in the forward or backward direction.
	 * <p>
	 * If any segment of the lizard moves over an exit cell, the lizard should be
	 * removed from the grid.
	 * <p>
	 * If there are no lizards left on the grid the player has won the puzzle the
	 * the dialog listener should be used to display (see showDialog) the message
	 * "You win!".
	 * <p>
	 * It is possible that the given direction is not in-line with the body of the
	 * lizard (as described above), in that case this method should do nothing.
	 * <p>
	 * It is possible that the given column and row are outside the bounds of the
	 * grid, in that case this method should do nothing.
	 * <p>
	 * It is possible that there is no lizard at the given column and row, in that
	 * case this method should do nothing.
	 * <p>
	 * It is possible that the lizard is blocked and cannot move in the requested
	 * direction, in that case this method should do nothing.
	 * <p>
	 * <b>Developer's note: You may have noticed that there are a lot of details
	 * that need to be considered when implement this method method. It is highly
	 * recommend to explore how you can use the public API methods of this class,
	 * Grid and Lizard (hint: there are many helpful methods in those classes that
	 * will simplify your logic here) and also create your own private helper
	 * methods. Break the problem into smaller parts are work on each part
	 * individually.</b>
	 * 
	 * @param col the given column of a selected segment
	 * @param row the given row of a selected segment
	 * @param dir the given direction to move the selected segment
	 */
	public void move(int col, int row, Direction dir) {
		// Check if the given position is within the bounds of the grid
		if (row < 0 || col < 0 || row >= getHeight() || col >= getWidth())
			return; // Exit if the position is out of bounds

		// Retrieve the cell at the given position and the lizard on that cell
		Cell cell = getCell(col, row);
		Lizard lizard = cell.getLizard();

		// If there's no lizard at the given position, exit the method.
		if (lizard == null) {
			return; // Exit if there is no lizard at the cell
		}

		// Get the segment at the specified cell and the head and tail segments of the
		// lizard
		BodySegment selectedSegment = lizard.getSegmentAt(cell);
		BodySegment headSegment = lizard.getHeadSegment();
		BodySegment tailSegment = lizard.getTailSegment();

		// Determine the adjacent cell in the specified direction from the current
		// position
		Cell movingToCell = getAdjacentCell(col, row, dir);
		// Exit the method if the adjacent cell is not valid (out of bounds)
		if (movingToCell == null) {
			return;
		}

		// Determine the type of movement based on the segment selected
		if (selectedSegment == headSegment) {
			// Determine the segment behind the head to check movement direction
			BodySegment segmentBehind = lizard.getSegmentBehind(selectedSegment);
			if (segmentBehind.getCell() == movingToCell) {
				// Handle movement backwards
				Direction taildir = lizard.getTailDirection();
				Cell tailCell = lizard.getTailSegment().getCell();
				Cell newtailcell = getAdjacentCell(tailCell.getCol(), tailCell.getRow(), taildir);

				// If the new tail cell is valid and available, move the lizard backward
				if (newtailcell != null && isAvailable(newtailcell.getCol(), newtailcell.getRow())) {
					moveBackward(lizard, newtailcell);
				}
			} else {
				// If moving forward is possible (the adjacent cell is available), move forward
				if (isAvailable(movingToCell.getCol(), movingToCell.getRow())) {
					moveForward(lizard, movingToCell);
				}
			}
		} else if (selectedSegment == tailSegment) {
			// Handle movement when the selected segment is the tail
			BodySegment segmentAhead = lizard.getSegmentAhead(selectedSegment);
			if (segmentAhead.getCell() == movingToCell) {
				// Handle movement forwards
				Direction headdir = lizard.getHeadDirection();
				Cell headcell = lizard.getHeadSegment().getCell();
				Cell newheadcell = getAdjacentCell(headcell.getCol(), headcell.getRow(), headdir);

				// If moving forward is possible (the adjacent cell is available), move forward
				if (newheadcell != null && isAvailable(newheadcell.getCol(), newheadcell.getRow())) {
					moveForward(lizard, newheadcell);
				}
			} else {

				// If the new tail cell is valid and available, move the lizard backward
				if (isAvailable(movingToCell.getCol(), movingToCell.getRow())) {
					moveBackward(lizard, movingToCell);
				}
			}
		} else {
			// Handle movement when the selected segment is neither the head nor the tail
			BodySegment segmentAhead = lizard.getSegmentAhead(selectedSegment);
			BodySegment segmentBehind = lizard.getSegmentBehind(selectedSegment);
			if (movingToCell == segmentAhead.getCell()) {
				// Move forward if possible
				Direction headdir = lizard.getHeadDirection();
				Cell newheadcell = getAdjacentCell(headSegment.getCell().getCol(), headSegment.getCell().getRow(),
						headdir);
				if (newheadcell != null && isAvailable(newheadcell.getCol(), newheadcell.getRow())) {
					moveForward(lizard, newheadcell);
				}
			} else if (movingToCell == segmentBehind.getCell()) {
				// Move backward if possible
				Direction taildir = lizard.getTailDirection();
				Cell newtailcell = getAdjacentCell(tailSegment.getCell().getCol(), tailSegment.getCell().getRow(),
						taildir);
				if (newtailcell != null && isAvailable(newtailcell.getCol(), newtailcell.getRow())) {
					moveBackward(lizard, newtailcell);
				}
			}
		}

		// Check and handle if any segment of the lizard has reached an exit
		if (headSegment.getCell().getExit() != null || tailSegment.getCell().getExit() != null) {
			removeLizard(lizard); // Remove the lizard if it reaches an exit
		}
		if (lizards.size() == 0) {
			dialogListener.showDialog("You win!"); // Show win dialog if no lizards left
		}
	}

	/**
	 * Private helper method that moves the lizard forward by one cell. This method
	 * shifts each body segment of the lizard one position towards the head, and
	 * then moves the head to a new specified cell.
	 * 
	 * @param lizard             The lizard to be moved.
	 * @param moveHeadTowardCell The cell towards which the head of the lizard will
	 *                           move.
	 */
	private void moveForward(Lizard lizard, Cell moveHeadTowardCell) {
		// Retrieve the list of all body segments of the lizard
		ArrayList<BodySegment> segments = lizard.getSegments();

		// Obtain the tail segment of the lizard
		BodySegment tail = lizard.getTailSegment();

		// Remove the lizard from its current tail cell
		tail.getCell().removeLizard();

		int i;
		// Iterate over all segments except the head segment
		for (i = 0; i < segments.size() - 1; i++) {
			// For each segment, get the cell of the next segment in the list
			Cell moveToCell = segments.get(i + 1).getCell();

			// Set this new cell as the location of the current segment.
			// This effectively shifts each segment towards the head.
			segments.get(i).setCell(moveToCell);
		}

		// After shifting all segments, handle the head segment.
		// The head moves to the new cell specified in the method's argument.
		BodySegment head = segments.get(i);
		head.setCell(moveHeadTowardCell);
	}

	/**
	 * Private helper method that moves the lizard backward by one cell. This method
	 * shifts each body segment of the lizard one position towards the tail, and
	 * then moves the tail to a new specified cell.
	 * 
	 * @param lizard             The lizard to be moved.
	 * @param moveTailTowardCell The cell towards which the tail of the lizard will
	 *                           move.
	 */
	private void moveBackward(Lizard lizard, Cell moveTailTowardCell) {
		// Retrieve the list of all body segments of the lizard
		ArrayList<BodySegment> segments = lizard.getSegments();

		// Obtain the head segment of the lizard
		BodySegment head = lizard.getHeadSegment();

		// Remove the lizard from its current head cell
		head.getCell().removeLizard();

		int i;
		// Iterate over all segments except the tail segment
		for (i = segments.size() - 1; i > 0; i--) {
			// For each segment, get the cell of the next segment in the list
			Cell moveToCell = segments.get(i - 1).getCell();

			// Set this new cell as the location of the current segment.
			// This effectively shifts each segment towards the tail.
			segments.get(i).setCell(moveToCell);
		}

		// After shifting all segments, handle the tail segment.
		// The tail moves to the new cell specified in the method's argument.
		BodySegment tail = segments.get(i);
		tail.setCell(moveTailTowardCell);
	}

	/**
	 * Sets callback listeners for game events.
	 * 
	 * @param dialogListener listener for creating a user dialog
	 * @param scoreListener  listener for updating the player's score
	 */
	public void setListeners(ShowDialogListener dialogListener, ScoreUpdateListener scoreListener) {
		this.dialogListener = dialogListener;
		this.scoreListener = scoreListener;
	}

	/**
	 * Load the game from the given file path
	 * 
	 * @param filePath location of file to load
	 */
	public void load(String filePath) {
		GameFileUtil.load(filePath, this);
	}

	/**
	 * Provides a string representation of the current state of the LizardGame grid.
	 * This representation includes the dimensions of the grid, the layout of each
	 * cell (including walls, exits, and lizards), and a list of all lizards
	 * currently on the grid.
	 *
	 * @return A string that visually represents the grid and the state of each
	 *         cell, as well as a list of all lizards in the game.
	 */
	@Override
	public String toString() {
		String str = "---------- GRID ----------\n";
		str += "Dimensions:\n";
		str += getWidth() + " " + getHeight() + "\n";
		str += "Layout:\n";
		for (int y = 0; y < getHeight(); y++) {
			if (y > 0) {
				str += "\n";
			}
			for (int x = 0; x < getWidth(); x++) {
				str += getCell(x, y);
			}
		}
		str += "\nLizards:\n";
		for (Lizard l : getLizards()) {
			str += l;
		}
		str += "\n--------------------------\n";
		return str;
	}
}
