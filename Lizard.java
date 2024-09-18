package hw3;

import api.BodySegment;
import api.Cell;
import api.Direction;
import java.util.ArrayList;

/**
 * Represents a Lizard as a collection of body segments.
 * 
 * @author Muhammad Blal
 */
public class Lizard {
	private ArrayList<BodySegment> segments; // Holds the segments of the lizard, ordered from tail to head.

	/**
	 * Constructs a Lizard object.
	 */
	public Lizard() {
		segments = new ArrayList<>();
	}

	/**
	 * Sets the segments of the lizard. Segments should be ordered from tail to
	 * head.
	 * 
	 * @param segments list of segments ordered from tail to head
	 */
	public void setSegments(ArrayList<BodySegment> segments) {
		this.segments = new ArrayList<>(segments); // Set the segments with a new list to avoid external modifications.
	}

	/**
	 * Gets the segments of the lizard. Segments are ordered from tail to head.
	 * 
	 * @return a list of segments ordered from tail to head
	 */
	public ArrayList<BodySegment> getSegments() {
		return new ArrayList<>(segments); // Return a copy to prevent external modifications
	}

	/**
	 * Gets the head segment of the lizard. Returns null if the segments have not
	 * been initialized or there are no segments.
	 * 
	 * @return the head segment
	 */
	public BodySegment getHeadSegment() {
		return segments.get(segments.size() - 1); // The head is the last segment in the list.
	}

	/**
	 * Gets the tail segment of the lizard. Returns null if the segments have not
	 * been initialized or there are no segments.
	 * 
	 * @return the tail segment
	 */
	public BodySegment getTailSegment() {
		return segments.get(0); // The tail is the first segment in the list.
	}

	/**
	 * Gets the segment that is located at a given cell or null if there is no
	 * segment at that cell.
	 * 
	 * @param cell to look for lizard
	 * @return the segment that is on the cell or null if there is none
	 */
	public BodySegment getSegmentAt(Cell cell) {
		for (BodySegment seg : segments) {
			if (seg.getCell().equals(cell)) {
				return seg;
			}
		}
		return null;
	}

	/**
	 * Get the segment that is in front of (closer to the head segment than) the
	 * given segment. Returns null if there is no segment ahead.
	 * 
	 * @param segment the starting segment
	 * @return the segment in front of the given segment or null
	 */
	public BodySegment getSegmentAhead(BodySegment segment) {
		int index = segments.indexOf(segment);
		if (index + 1 >= segments.size()) {
			return null;
		}
		return segments.get(index + 1);
	}

	/**
	 * Get the segment that is behind (closer to the tail segment than) the given
	 * segment. Returns null if there is not segment behind.
	 * 
	 * @param segment the starting segment
	 * @return the segment behind of the given segment or null
	 */
	public BodySegment getSegmentBehind(BodySegment segment) {
		int index = segments.indexOf(segment);
		if (index - 1 < 0) {
			return null;
		}
		return segments.get(index - 1);
	}

	/**
	 * Gets the direction from the perspective of the given segment point to the
	 * segment ahead (in front of) of it. Returns null if there is no segment ahead
	 * of the given segment.
	 * 
	 * @param segment the starting segment
	 * @return the direction to the segment ahead of the given segment or null
	 */
	public Direction getDirectionToSegmentAhead(BodySegment segment) {
	    // Retrieve the segment that is directly ahead (closer to the head) of the given segment
	    BodySegment aheadSegment = getSegmentAhead(segment);

	    // Check if there is no segment ahead of the given segment
	    if (aheadSegment == null) {
	        return null; // Return null since there is no segment ahead to determine a direction
	    }

	    // Calculate and return the direction from the given segment to the segment ahead
	    // This is done by comparing the cells of the current segment and the segment ahead
	    return getDirectionBetweenSegments(segment.getCell(), aheadSegment.getCell());
	}
		

	/**
	 * Gets the direction from the perspective of the given segment point to the
	 * segment behind it. Returns null if there is no segment behind of the given
	 * segment.
	 * 
	 * @param segment the starting segment
	 * @return the direction to the segment behind of the given segment or null
	 */
	public Direction getDirectionToSegmentBehind(BodySegment segment) {
		// Get the segment that is directly behind the given segment in the lizard's
		// body.
		BodySegment behindSegment = getSegmentBehind(segment);

		// If there is no segment behind the given one (e.g., if it's the tail), return
		// null.
		if (behindSegment == null) {
			return null;
		}

		// Calculate the direction from the behind segment to the current segment.
		// This is achieved by comparing their positions.
		return getDirectionBetweenSegments(segment.getCell(), behindSegment.getCell());
	}

