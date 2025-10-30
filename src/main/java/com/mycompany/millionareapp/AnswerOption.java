/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

/**
 *
 * @author rupertguppy
 * 
 * this class maintains that the correct answer option is correctly validated by making sure that 
 * the answer option cannot be empty or null
 */

public final class AnswerOption {
    
    private final String txt;
    
    // this constructor checks that the text option isnt empty and throws a exception
    // with a friendly message to avoid the program from chashing
    public AnswerOption(String txt){
        
        if(txt == null || txt.trim().isEmpty()) {
            throw new IllegalArgumentException("The Answer Option text cant be empty!");
        }
        
        this.txt = txt.trim();
    }
    
    //simple getter
    
    public String getText(){
        return txt;
    }
    
    // simple toString method
    @Override
    public String toString(){
        return txt;
    } 
}
