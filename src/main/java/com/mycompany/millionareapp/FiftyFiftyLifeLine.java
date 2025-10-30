/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

/**
 *
 * @author rupertguppy
 * 
 * What it does:
  - You can use it once per game. After that it’s “spent”.
  - When used, it picks the first two incorrect options (by index 0..3) and
    returns those indices so the UI can hide them. That leaves the player with two choices.
  - If it’s already been used, or the question is missing, it just returns an empty array.
  * 
 */



public class FiftyFiftyLifeLine {
    
    private boolean Used5050 = false;
    // method to check if this lifeline has been used
    public boolean is5050Used(){
        return Used5050;
    }
    // method to mark this lifeline as being already used
    public void mark5050Used(){
        this.Used5050 = true;
    }
    
    // this method hides 2 incorrect options to give the player two options to choose from
    public int[] hidingQuestions(Question q){
        if(Used5050 || q == null){
            return new int[0];
        }
        
        int[] hideQuestion = new int[2];
        int count = 0;
        
        for(int i = 0; i < 4 && count < 2; i++){
            if(!q.isCorrect(i)){
                hideQuestion[count++] = i;
            }
        }
        if(count == 2){
            Used5050 = true;
            return hideQuestion;
        }
        return new int[0];
    }

}
