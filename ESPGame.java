/*
 * Class: CMSC203 
 * Instructor: Dr 
 * Description: (Give a brief description for each Class)
 * Due: 09/16/2025
 * Platform/compiler: Java
 * I pledge that I have completed the programming assignment 
* independently. I have not copied the code from a student or   * any source. I have not given my code to any student.
 * Print your Name here: Mathieu Nandjo
*/


package progProject1;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

public class ESPGame {
    public static void main(String[] args) {

        // Filenames (default file for colors; results file to write)
        final String DEFAULT_COLORS_FILE = "colors.txt";
        final String RESULTS_FILE = "EspGameResults.txt";
        // Menu constants (numeric choices)
        final int MENU_SHOW_16 = 1;
        final int MENU_SHOW_10 = 2;
        final int MENU_SHOW_5  = 3;
        final int MENU_EXIT    = 4;
        // Game parameters
        final int MAX_COLORS_FOR_RANDOM = 16; // computer picks from first 16 lines
        final int ROUNDS = 3;                 // exactly 3 rounds
        //  messages and labels
        final String TITLE_MENU = "====== ESP Game Menu ======";
        final String MSG_ENTER_CHOICE = "Enter your choice (1-4): ";
        final String MSG_AVAILABLE = "Available colors to guess from:";
        final String MSG_GAME_OVER = "===== Game Over =====";
        final String PROMPT_GUESS  = "Type your guess (enter the color name as text): ";

        final String PROMPT_NAME   = "Enter your name: ";
        final String PROMPT_ABOUT  = "Enter a sentence that describes yourself: ";
        final String PROMPT_DUE    = "Enter the due date (MM/DD/YY): ";

        //  validation patterns
        final String DATE_PATTERN = "^(0[1-9]|1[0-2])/([0-2][0-9]|3[01])/\\d{2}$"; // MM/DD/YY
        final String NON_EMPTY_PATTERN = ".*\\S.*"; // contains at least one non-whitespace char

        // In comments and labeling outputs
        final String INFO_COLORS_SOURCE = "Colors Source File: ";
        final String INFO_MENU_SHOWN = "Menu Choice Shown: ";
        final String INFO_CORRECT = "Correct Guesses: ";

        /* =======================
           ======= COMMAND-LINE  =======
           ================
        */
        String colorsFilePath = DEFAULT_COLORS_FILE;
        if (args != null && args.length >= 1 && args[0] != null && args[0].trim().length() > 0) {
            colorsFilePath = args[0].trim();
        }

        /* ===================
           ======= SCANNERS AND RANDOMIZER =======
           =================== */
        Scanner keyboard = new Scanner(System.in);
        Random random = new Random();

        /* =================================
           ======= MENU (with switch) =======
           =================================
        */
        int choice = 0;

        // Input validation loop for the menu selection (must be 1..4)
        while (true) {
            System.out.println(TITLE_MENU);
            System.out.println("1. Show first 16 colors");
            System.out.println("2. Show first 10 colors");
            System.out.println("3. Show first 5 colors");
            System.out.println("4. Exit");
            System.out.print(MSG_ENTER_CHOICE);

            if (keyboard.hasNextInt()) 
            {
                choice = keyboard.nextInt();
                keyboard.nextLine(); // consume leftover newline
                if (choice >= 1 && choice <= 4) 
                {
                    break; // valid
                } else {
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.\n");
                }
            } else {
                // Not an integer → clear invalid token and reprompt
                keyboard.nextLine();
                System.out.println("Invalid input. Please enter a numeric choice (1-4).\n");
            }
        }

        // Decide how many colors to display (showCount) based on menu choice
        int showCount = 0;
        switch (choice) {
            case MENU_SHOW_16:
                showCount = 16; // we *intend* to show up to 16 (file may have fewer)
                break;
            case MENU_SHOW_10:
                showCount = 10;
                break;
            case MENU_SHOW_5:
                showCount = 5;
                break;
            case MENU_EXIT:
                System.out.println("Exiting the program...");
                keyboard.close();
                return;
            default:
                // Should never happen due to validation
                System.out.println("Unexpected menu selection. Exiting now.");
                keyboard.close();
                return;
        }

        /* ============================================================
           ======= DISPLAY FIRST N COLORS FROM THE FILE (NO ARRAYS) =======
           ============================================================
        */
        System.out.println();
        System.out.println(MSG_AVAILABLE);

        int actuallyPrinted = 0;
        Scanner fileScannerForList = null;
        try {
            fileScannerForList = new Scanner(new File(colorsFilePath));

            while (fileScannerForList.hasNextLine() && actuallyPrinted < showCount) {
                String line = fileScannerForList.nextLine().trim();
                if (line.length() > 0) { // skip blank lines
                    System.out.println((actuallyPrinted + 1) + ". " + line);
                    actuallyPrinted++;
                }
            }

            if (actuallyPrinted == 0) {
                System.out.println("No colors found in " + colorsFilePath + ". Exiting.");
                if (fileScannerForList != null) fileScannerForList.close();
                keyboard.close();
                return;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + colorsFilePath + " not found. Exiting.");
            keyboard.close();
            return;
        } finally {
            if (fileScannerForList != null) fileScannerForList.close();
        }

        System.out.println();

        /* ======================================================
           ======= PLAY EXACTLY 3 ROUNDS (WHILE + FOR + DO) =======
           ======================================================

           - Computer picks a random "target line number" in [1..16].
           - We reopen the file, advance line-by-line until that line, and read that color.
           - We ask the user to type a guess (validated to be non-empty).
           - Reveal the computer's color and count correct guesses (case-insensitive).
         */
        int correct = 0;

        // We show 'for' loop usage here for rounds:
        for (int round = 1; round <= ROUNDS; round++) {

            // Pick a random target line number from 1..16 (as required)
            int targetIndex = random.nextInt(MAX_COLORS_FOR_RANDOM) + 1;

            // Read that specific line (no arrays): we scan down the file until we reach targetIndex.
            String computerColor = "";
            Scanner fileScannerForPick = null;
            int nonEmptyCount = 0;

            try {
                fileScannerForPick = new Scanner(new File(colorsFilePath));

                // We use a while loop to iterate through lines
                while (fileScannerForPick.hasNextLine()) {
                    String line = fileScannerForPick.nextLine().trim();
                    if (line.length() > 0) {
                        nonEmptyCount++;
                        if (nonEmptyCount == targetIndex) {
                            computerColor = line;
                            break; // found the target color
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error: " + colorsFilePath + " not found while picking a color. Exiting.");
                if (fileScannerForPick != null) fileScannerForPick.close();
                keyboard.close();
                return;
            } finally {
                if (fileScannerForPick != null) fileScannerForPick.close();
            }

            // Validate that we actually found a color at that index.
            if (computerColor.length() == 0) {
                System.out.println(
                    "The file does not have at least " + MAX_COLORS_FOR_RANDOM +
                    " non-empty color lines. Please add more colors. Exiting."
                );
                keyboard.close();
                return;
            }

            // Ask for the user's guess, demonstrating a do-while validation loop (non-empty input)
            String userGuess = "";
            do {
                System.out.print("Round " + round + "/" + ROUNDS + " - " + PROMPT_GUESS);
                userGuess = keyboard.nextLine().trim();
                if (!userGuess.matches(NON_EMPTY_PATTERN)) {
                    System.out.println("Input cannot be empty. Please type a color name.\n");
                }
            } while (!userGuess.matches(NON_EMPTY_PATTERN));

            // Reveal computer's selection and tally
            System.out.println("Computer selected: " + computerColor);
            if (userGuess.equalsIgnoreCase(computerColor)) {
                System.out.println("You guessed correctly!");
                correct++;
            } else {
                System.out.println("That was not a match.");
            }
            System.out.println();
        }

        // After 3 rounds, display the tally
        System.out.println("You guessed correctly " + correct + " out of " + ROUNDS + " times.\n");

        /* ============================================
           ======= STUDENT INFO (with validation) =======
           ============================================ */

        // Name (non-empty)
        String studentName = "";
        do {
            System.out.print(PROMPT_NAME);
            studentName = keyboard.nextLine().trim();
            if (!studentName.matches(NON_EMPTY_PATTERN)) {
                System.out.println("Name cannot be empty. Please try again.\n");
            }
        } while (!studentName.matches(NON_EMPTY_PATTERN));

        // Self-description (non-empty)
        String selfSentence = "";
        do {
            System.out.print(PROMPT_ABOUT);
            selfSentence = keyboard.nextLine().trim();
            if (!selfSentence.matches(NON_EMPTY_PATTERN)) {
                System.out.println("Description cannot be empty. Please try again.\n");
            }
        } while (!selfSentence.matches(NON_EMPTY_PATTERN));

        // Due date (must match MM/DD/YY)
        String dueDate = "";
        do {
            System.out.print(PROMPT_DUE);
            dueDate = keyboard.nextLine().trim();
            if (!dueDate.matches(DATE_PATTERN)) {
                System.out.println("Invalid format. Please use MM/DD/YY (e.g., 09/30/25).\n");
            }
        } while (!dueDate.matches(DATE_PATTERN));

        /* ====================================
           ======= FINAL OUTPUT + FILE I/O =======
           ==================================== */
        System.out.println();
        System.out.println(MSG_GAME_OVER);
        System.out.println("Name: " + studentName);
        System.out.println("About: " + selfSentence);
        System.out.println("Due Date: " + dueDate);
        System.out.println(INFO_CORRECT + correct + " out of " + ROUNDS);
        System.out.println(INFO_MENU_SHOWN + actuallyPrinted + " colors");
        System.out.println(INFO_COLORS_SOURCE + colorsFilePath);

        // Write results to file with PrintWriter
        PrintWriter out = null;
        try {
            out = new PrintWriter(RESULTS_FILE);
            out.println(MSG_GAME_OVER);
            out.println("Name: " + studentName);
            out.println("About: " + selfSentence);
            out.println("Due Date: " + dueDate);
            out.println(INFO_CORRECT + correct + " out of " + ROUNDS);
            out.println(INFO_MENU_SHOWN + actuallyPrinted + " colors");
            out.println(INFO_COLORS_SOURCE + colorsFilePath);
            out.flush();
            System.out.println("\nResults saved to " + RESULTS_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("Error: could not create/write " + RESULTS_FILE);
        } finally {
            if (out != null) out.close();
        }

        // Clean up keyboard scanner
        keyboard.close();
    }
}