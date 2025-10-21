/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

/**
 *
 * @author rupertguppy
 * 
 * What this class does:
  - Keeps a plain text file at data/highscores.txt.
  - Each score is one line in the format: playerName|score  (e.g., "Rupert|32000").
  - Creates the file/folders if they don’t exist.
  - When reading, it just returns all lines; if anything goes wrong, you get an empty list.

 */

/**
 * DEPRECATED FILE-BASED PERSISTENCE (wraps DB for compatibility).
 * What to change:
 *  - Annotate @Deprecated; internally delegate to GameRepository for sessions/leaderboard.
 *  - Remove/disable file-based highscores/save (DB is the source of truth).
 *  - Prefer calling GameRepository directly from GUIController where practical.
 */

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// this is how this class works:

/*
      Add one high score to the file.

      How it works:
      - Builds a single line like "Alice|1000".
      - Ensures the highscores file (and its folder) exist.
      - Appends the line with a newline at the end.
      - If the write fails, we throw an IllegalStateException so the UI can report it.
    */


public class GamePersistance {
    
    private final Path highscoresFile = Paths.get("data", "highscores.txt");
    // method to append the highscore
    public void appendHighScore(String name, int prize){
        String safeName = (name == null ? "" : name.replace('|', '/').trim());
        
        String line = safeName + '|' + prize;
        
        try{
            FileManager.ensureFile(highscoresFile);
            FileManager.appendLine(highscoresFile, line);
        } catch(IOException e){
            throw new IllegalStateException("Failed to append highscore", e);
        }
    }
    // method to load all highscores
    public List<String> loadHighScores(){
        try{
            return FileManager.readAllLines(highscoresFile);
        } catch(IOException e){
            return List.of();
        }
    }
    
    

}
