/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author rupertguppy
 * 
 * What this class does:
  - Stores questions exactly in the order you add them (same as file order).
  - Lets you ask “how many questions do we have?”.
  - Lets you fetch a question by its 1-based number (Q1 = first, Q2 = second, etc.).
  - Uses a bounds check so you get a clear error if you ask for an invalid number.
 */

/**
 * IN-MEMORY QUESTION SOURCE (DB-backed).
 * What to change:
 *  - Remove file coupling; build from List<Question> loaded via GameRepository.
 *  - Provide fromList(List<Question>), get(int index), size(), and (optionally) getByNumber(1-based).
 *  - Preserve original question order as provided by the DB/seed.
 */


public class QuestionBank {
    // TODO: private List<Question> questions; + methods above.
    
    private final List<Question> questions = new ArrayList<>();
    
    public void add(Question question){
        if(question == null) throw new NullPointerException("question");
        questions.add(question);
    }
    // returning the total number of questions
    public int size(){
        return questions.size();
    }
    
    public Question getByNumber(int index){
        if(index < 1 || index > questions.size()){
            throw new IndexOutOfBoundsException("question size must be between 1 and "+questions.size());
        }
        return questions.get(index - 1);
    }

}
