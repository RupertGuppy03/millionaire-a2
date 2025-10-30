/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

import java.time.Instant;
import java.util.List;
import javax.swing.JOptionPane;
import java.time.Duration;


// these imports have errors

/*import com.mycompany.millionareapp.ui.GameUI;
import com.mycompany.millionareapp.engine.GameEngine;
import com.mycompany.millionareapp.model.GameState;
import com.mycompany.millionareapp.persistence.GameRepository;

import java.time.Instant;
import java.util.List;
*/

/**
 *
 * @author rupertguppy
 * 
 * chat GPT helped with 40% of this class
 */

/**
 * What this class does:
 *  - Acts as the MVC controller for the Swing app.
 *  - Connects GameUI (view) to GameEngine (rules) and GameRepository (Derby DB).
 *  - Starts a game session (ensure player, startSession), advances questions,
 *    checks answers, and finishes the session (finishSession).
 *  - Applies lifelines (50/50, Reveal) and updates the UI accordingly.
 *  - Loads and shows the leaderboard, and handles simple navigation (menu/game).
 *  - Handles errors with friendly dialogs; no layout or SQL logic lives here.
 *  - Intended to be called on the Swing EDT.
 */

public class GUIController {
    // parameters
    private final GameUI ui;
    private GameEngine engine;
    private final GameRepository repo;
    private GameState state;
    private long playerId = -1L;
    private long sessionId = -1L;
    private Instant startedAt;
    
    // constructor for the GUI contoller
    public GUIController(GameUI ui, GameEngine engine, GameRepository repo) {
        this.ui = ui;
        this.engine = engine;
        this.repo = repo;
    }
    
    // start method
    public void start() {
        // Menu listeners
        ui.onStart(e -> beginNewGame(ui.getPlayerName()));
        ui.onLeaderboard(e -> showLeaderboard());
        ui.onQuit(e -> System.exit(0));
        // Game listeners
        ui.onAnswer(0, e -> submitAnswer(0));
        ui.onAnswer(1, e -> submitAnswer(1));
        ui.onAnswer(2, e -> submitAnswer(2));
        ui.onAnswer(3, e -> submitAnswer(3));
        ui.onFifty(e -> useFiftyFifty());
        ui.onReveal(e -> useReveal());
        ui.onBack(e -> backToMenu());
        // Default screen
        ui.showMenu();
        // leaderboard back button
        ui.onLeaderboardBack(e -> backToMenu());

    }
    // this method controls how a new game starts once you enter your name
    public void beginNewGame(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            JOptionPane.showMessageDialog(ui, "Please enter a player name.", "Invalid name",
            JOptionPane.WARNING_MESSAGE);

            return;           
        }
        
        try{
            playerId = repo.ensurePlayer(playerName);
            sessionId = repo.startSession(playerId);
            startedAt = Instant.now();  
            
            List<Question> questions = repo.findAllQuestions();
                        
            if(questions.isEmpty()){
                ui.setQuestionText("No questions in database");
                return;
            } 
            
            QuestionBank bank = new QuestionBank();
            for (Question q : questions) bank.add(q);
            
            this.engine = new GameEngine(bank);
            this.state = this.engine.startGame(playerName);
            
            refreshQuestionView();           
            ui.showGame();
                
        } catch(Exception ex){
            JOptionPane.showMessageDialog(ui,
                    "Could not start session:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
    }
    
    // this method controls how answers are checked
    public void submitAnswer(int optionIndex) {
        if (engine == null || state == null) return;

        try {
            engine.answer(state, optionIndex);

            if (engine.gameIsOver(state)) {
                int  winnings = engine.currentPrizeGet(state);
                long elapsed  = Duration.between(startedAt, Instant.now()).getSeconds();
                try {
                    repo.finishSession(sessionId, winnings, elapsed, Instant.now());
                } catch (Exception ignore) {
                    // If finishing fails, still show result
                }
                ui.showSummary("Game over! You won $" + winnings);
                return;
            }

            // Continue to next question
            refreshQuestionView();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ui,
                    "Could not process the answer:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // this method controls how the UI gets updated for the 50/50
    public void useFiftyFifty() {
        if (engine == null || state == null) {
            return;
        }
        int[] hide = engine.useFiftyFiftyLifeLine(state); 
            for (int idx : hide) ui.hideOption(idx);

        ui.enableLifeline("50/50", false);
    }
    // this method controls how the UI gets updated for the lifeline
    public void useReveal() {
        if (engine == null || state == null) {
            return;
        }

        int correctIdx = engine.revealCorrectAnswer(state);
        if (correctIdx >= 0) {
        // Hide every incorrect option
        for (int i = 0; i < 4; i++) {
            if (i != correctIdx) ui.hideOption(i);
        }
        // Label the correct option and keep it enabled
        ui.setOption(correctIdx, "[Correct]");
        ui.enableOption(correctIdx, true);
        ui.enableLifeline("REVEAL", false);
    }
    }
    // this method controls shows the leaderboard UI with safe fallbacks if the repo isnt active 
    public void showLeaderboard() {
        try {
            ui.setLeaderboardRows(repo.topSessions(20));
        } catch (UnsupportedOperationException ex) {
            ui.setLeaderboardRows(java.util.Collections.emptyList());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ui,
                    "Could not load leaderboard:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ui.setLeaderboardRows(java.util.Collections.emptyList());
        }
        ui.showLeaderboard();
    }
    // this method brings you back to the menu UI
    public void backToMenu() {
        ui.showMenu();
    }
    // this method refreshed the questions each time you use a lifeline or get the answer correct
    private void refreshQuestionView() {
        
        ui.resetOptionsEnabled();


        Question q = engine.getCurrentQuestion(state);
        if (q == null) {
            // Defensive: if something went off, end gracefully.
            ui.showSummary("No more questions.");
            return;
        }
        ui.setQuestionText(q.getQuestion());
        ui.setOption(0, q.getOption(0).toString());
        ui.setOption(1, q.getOption(1).toString());
        ui.setOption(2, q.getOption(2).toString());
        ui.setOption(3, q.getOption(3).toString());

        int prize = engine.currentPrizeGet(state);
        ui.setTierText("Tier: $" + prize);

        // Lifeline availability from GameState flags (engine enforces once-only)
        boolean used5050 = state.hasUsed5050();
        boolean usedRev = state.hasUsedLifeline();
        ui.enableLifeline("50/50", !used5050);
        ui.enableLifeline("REVEAL", !usedRev);

    }
}
