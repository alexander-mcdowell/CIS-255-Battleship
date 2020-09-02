import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
	
	// Global variables
	
	// Board size by difficulty
	private static final int EASY_BOARD = 35;
	private static final int MEDIUM_BOARD = 50;
	private static final int HARD_BOARD = 65;
	
	// Number of allowable misses by difficulty
	private static final int EASY_NUM_MISSES = 15;
	private static final int MEDIUM_NUM_MISSES = 10;
	private static final int HARD_NUM_MISSES = 10;
	
	private static int board[];
	private static int numMisses;
	
	public static void main(String[] args) {
		System.out.println("Starting a game of Battleship.");
		int difficulty;
		System.out.println("What difficulty would you like?");
		System.out.print("Easy (type 1)\nMedium (type 2)\nHard (type 3)\n");

		Scanner inputScanner = new Scanner(System.in);
		// Ensure that the difficulty is in the range we want
		while (true) {
			difficulty = Integer.parseInt(inputScanner.nextLine());
			if ((difficulty >= 0) && (difficulty < 4)) break;
			System.out.println("Try entering an integer between 1 and 3 inclusive.");
		}
		
		setup(difficulty);
		
		System.out.println("The sizes of the ships are: [3, 4, 5]");
		System.out.println("You only have " + numMisses + " misses. Make them count!");
		System.out.println();
		
		int gameState = determineGameState();
		int chosenCoordinate;
		// gameState == 0 means the game is still being played.
		// gameState == 1 means the player has lost.
		// gameState == 2 means the player has won.
		while (gameState == 0) {
			printBoard();
			
			System.out.format("Aim for a coordinate between %d and %d.\n", 1, board.length);
			while (true) {
				chosenCoordinate = Integer.parseInt(inputScanner.nextLine());
				if ((chosenCoordinate >= 1) && (chosenCoordinate <= board.length)) break;
				System.out.format("Try entering an integer between %d and %d inclusive.\n", 1, board.length);
			}
			
			handleMove(chosenCoordinate);
		}
		
		if (gameState == 1) System.out.println("You lost! Better luck next time.");
		else System.out.println("You won! Congratulations!");
		inputScanner.close();
	}

	private static int selectFromArrayList(Random generator, ArrayList<Integer> array) {
		return array.get(generator.nextInt(array.size()));
	}
	
	// Set up the board
	private static void setup(int difficulty) {
		Random generator = new Random();
		
		switch (difficulty) {
			case 1:
				board = new int[EASY_BOARD];
				numMisses = EASY_NUM_MISSES;
				break;
			case 2:
				board = new int[MEDIUM_BOARD];
				numMisses = MEDIUM_NUM_MISSES;
				break;
			case 3:
				board = new int[HARD_BOARD];
				numMisses = HARD_NUM_MISSES;
				break;
		}
		
		// Think of this ArrayList as what is "up for grabs".
		// We start with placing the size 3 board, then remove any spots where, if a size 4 ship was placed, would overlap with the size 3
		// Do the same with the size 4 ship.
		// The spot for the size 5 ship is what is left.
		ArrayList<Integer> locations = new ArrayList<Integer>();
		for (int i = 0; i < board.length - 3; i++) locations.add(i);
		
		// Find locations for the size 3 ship
		int ship1Loc = selectFromArrayList(generator, locations);
		for (int i = -3; i < 3; i++) {
			int x = ship1Loc + i;
			// Cast to Integer is needed to specify the removal of an element with value x, not the element at index x
			if (locations.contains(x)) locations.remove((Integer) x);
		}
		//System.out.println(ship1Loc + ", " + Arrays.toString(locations.toArray()));
		
		// Make sure the size 4 ship doesn't end up going past the end of the board
		int illegalSpot = Math.max(board.length - 4, 0);
		if (locations.contains(illegalSpot)) locations.remove((Integer) illegalSpot);
		// Find locations for the size 4 ship
		int ship2Loc = selectFromArrayList(generator, locations);
		for (int i = -4; i < 4; i++) {
			int x = ship2Loc + i;
			if (locations.contains(x)) locations.remove((Integer) x);
		}
		//System.out.println(ship2Loc + ", " + Arrays.toString(locations.toArray()));
		
		// Remember to remove the spot, if not already done, where a size 5 ship overlaps with the size 3 ship
		illegalSpot = Math.max(ship1Loc - 4, 0);
		if (locations.contains(illegalSpot)) locations.remove((Integer) illegalSpot);
		// Make sure the size 5 ship doesn't end up going past the end of the board
		illegalSpot = Math.max(board.length - 5, 0);
		if (locations.contains(illegalSpot)) locations.remove((Integer) illegalSpot);
		// The location of the size 5 ship is whatever remains
		int ship3Loc = selectFromArrayList(generator, locations);

		// 0 = nothing, 1 = miss, 2 = hit, 3 = 3-ship, 4 = 4-ship, 5 = 5-ship
		int i = 0;
		while (i < board.length) {
			if (i == ship1Loc) {
				for (int j = 0; j < 3; j++) board[i + j] = 3;
				i += 3;
			} else if (i == ship2Loc) {
				for (int j = 0; j < 4; j++) board[i + j] = 4;
				i += 4;
			} else if (i == ship3Loc) {
				for (int j = 0; j < 5; j++) board[i + j] = 5;
				i += 5;
			} else {
				board[i] = 0;
				i++;
			}
		}
		//System.out.println(Arrays.toString(board));
		
		System.out.println("Game has been set up!");
	}
	
	private static void printBoard() {
		// Print coordinates
		for (int i = 1; i <= board.length; i++) System.out.print(i + "\t");
		System.out.println();
		// Print board
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 2) System.out.print("X\t");
			else if (board[i] == 1) System.out.print("M\t");
			else System.out.print("_\t");
		}
		System.out.println();
	}
	
	private static int determineGameState() {
		if (numMisses == 0) return 1;
		
		int activeShips = 0;
		// found is a variable that prevents counting ships more than once.
		boolean found = false;
		for (int i = 0; i < board.length; i++) {
			// If the start of a new ship is found, increment the activeShips counter
			if (board[i] >= 3 && !found) {
				found = true;
				activeShips += 1;
			}
			// If the edge of a ship is found, change found to false
			if (board[i] < 3 && found) found = false;
		}
		
		if (activeShips == 0) return 2;
		else return 0;
	}
	
	private static void handleMove(int chosenCoordinate) {
		
	}

}
