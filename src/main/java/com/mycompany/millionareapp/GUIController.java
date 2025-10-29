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
 */

/**
 * GUIController — Screen navigation & gameplay orchestration (MVC controller).
 *
 * PURPOSE
 *  - Bridge the view (GameUI) with the domain (GameEngine) and persistence (GameRepository).
 *  - Own session lifecycle (ensure player → start session → finish session).
 *  - Route all UI events to engine/repo; push state changes back to the UI.
 *
 * WHAT TO IMPLEMENT (step-by-step)
 *  1) start():
 *     - Register all GameUI listeners to call the methods below (beginNewGame, submitAnswer, useFiftyFifty, useReveal, showLeaderboard, backToMenu).
 *     - Default screen: ui.showMenu().
 *
 *  2) beginNewGame(playerName):
 *     - Validate playerName (non-empty); show friendly UI message if invalid.
 *     - playerId = repo.ensurePlayer(playerName)
 *     - sessionId = repo.startSession(playerId); startedAt = Instant.now()
 *     - engine.resetWithQuestions(repo.findAllQuestions()); state.resetForNewGame(playerId, sessionId)
 *     - ui.showGame(); refreshQuestionView()
 *
 *  3) submitAnswer(optionIndex):
 *     - boolean correct = engine.answer(optionIndex)
 *     - If game over: compute winnings & elapsedSeconds; repo.finishSession(sessionId, winnings, elapsedSeconds, Instant.now())
 *                    ui.showSummary("You won $X"); optionally offer Leaderboard button
 *       Else: refreshQuestionView()
 *
 *  4) useFiftyFifty() / useReveal():
 *     - Check GameState flags to ensure one-time use (engine enforces); update UI (disable the used button).
 *     - Call engine.useFiftyFifty()/engine.useReveal(); then repo.recordLifelineUse(sessionId, "50/50"/"REVEAL", currentQuestionId)
 *     - refreshQuestionView()
 *
 *  5) showLeaderboard():
 *     - List<Object[]> rows = repo.topSessions(limit); ui.setLeaderboardRows(rows); ui.showLeaderboard()
 *
 *  6) backToMenu():
 *     - ui.showMenu(); (optionally clear transient UI state)
 *
 *  7) refreshQuestionView():
 *     - Read current question, options, tier/prize, and lifeline flags from engine/state
 *     - ui.setQuestionText(...), ui.setOption(i,...), ui.setTierText(...), ui.enableLifeline("50/50", !used), ui.enableLifeline("REVEAL", !used)
 *
 * THREADING & RELIABILITY
 *  - All UI calls on Swing EDT. Keep DB calls short; if slow, offload to a background worker and then update UI on EDT.
 *  - No SQL or business logic here—only calls to repo/engine and UI updates.
 *  - Handle exceptions gracefully (dialog + safe navigation back to menu); never let the UI crash.
 *
 * MARKING NOTES
 *  - Demonstrates clean separation of concerns (MVC), robust input handling, and lifeline constraints.
 *  - Avoid System.out in production paths; prefer UI messages.
 */

public class GUIController {
    private final GameUI ui;
    private GameEngine engine;
    private final GameRepository repo;
    private GameState state;
    private long playerId = -1L;
    private long sessionId = -1L;
    private Instant startedAt;

    public GUIController(GameUI ui, GameEngine engine, GameRepository repo) {
        this.ui = ui;
        this.engine = engine;
        this.repo = repo;
    }

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
    }

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
                    // If finishing fails, still show result; robustness first.
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

    public void useFiftyFifty() {
        if (engine == null || state == null) {
            return;
        }
        int[] hide = engine.useFiftyFiftyLifeLine(state); // <- your engine + strategy do the work
        for (int idx : hide) {
            ui.setOption(idx, "—");      // <- UI-only presentation
        }
        ui.enableLifeline("50/50", false);
    }

    public void useReveal() {
        if (engine == null || state == null) {
            return;
        }
        int correctIdx = engine.revealCorrectAnswer(state); // <- engine + LifeLine decide
        if (correctIdx >= 0) {
            ui.setOption(correctIdx, "[Correct]"); // <- simple UI marker
        }
        ui.enableLifeline("REVEAL", false);
    }

    public void showLeaderboard() {
        // TODO: List<Object[]> rows = repo.topSessions(20); ui.setLeaderboardRows(rows); ui.showLeaderboard();
        try {
            ui.setLeaderboardRows(repo.topSessions(20));
        } catch (UnsupportedOperationException ex) {
            // Before repo.topSessions is implemented, show empty table gracefully
            ui.setLeaderboardRows(java.util.Collections.emptyList());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(ui,
                    "Could not load leaderboard:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ui.setLeaderboardRows(java.util.Collections.emptyList());
        }
        ui.showLeaderboard();
    }

    public void backToMenu() {
        ui.showMenu();
    }

    private void refreshQuestionView() {

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
