/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

import java.time.Instant;
import javax.swing.JOptionPane;

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
    private final GameEngine engine;
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
        } catch(UnsupportedOperationException ex) {
            playerId = -1L;
            sessionId = -1L;
            startedAt = Instant.now();
            
        } catch(Exception ex){
            JOptionPane.showMessageDialog(ui,
                    "Could not start session:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show the game screen
        ui.setTierText("Tier: $0");
        ui.setQuestionText("Q1 will appear here once the engine is connected.");
        ui.setOption(0, "Option A");
        ui.setOption(1, "Option B");
        ui.setOption(2, "Option C");
        ui.setOption(3, "Option D");
        ui.enableLifeline("50/50", true);
        ui.enableLifeline("REVEAL", true);

        ui.showGame();
    }

    public void submitAnswer(int optionIndex) {
        // TODO: call engine.answer(...); if game over -> repo.finishSession(...); ui.showSummary(...); else refreshQuestionView();
        JOptionPane.showMessageDialog(ui,
                "You chose option " + ("ABCD".charAt(optionIndex)) + ".\n" +
                "Answer handling will be enabled after engine wiring.",
                "Answer Selected", JOptionPane.INFORMATION_MESSAGE);
    }

    public void useFiftyFifty() {
        // TODO: engine.useFiftyFifty(); repo.recordLifelineUse(sessionId, "50/50", /*questionId*/0); refreshQuestionView();
        ui.enableLifeline("50/50", false);
        JOptionPane.showMessageDialog(ui,
                "50/50 lifeline will be applied after engine wiring.",
                "Lifeline", JOptionPane.INFORMATION_MESSAGE);
    }

    public void useReveal() {
        // TODO: engine.useReveal(); repo.recordLifelineUse(sessionId, "REVEAL", /*questionId*/0); refreshQuestionView();
        ui.enableLifeline("REVEAL", false);
        JOptionPane.showMessageDialog(ui,
                "Reveal lifeline will show the correct answer after engine wiring.",
                "Lifeline", JOptionPane.INFORMATION_MESSAGE);
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
        // TODO: pull question/tier/flags from engine+state and call ui.setQuestionText/setOption/... etc.
    }
}
