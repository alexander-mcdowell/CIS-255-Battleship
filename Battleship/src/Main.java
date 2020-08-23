import java.util.Random;
import java.util.Scanner;

public class Main {
	
	private static final int EASY_BOARD = 35;
	private static final int MEDIUM_BOARD = 50;
	private static final int HARD_BOARD = 65;
	
	private static final int EASY_NUM_MISSES = 15;
	private static final int MEDIUM_NUM_MISSES = 10;
	private static final int HARD_NUM_MISSES = 10;
	
	private static int board[];
	private static int numMisses;
	
	private static int randomInt(Random generator, int lower, int upper) {
		return generator.nextInt(upper - lower + 1) + lower;
	}
	
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
		
		// Find a location for the size three ship.
		int ship1Loc = randomInt(generator, 0, board.length - 4);
		int ship2Loc, ship3Loc;
		
		// Find a location for the size four ship that doesn't intersect with other ships.
		while (true) {
			ship2Loc = randomInt(generator, 0, board.length - 5);
			if (ship2Loc - ship1Loc >= 3) break;
		}
		
		// Find a location for the size five ship that doesn't intersect with other ships.
		int counter;
		while (true) {
			counter = 0;
			ship3Loc = randomInt(generator, 0, board.length - 6);
			if (ship3Loc - ship1Loc >= 3) counter++;
			if (ship3Loc - ship2Loc >= 4) counter++;
			if (counter == 2) break;
		}
		
		// 0 = nothing, 1 = miss, 2 = hit, 3 = 3-ship, 4 = 4-ship, 5 = 5-ship
		int i = 0;
		while (i < board.length) {
			if (i == ship1Loc) {
				board[i] = 3;
				board[i + 1] = 3;
				board[i + 2] = 3;
				i += 3;
			} else if (i == ship2Loc) {
				board[i] = 4;
				board[i + 1] = 4;
				board[i + 2] = 4;
				board[i + 3] = 4;
				i += 4;
			} else if (i == ship3Loc) {
				board[i] = 5;
				board[i + 1] = 5;
				board[i + 2] = 5;
				board[i + 3] = 5;
				board[i + 4] = 5;
				i += 5;
			} else {
				board[i] = 0;
				i++;
			}
		}
		
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
		return 0;
	}
	
	private static void handleMove(int chosenCoordinate) {
		
	}
	
	public static void main(String[] args) {
		System.out.println("Starting a game of Battleship.");
		int difficulty;
		System.out.println("What difficulty would you like?");
		System.out.print("Easy (type 1)\nMedium (type 2)\nHard (type 3)\n");

		Scanner inputScanner = new Scanner(System.in);
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
	}

}
