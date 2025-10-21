/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
/**
 *
 * @author rupertguppy
 */

/**
 * GameUI — Single-window Swing UI (CardLayout; inner screens).
 *
 * PURPOSE
 *  - Provide the entire GUI in one top-level JFrame.
 *  - Manage three screens via CardLayout: MENU, GAME, LEADERBOARD.
 *  - Contain private inner panels (MenuCard, GameCard, LeaderboardCard) to keep file count low.
 *  - Hold no business logic; all actions are delegated to GUIController.
 *
 * PUBLIC API (methods to implement later; called by GUIController)
 *  - Navigation:
 *      void showMenu();
 *      void showGame();
 *      void showLeaderboard();
 *  - Menu screen:
 *      String getPlayerName();
 *      void onStart(java.awt.event.ActionListener l);
 *      void onLeaderboard(java.awt.event.ActionListener l);
 *      void onHowTo(java.awt.event.ActionListener l);
 *      void onQuit(java.awt.event.ActionListener l);
 *  - Game screen:
 *      void setTierText(String text);
 *      void setQuestionText(String text);
 *      void setOption(int idx, String text);        // idx ∈ {0,1,2,3}
 *      void enableLifeline(String name, boolean on); // "50/50" or "REVEAL"
 *      void showSummary(String text);               // displayed on game over
 *      void onAnswer(int idx, java.awt.event.ActionListener l); // per-button hook
 *      void onFifty(java.awt.event.ActionListener l);
 *      void onReveal(java.awt.event.ActionListener l);
 *      void onBack(java.awt.event.ActionListener l); // return to menu
 *  - Leaderboard screen:
 *      void setLeaderboardRows(java.util.List<Object[]> rows); // [player, winnings, finished_at]
 *      void onLeaderboardBack(java.awt.event.ActionListener l);
 *
 * SCREEN STRUCTURE (to build later)
 *  - Root: CardLayout (keys: "menu", "game", "leaderboard").
 *  - MenuCard: JTextField (player name), buttons: Start, Leaderboard, How to Play, Quit.
 *  - GameCard: JLabel (tier), JTextArea (question), 4 answer JButtons, 2 lifeline JButtons (50/50, Reveal),
 *              JButton Back, JLabel summary (hidden until game over).
 *  - LeaderboardCard: JTable (3 columns: Player, Winnings, Finished At) + Back button.
 *
 * CONSTANTS (to define later)
 *  - public static final String SCREEN_MENU = "menu";
 *  - public static final String SCREEN_GAME = "game";
 *  - public static final String SCREEN_LEADERBOARD = "leaderboard";
 *
 * THREADING & UX
 *  - Create/show UI on the Swing EDT (SwingUtilities.invokeLater in MillionareApp).
 *  - All listener registration is exposed via onXxx(...) methods; no controller references stored here.
 *  - Disable lifeline buttons after use; guard against null/invalid states with friendly dialogs.
 *  - Keep components private; expose only the minimal API above.
 *
 * TESTABILITY & MARKING
 *  - No DB or engine logic here; pure view layer (MVC).
 *  - Keep layout code tidy and readable; small helper methods per screen are fine.
 */

public class GameUI extends JFrame {

    
}
