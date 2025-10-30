/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

/**
 *
 * @author rupertguppy
 * 
 * this class is for helping when the lifeline if called. while it doesnt handle the 
 * logic for it but it has other methods to ensure that the lifeline is only used
 * once peer game
 */


public class LifeLine {
    
    private boolean lifeLineUsed = false;
    // method to check that this lifeline has been used
    public boolean isLifeLineUsed(){
        return lifeLineUsed;
    }
    // method to mark this lifeline as been used
    public void markLifeLineUsed(){
        this.lifeLineUsed = true;
    }
    // this method reveals the correct asnwer like a lifeline in the real game
    public int revealCorrectAnswer(Question q){
        if(lifeLineUsed || q == null){
            return -1;
            
        }
        lifeLineUsed = true;
        return q.getCorrectAnswer();
    }
 
}