	/**
	 * Gets the direction in which the head segment is pointing. This is the
	 * direction formed by going from the segment behind the head segment to the
	 * head segment. A lizard that does not have more than one segment has no
	 * defined head direction and returns null.
	 * 
	 * @return the direction in which the head segment is pointing or null
	 */
	public Direction getHeadDirection() {
		// Check if the lizard has less than two segments (i.e., lacks a distinct head and neck)
		if (segments.size() < 2) {
			return null;
		}
	    // Get the segment just before the head, which we can consider as the 'neck'
		BodySegment neck = segments.get(segments.size() - 2);
		
	    // Get the head segment of the lizard
		BodySegment head = getHeadSegment();
		
	    // Calculate and return the direction from the neck segment to the head segment
	    // This represents the direction the head is pointing
		return getDirectionBetweenSegments(neck.getCell(), head.getCell());
	}

	/**
	 * Gets the direction in which the tail segment is pointing. This is the
	 * direction formed by going from the segment ahead of the tail segment to the
	 * tail segment. A lizard that does not have more than one segment has no
	 * defined tail direction and returns null.
	 * 
	 * @return the direction in which the tail segment is pointing or null
	 */
	public Direction getTailDirection() {
	    // Check if the lizard has less than two segments.
	    // With only one segment, it's not possible to define a tail direction.
	    if (segments.size() < 2) {
	        return null;
	    }

	    // Get the cell of the second segment in the list.
	    // This is considered the segment immediately in front of the tail.
	    Cell secondSegmentCell = segments.get(1).getCell();

	    // Retrieve the tail segment of the lizard.
	    BodySegment tail = getTailSegment();

	    // Get the cell where the tail segment is located.
	    Cell tailCell = tail.getCell();

	    // Calculate and return the direction from the second segment (in front of the tail) to the tail segment.
	    // This gives us the direction in which the tail is pointing.
	    return getDirectionBetweenSegments(secondSegmentCell, tailCell);
	}
		

	/**
	 * Private helper method to determine the direction between two adjacent
	 * segments of the lizard. This method calculates the direction based on the
	 * relative positions of two cells, representing segments of the lizard's body.
	 * 
	 * @param start The cell representing the starting segment (can be tail, head,
	 *              or any other segment).
	 * @param end   The cell representing the ending segment (adjacent to the
	 *              starting segment).
	 * @return The direction from the start cell to the end cell. Possible values
	 *         are UP, DOWN, LEFT, RIGHT. Returns null if the segments are not
	 *         adjacent or are diagonal to each other.
	 */
	private Direction getDirectionBetweenSegments(Cell start, Cell end) {
		// Determine the column difference between the end and start segments.
		int colDiff = end.getCol() - start.getCol();

		// Determine the row difference between the end and start segments.
		int rowDiff = end.getRow() - start.getRow();

		// Check the differences to determine the direction:
		if (colDiff > 0) {
			return Direction.RIGHT; // The end segment is to the right of the start segment.
		} else if (colDiff < 0) {
			return Direction.LEFT; // The end segment is to the left of the start segment.
		} else if (rowDiff > 0) {
			return Direction.DOWN; // The end segment is below the start segment.
		} else if (rowDiff < 0) {
			return Direction.UP; // The end segment is above the start segment.
		}

		// If the segments are not adjacent or are diagonal to each other, return null.
		return null;
	}

	/**
	 * Provides a string representation of the lizard. This representation includes
	 * the positions of all the body segments of the lizard, ordered from tail to
	 * head. Each segment's position is represented in a human-readable format. This
	 * method is useful for debugging and for displaying the lizard's current state
	 * in a text-based interface.
	 * 
	 * @return A string that lists all the segments of the lizard, with each
	 *         segment's position represented in the format "(column,row)". The
	 *         segments are listed in order, starting from the tail and ending with
	 *         the head.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (BodySegment seg : getSegments()) {
			result.append(seg).append(" ");
		}
		return result.toString().trim();
	}
}
