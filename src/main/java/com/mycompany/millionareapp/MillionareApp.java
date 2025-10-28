/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.millionareapp;

/**
 *
 * @author rupertguppy
 * 
 * ChatGPT helped with this class
 * 
 * this class is the main method of the project. it uses the data from the 
 * questions.txt file through the file question repository class so cant 
 * directly interfere with the file. it also builds the game through the game 
 * engine and game persistence classes and passes everything to the game 
 * controller class to ensure the game runs smoothly
 */

/**
 * APPLICATION ENTRY (GUI-first).
 * What to change:
 *  - Launch GUI on the EDT (SwingUtilities.invokeLater).
 *  - Construct GameRepository("db/MillionaireDB"), call ensureSchema() and (optionally) seedIfEmpty(...).
 *  - Create GameEngine and feed repo.findAllQuestions().
 *  - Create GameUI + GUIController and call controller.start(); set frame visible.
 *  - (Optional) Keep CLI flag to start legacy console path for testing.
 */

import java.nio.file.Path;
import java.nio.file.Paths;





public class MillionareApp {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // DB repo (Derby Embedded); safe even if methods are still TODO
            GameRepository repo = new GameRepository("db/MillionaireDB");
            repo.ensureSchema(); // creates tables if missing

            GameEngine engine = null; // not used yet (placeholders in GUIController)
            GameUI ui = new GameUI();

            GUIController controller = new GUIController(ui, engine, repo);
            controller.start();      // hooks up all listeners, shows Menu
            ui.setVisible(true);     // display the window
        });
            
    }
}

