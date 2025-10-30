/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author rupertguppy
 * 
 * this class provides the logic for each individual question, it holds the question at the stem and then checks
 * that there are exactly for answers with only one correct option between a range of 0-3. it validates the four
 * non-null options in the constructor and provides simple get, correctness check and to string methods
 */
public final class Question {

    
    private final String q;
    private final AnswerOption a, b, c, d;
    private final int correctAnswer;
    
    
    //simple constructor
    public Question(String q, 
            AnswerOption a, 
            AnswerOption b,
            AnswerOption c,
            AnswerOption d,
            int correctAnswer){
        
        //checking that question stem isnt empty
        if(q == null || q.trim().isEmpty()) {
            throw new IllegalArgumentException("Question cannot be empty!");
        }
        
        //checking if there are exactly 4 answer options for each question
        if(a == null || b == null || c == null || d == null) {
            throw new IllegalArgumentException("Each question must have exactly 4 options and none can be empty");
        }
                
        //checking if the correct answer index lies between 1 and 4
        if(correctAnswer < 0 || correctAnswer > 3){
            throw new IllegalArgumentException("correct answer index must be between 0-3");
            
        }
        
        // initialising variables in the constructor class
        this.q = q.trim();
        this.a = a;
        this.b = b;
        this.c =c;
        this.d = d;
        this.correctAnswer = correctAnswer;
    }
    
    public static Question of(String stem, String A, String B, String C, String D, int correctIndex) {
        return new Question(stem,
                new AnswerOption(A),
                new AnswerOption(B),
                new AnswerOption(C),
                new AnswerOption(D),
                correctIndex);
    }
    
    // get question method
    public String getQuestion(){
        return q;
    }
    // get correct answer method
    public int getCorrectAnswer(){
        return correctAnswer;
    }
    // checks that the answer options are within the correct range
    public AnswerOption getOption(int i){
        switch(i){
            case 0:
                return a;
            case 1:
                return b;
            case 2:
                return c;
            case 3:
                return d;
            default:
                throw new IndexOutOfBoundsException("Answer must be between options 0-3");
        }
    }
    // checks that the answer is correct
    public boolean isCorrect(int i){
        return i == correctAnswer;
    }

    public char getCorrectLetter(){
        return(char) ('A' + correctAnswer);  
    }
    // simple toString method for the answers to the questions
    @Override
    public String toString(){
        return q + System.lineSeparator() +
                "(A) " + a + System.lineSeparator() +
                "(B) " + b + System.lineSeparator() +
                "(C) " + c + System.lineSeparator() +
                "(D) " + d;
    }
}
