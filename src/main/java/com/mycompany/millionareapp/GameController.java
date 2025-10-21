/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

/**
 *
 * @author rupertguppy
 * 
 * ChatGPT helped with a majority of this class
 * 
 * High-level flow manager between UI and Engine.
 *
 * What this class does:
 * - Displays the main menu loop:
 *     1) New Game
 *     2) View High Scores
 *     3) Quit
 * - For New Game:
 *     - Ask player name via UI.
 *     - Ask confirmation if needed (optional).
 *     - Call engine.startNewGame(playerName) → returns a fresh GameState.
 *     - Enter game play loop (see below).
 * - For High Scores:
 *     - Ask persistence for lines of high scores and print them.
 * - Game play loop:
 *     - Render current question (via engine previous get methods) and options.
 *     - Allow actions:
 *          [1-4] answer option
 *          [5] use 50/50 lifeline (if available)
 *          [6] use Reveal Answer lifeline (if available)
 *          [7] save and exit to main menu
 *     - Delegate to engine for result of action (no game rules here).
 *     - Loop until engine says game is won or over.
 *     - On end: show final winnings; ask persistence to record high score.
 */


/**
 * LEGACY CLI CONTROLLER.
 * What to change:
 *  - Mark as legacy/fallback; not used by default in Assignment 2.
 *  - Optionally delegate any reusable logic to GameEngine (pure domain logic).
 *  - No DB or GUI code should be added here.
 */

public class GameController {
    
    private final GameEngine engine;
    private final GamePersistance persistance;
    private final Scanner input = new Scanner(System.in);

    public GameController(GameEngine engine, GamePersistance persistance){
        this.engine = engine;
        this.persistance = persistance;
    }
    // this method is for when a new game starts, includes the main menu UI
    public void run(){
        while(true){
            System.out.println("\n=== Main Menu ===");
            System.out.println("1) New Game");
            System.out.println("2) View High Scores");
            System.out.println("3) Quit");
            System.out.print("Choose here: ");

            String s = input.nextLine().trim();
            if(s.equalsIgnoreCase("q") || s.equals("3")){
                System.out.println("=== Goodbye ===");
                return;
            }

            switch(s) {
                case "1" -> startNewGame();
                case "2" -> showHighScores();
                default -> System.out.println("Invalid option, please enter 1, 2 or 3 ['q' to quit]");
            }
        }
    }
    // thios method is for starting a new game
    public void startNewGame(){
        System.out.print("Enter a player name ['q' to quit]: ");
        String playerName = input.nextLine().trim();

        if(playerName.equalsIgnoreCase("q") || playerName.isBlank()){
            System.out.println("=== Cancelled! ===");
            return;
        }

        GameState status = engine.startGame(playerName);

        if(engine.getCurrentQuestion(status) == null){
            System.out.println("No questions available. Please add some to questions.txt");
            return;
        }

        playLoop(status);
    }
    // this method shows all the previous highscores 
    public void showHighScores(){
        System.out.println("\n=== Current High Scores ===");
        List<String> lines = persistance.loadHighScores();

        if(lines.isEmpty()){
            System.out.println("[no scores yet]");
            return;
        }
        for(String line : lines){
            if(line != null && !line.isBlank() && line.contains("|")){
                System.out.println(line);
            }
        }
    }

    public void playLoop(GameState status){
        int lastQ = -1;                    // track question number to reset hidden between questions
        boolean[] hidden = new boolean[4]; // indices 0..3 hidden by 50/50

        while(!engine.gameIsOver(status) && !engine.isWon(status)) {

            // Reset hidden when we move to a new question
            if (status.getQuestionNumber() != lastQ) {
                hidden = new boolean[4];
                lastQ = status.getQuestionNumber();
            }
            Question q = engine.getCurrentQuestion(status);
            if(q == null){
                System.out.println("[No more questions. Ending game...]");
                status.gameOver();
                break;
            }

            // Build visible list (renumbered for display)
            List<Integer> visible = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) if (!hidden[i]) visible.add(i);

            System.out.println("\n=== Question " + status.getQuestionNumber() + " ===");
            System.out.println(q.getQuestion());
            for (int i = 0; i < visible.size(); i++) {
                int realIdx = visible.get(i); // 0..3
                System.out.println(" " + (i + 1) + ") " + q.getOption(realIdx).getText());
            }

            System.out.println("\nActions: [1-" + visible.size() + "]=Answer  [5]=50/50  [6]=LifeLine  (q=Quit to menu)");
            System.out.print("Choose: ");
            String s = input.nextLine().trim();

            // Walk away ststus
            if(s.equalsIgnoreCase("q")){
                int payout = status.getCurrentPrize();
                persistance.appendHighScore(status.getName(), payout);
                System.out.println("You walked away with $" + payout + ". Returning to menu...");
                return;
            }

            // 50/50 ststus
            if (s.equals("5")) {
                if (status.hasUsed5050()) {
                    System.out.println("50/50 already used.");
                } else {
                    int[] hide = engine.useFiftyFiftyLifeLine(status); // must be exactly two wrong indices
                    if (hide.length == 2) {
                        hidden[hide[0]] = true;
                        hidden[hide[1]] = true;
                        // On next loop the question will render with only two options
                    } else {
                        System.out.println("50/50 not available.");
                    }
                }
                continue;
            }

            // Reveal status
            if (s.equals("6")) {
                if (status.hasUsedLifeline()) {
                    System.out.println("Reveal already used.");
                } else {
                    int idx = engine.revealCorrectAnswer(status);
                    if (idx >= 0) {
                        System.out.println("\"I think the correct option is "
                                + q.getOption(idx).getText() + "\" -Lifeline");
                    } else {
                        System.out.println("Reveal not available.");
                    }
                }
                continue;
            }

            // Answer selection status
            try {
                int choice = Integer.parseInt(s);
                if (choice < 1 || choice > visible.size()) {
                    System.out.println("Please enter 1-" + visible.size() + ", 5, 6, or q.");
                    continue;
                }
                int realIdx = visible.get(choice - 1); // map display choice back to actual 0..3
                engine.answer(status, realIdx);

                if (!engine.gameIsOver(status) && !engine.isWon(status)) {
                    System.out.println("Current prize: $" + status.getCurrentPrize()
                            + "   Guaranteed: $" + status.getGuaranteedPrize());
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Enter 1-" + visible.size() + ", 5, 6, or q.");
            }
        }
        // End of game status
        if (engine.isWon(status)) {
            System.out.println("\nCongratulations!!! You WON! Final prize: $" + status.getCurrentPrize());
            persistance.appendHighScore(status.getName(), status.getCurrentPrize());
        } else {
            System.out.println("\nGame over. Guaranteed payout: $" + status.getGuaranteedPrize());
            persistance.appendHighScore(status.getName(), status.getGuaranteedPrize());
        }
        System.out.println("Returning to main menu...");
    }

}
