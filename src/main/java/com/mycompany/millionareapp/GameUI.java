package com.mycompany.millionareapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * GameUI — Single-window Swing UI (CardLayout; inner screens). Swing/AWT only.
 * Pure view: no business logic. GUIController attaches all listeners.
 *
 * Screens: MENU, GAME, LEADERBOARD
 * Public API:
 *  - Navigation: showMenu(), showGame(), showLeaderboard()
 *  - Menu: getPlayerName(), onStart(...), onLeaderboard(...), onQuit(...)
 *  - Game: setTierText(...), setQuestionText(...), setOption(i,...),
 *          enableLifeline(name,boolean), showSummary(...),
 *          onAnswer(i,...), onFifty(...), onReveal(...), onBack(...)
 *  - Leaderboard: setLeaderboardRows(List<Object[]>), onLeaderboardBack(...)
 */
public class GameUI extends JFrame {

    // Card keys
    public static final String screenMenu = "MENU";
    public static final String screenGame = "GAME";
    public static final String screenLeaderboard = "LEADERBOARD";

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    private final MenuCard menuCard = new MenuCard();
    private final GameCard gameCard = new GameCard();
    private final LeaderboardCard leaderboardCard = new LeaderboardCard();

    public GameUI() {
        super("Who Wants to Be a Millionaire");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        setLocationRelativeTo(null);

        root.add(menuCard, screenMenu);
        root.add(gameCard, screenGame);
        root.add(leaderboardCard, screenLeaderboard);
        setContentPane(root);
    }

    // -------- Navigation --------
    public void showMenu()        { cards.show(root, screenMenu); }
    public void showGame()        { cards.show(root, screenGame); }
    public void showLeaderboard() { cards.show(root, screenLeaderboard); }

    // -------- Menu API --------
    public String getPlayerName()               { return menuCard.getPlayerName(); }
    public void onStart(ActionListener l)       { menuCard.onStart(l); }
    public void onLeaderboard(ActionListener l) { menuCard.onLeaderboard(l); }
    public void onQuit(ActionListener l)        { menuCard.onQuit(l); }

    // -------- Game API --------
    public void setTierText(String text)                { gameCard.setTierText(text); }
    public void setQuestionText(String text)            { gameCard.setQuestionText(text); }
    public void setOption(int idx, String text)         { gameCard.setOption(idx, text); }
    public void enableLifeline(String name, boolean on) { gameCard.enableLifeline(name, on); }
    public void showSummary(String text)                { gameCard.showSummary(text); }
    public void onAnswer(int idx, ActionListener l)     { gameCard.onAnswer(idx, l); }
    public void onFifty(ActionListener l)               { gameCard.onFifty(l); }
    public void onReveal(ActionListener l)              { gameCard.onReveal(l); }
    public void onBack(ActionListener l)                { gameCard.onBack(l); }

    // -------- Leaderboard API --------
    public void setLeaderboardRows(List<Object[]> rows) { leaderboardCard.setRows(rows); }
    public void onLeaderboardBack(ActionListener l)     { leaderboardCard.onBack(l); }

    // ===================== Inner Panels =====================

    private static class MenuCard extends JPanel {
        private final JTextField playerName = new JTextField(20);
        private final JButton startButton = new JButton("Start");
        private final JButton leaderboardButton = new JButton("Leaderboard");
        private final JButton quitButton = new JButton("Quit");

        MenuCard() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);

            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Player Name:"), gbc);
            gbc.gridx = 1;
            add(playerName, gbc);

            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
            add(startButton, gbc);
            gbc.gridy++;
            add(leaderboardButton, gbc);
            gbc.gridy++;
            add(quitButton, gbc);
        }

        String getPlayerName() { return playerName.getText().trim(); }
        void onStart(ActionListener l)       { startButton.addActionListener(l); }
        void onLeaderboard(ActionListener l) { leaderboardButton.addActionListener(l); }
        void onQuit(ActionListener l)        { quitButton.addActionListener(l); }
    }

    private static class GameCard extends JPanel {
        private final JLabel tierLabel = new JLabel("Tier");
        private final JTextArea questionArea = new JTextArea(5, 50);
        private final JButton[] answerButtons = {
                new JButton("A"), new JButton("B"), new JButton("C"), new JButton("D")
        };
        private final JButton fiftyButton = new JButton("50/50");
        private final JButton revealButton = new JButton("Reveal");
        private final JButton backButton = new JButton("Back");
        private final JLabel summary = new JLabel(); // hidden until game over

        GameCard() {
            setLayout(new BorderLayout(8, 8));

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            top.add(new JLabel("Current Tier: "));
            top.add(tierLabel);
            add(top, BorderLayout.NORTH);

            questionArea.setLineWrap(true);
            questionArea.setWrapStyleWord(true);
            questionArea.setEditable(false);
            add(new JScrollPane(questionArea), BorderLayout.CENTER);

            JPanel centerButtons = new JPanel(new GridLayout(2, 2, 8, 8));
            for (JButton b : answerButtons) centerButtons.add(b);

            JPanel lifelines = new JPanel(new FlowLayout(FlowLayout.LEFT));
            lifelines.add(fiftyButton);
            lifelines.add(revealButton);

            JPanel bottom = new JPanel(new BorderLayout());
            bottom.add(lifelines, BorderLayout.NORTH);
            bottom.add(centerButtons, BorderLayout.CENTER);

            JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            bottomBar.add(backButton);
            bottom.add(bottomBar, BorderLayout.SOUTH);

            add(bottom, BorderLayout.SOUTH);

            summary.setHorizontalAlignment(SwingConstants.CENTER);
            summary.setVisible(false);
            add(summary, BorderLayout.WEST); // simple placement; controller decides when to show
        }

        void setTierText(String t)            { tierLabel.setText(t != null ? t : ""); }
        void setQuestionText(String t)        { questionArea.setText(t != null ? t : ""); }
        void setOption(int idx, String text)  {
            if (idx >= 0 && idx < answerButtons.length) answerButtons[idx].setText(text != null ? text : "");
        }
        void enableLifeline(String name, boolean on) {
            if (name == null) return;
            if ("50/50".equalsIgnoreCase(name)) fiftyButton.setEnabled(on);
            if ("REVEAL".equalsIgnoreCase(name) || "REVEAL ANSWER".equalsIgnoreCase(name)) revealButton.setEnabled(on);
        }
        void showSummary(String t) {
            summary.setText(t != null ? t : "");
            summary.setVisible(true);
        }
        void onAnswer(int idx, ActionListener l) {
            if (idx >= 0 && idx < answerButtons.length) answerButtons[idx].addActionListener(l);
        }
        void onFifty(ActionListener l)  { fiftyButton.addActionListener(l); }
        void onReveal(ActionListener l) { revealButton.addActionListener(l); }
        void onBack(ActionListener l)   { backButton.addActionListener(l); }
    }

    private static class LeaderboardCard extends JPanel {
        private final JTable table = new JTable();
        private final DefaultTableModel model =
                new DefaultTableModel(new Object[]{"Player", "Winnings", "Finished At"}, 0) {
                    @Override public boolean isCellEditable(int r, int c) { return false; }
                };
        private final JButton backButton = new JButton("Back");

        LeaderboardCard() {
            setLayout(new BorderLayout(8, 8));
            table.setModel(model);
            add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            south.add(backButton);
            add(south, BorderLayout.SOUTH);
        }

        void setRows(List<Object[]> rows) {
            model.setRowCount(0);
            if (rows == null) return;
            for (Object[] r : rows) model.addRow(r);
        }
        void onBack(ActionListener l) { backButton.addActionListener(l); }
    }
}
