package hw3;

import api.BodySegment;
import api.Cell;
import api.Exit;
import api.Wall;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Utility class with static methods for loading game files.
 * 
 * @author Muhammad Blal
 */
public class GameFileUtil {
	/**
	 * Loads the file at the given file path into the given game object. When the
	 * method returns the game object has been modified to represent the loaded
	 * game.
	 *
	 * @param filePath the path of the file to load
	 * @param game     the game to modify
	 */
    public static void load(String filePath, LizardGame game) {
        File file = new File(filePath); // Create a File object from the given file path.
        Scanner scnr;
        try {
            scnr = new Scanner(file); // Try to open the file for reading.
        } catch (FileNotFoundException e) {
            return; // If the file is not found, exit the method without making changes.
        }

        // Read the grid dimensions from the file.
        String ds = scnr.nextLine(); // Read the first line containing dimensions.
        String[] dimensions = ds.split("x"); // Split the dimension string at 'x' to get width and height.
        int width = Integer.parseInt(dimensions[0]); // Parse width from the dimensions array.
        int height = Integer.parseInt(dimensions[1]); // Parse height from the dimensions array.
        game.resetGrid(width, height); // Reset the game grid with the new dimensions.

        // Parse the grid representation (walls and exits) from the file.
        for (int row = 0; row < height; row++) {
            String line = scnr.nextLine(); // Read each line representing a row of the grid.
            for (int col = 0; col < width; col++) {
                char c = line.charAt(col); // Get the character at each column in the row.
                Cell cell = game.getCell(col, row); // Get the corresponding Cell object from the game.

                if (c == 'W') {
                    Wall wall = new Wall(cell); // Create a Wall object if the character is 'W'.
                    game.addWall(wall); // Add the wall to the game.
                } else if (c == 'E') {
                    Exit exit = new Exit(cell); // Create an Exit object if the character is 'E'.
                    game.addExit(exit); // Add the exit to the game.
                }
            }
        }

        // Parse the lizard segment positions from the file.
        while (scnr.hasNextLine()) {
            String line = scnr.nextLine(); // Read each line representing a lizard.
            Scanner linescnr = new Scanner(line); // Open a new scanner for parsing the line.
            String first = linescnr.next(); // Read the first word to identify lizard lines.

            if (first.equals("L")) { // Check if the line represents a lizard.
                Lizard lizard = new Lizard(); // Create a new Lizard object.
                ArrayList<BodySegment> segments = new ArrayList<>(); // Create a list for the lizard's segments.

                while (linescnr.hasNext()) { // Iterate through the segment positions.
                    String loc = linescnr.next(); // Read the segment position.
                    String[] coordinates = loc.split(","); // Split the position into column and row.
                    int col = Integer.parseInt(coordinates[0]); // Parse the column value.
                    int row = Integer.parseInt(coordinates[1]); // Parse the row value.
                    segments.add(new BodySegment(lizard, game.getCell(col, row))); // Add the segment to the list.
                }

                lizard.setSegments(segments); // Set the segments to the lizard.
                game.addLizard(lizard); // Add the lizard to the game.
            }
            linescnr.close(); // Close the line scanner.
        }
    }
}
