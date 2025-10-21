/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

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
        // TODO: wire UI listeners -> methods below; ui.showMenu();
    }

    public void beginNewGame(String playerName) {
        // TODO: playerId = repo.ensurePlayer(playerName);
        // TODO: sessionId = repo.startSession(playerId); startedAt = Instant.now();
        // TODO: engine.resetWithQuestions(repo.findAllQuestions());
        // TODO: ui.showGame(); refreshQuestionView();
    }

    public void submitAnswer(int optionIndex) {
        // TODO: call engine.answer(...); if game over -> repo.finishSession(...); ui.showSummary(...); else refreshQuestionView();
    }

    public void useFiftyFifty() {
        // TODO: engine.useFiftyFifty(); repo.recordLifelineUse(sessionId, "50/50", /*questionId*/0); refreshQuestionView();
    }

    public void useReveal() {
        // TODO: engine.useReveal(); repo.recordLifelineUse(sessionId, "REVEAL", /*questionId*/0); refreshQuestionView();
    }

    public void showLeaderboard() {
        // TODO: List<Object[]> rows = repo.topSessions(20); ui.setLeaderboardRows(rows); ui.showLeaderboard();
    }

    public void backToMenu() {
        // TODO: ui.showMenu();
    }

    private void refreshQuestionView() {
        // TODO: pull question/tier/flags from engine+state and call ui.setQuestionText/setOption/... etc.
    }
}
