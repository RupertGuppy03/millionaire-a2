/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.millionareapp;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author rupertguppy
 * 
/**
 * ChatGPT assisted with this class.
 *
 * Entry point for the GUI version. Runs on the Swing EDT, initializes the
 * embedded Apache Derby database (creates tables and seeds from data/questions.txt
 * on first run), builds the GameUI and GUIController, and shows the main window.
 * Startup wiring only — game play logic and database code live in their own classes.
 */




    

public class MillionareApp {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // DB bootstrap
            GameRepository repo = new GameRepository("db/MillionaireDB");
            repo.ensureSchema();

            // Seed questions once (safe to ignore failures)
            try {
                FileQuestionRepository fileRepo =
                        new FileQuestionRepository(java.nio.file.Paths.get("data", "questions.txt"));
                QuestionBank bank = fileRepo.loadAll();

                java.util.List<Question> starter = new java.util.ArrayList<>();
                for (int i = 1; i <= bank.size(); i++) {
                    starter.add(bank.getByNumber(i));
                }
                int inserted = repo.seedIfEmpty(starter);
                if (inserted > 0) {
                    System.out.println("Seeded " + inserted + " questions.");
                }
            } catch (Exception ignore) {
                // Keep GUI usable even if seeding fails.
            }

            // UI and controller setup
            GameUI ui = new GameUI();
            GUIController controller = new GUIController(ui, /* engine */ null, repo);
            controller.start();
            ui.setVisible(true);
        });
    }
}

