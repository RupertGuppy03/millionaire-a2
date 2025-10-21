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
        // getting file path to the questions.txt file
        Path path = Paths.get("data", "questions.txt");

        // Load questions from file
        FileQuestionRepository repo = new FileQuestionRepository(path);
        
        QuestionBank bank;
        try {
            bank = repo.loadAll();  
            System.out.println("Loaded questions: " + bank.size());
        } catch (Exception e) {        // IOException or IllegalArgumentException → friendly message
            System.err.println("Failed to load questions.txt: " + e.getMessage());
            return; // bail to avoid NPEs later
        }


        // these classes handle the game logic and ensure that the game runs smoothly
        GameEngine engine = new GameEngine(bank);
        GamePersistance gp = new GamePersistance();
        new GameController(engine, gp).run();
            
    }
}

