/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

/**
 *
 * @author rupertguppy
 */

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/*
 * File-backed question loader (preserves order)

// ChatGPT helped with a majority of this class specifically the loadAll() method

 * What this class does:
 * - Reads questions from "data/questions.txt" (or path given in ctor).
 * - Parses each record into a Question with exactly 4 AnswerOption objects.
 * - Identifies the correct option by index (0..3).
 * - Returns a populated QuestionBank in the file order.
 */
 
/**
 * QUESTION PARSER (seeding only; preserve file order).
 * What to change:
 *  - Expose a static parseQuestions(...) that returns List<Question> for initial DB seeding.
 *  - Validate each record has 4 options and a valid correct index.
 *  - Do NOT use this class at runtime for gameplay once DB is seeded.
 */

public class FileQuestionRepository {
    
    private final Path p;
    
    public FileQuestionRepository(){
        this(Paths.get("data", "questions.txt"));
    }
    
    public FileQuestionRepository(Path p){
        this.p = p;
    }
    // this method loads all the contents from the questions file where all the projects questions
    // are stored
    public QuestionBank loadAll() throws IOException {
        List<String> lines = FileManager.readAllLines(p);
        QuestionBank b = new QuestionBank();
        int lineNumber = 0;
        
        for(String raw : lines){
            lineNumber++;
            String l = raw.trim();
            
            if(l.isEmpty() || l.startsWith("#")) continue;
            
            String[] parse = l.split("\\|", -1);
            if(parse.length != 7) {
                throw new IllegalArgumentException
                        ("questions.txt line: "+lineNumber+ 
                        ", expected only 7 fields (ID|QUESTION|A|B|C|D|CORRECT)");
            }
            
            String Stem = parse[1].trim();
            String A = parse[2].trim();
            String B = parse[3].trim();
            String C = parse[4].trim();
            String D = parse[5].trim();
            String correctOption = parse[6].trim();
            
            if(correctOption.length() != 1){
                throw new IllegalArgumentException("questions.txt line: " +lineNumber+
                        ", CORRECT must be either A, B, C or D");
            }
            char character = Character.toUpperCase(correctOption.charAt(0));
            
            if(character < 'A' || character > 'D'){
                throw new IllegalArgumentException("questions.txt line: " +lineNumber+
                        ", CORRECT must be either A, B, C or D");
            }
            int correctIndex = character - 'A';
            
            b.add(Question.of(Stem, A, B, C, D, correctIndex));
        }        
        return b;
    }
}
