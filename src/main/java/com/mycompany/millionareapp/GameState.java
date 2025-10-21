/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

/**
 *
 * @author rupertguppy
 * 
 * this class handles the game state of the project. it helps the program 
 * understand where the game is at while keeping track of all aspects of the game
 */

/**
 * GAME SESSION STATE (owned by GameEngine).
 * What to change:
 *  - Add DB/session fields: playerId, sessionId, startedAt (Instant).
 *  - Add lifeline flags: usedFiftyFifty, usedReveal (+ getters/setters).
 *  - Add resetForNewGame(playerId, sessionId): clears counters, sets timestamps.
 *  - Keep all mutations encapsulated; GUIController reads state via GameEngine APIs.
 */


public class GameState {
    
    //variables for different game states
    private String name;
    private int currentQuestionNum; // 1-based
    private int currentPrize;
    private int LastPrize;
    private boolean hasUsed5050;
    private boolean hasUsedLifeline;
    private boolean gameOver;
    private boolean won;
    
    //constructor
    public GameState(String name){
        this.name = name;
        this.currentQuestionNum = 1; // 1-based
        this.currentPrize = 0;
        this.LastPrize = 0;
        this.hasUsed5050 = false;
        this.hasUsedLifeline = false;
        this.gameOver = false;
        this.won = false;
    }
    
    // getters 
    public String getName(){
        return name;
    }
    public int getQuestionNumber(){
        return currentQuestionNum;
    }
    public int getCurrentPrize(){
        return currentPrize;
    }
    public int getGuaranteedPrize(){
        return LastPrize;
    }
    public boolean hasUsed5050(){
        return hasUsed5050;
    }
    public boolean hasUsedLifeline(){
        return hasUsedLifeline;
    }
    public boolean getGameOver(){
        return gameOver;
    }
    public boolean getHasWon(){
        return won;
    }
    
    // setters for lifelines
    public void setHasUsed5050(boolean used){
        this.hasUsed5050 = used;     
    }
    public void setHasUsedLifeline(boolean used){
        this.hasUsedLifeline = used;
    }
    
    // helper method to move to next question
    public void nextQuestion(){
        this.currentQuestionNum++;
    }
    
    // helper method to update prize values when the player answers question correctly
    public void updatePrize(){
        this.currentPrize = MoneyTier.currentPrize(currentQuestionNum);
        this.LastPrize = MoneyTier.payoutLoss(currentQuestionNum);
    }
    
    // method to mark the game over
    public void gameOver(){
        this.gameOver = true;
    }
    
    // method to mark the game as won
    public void hasWon(){
        this.won = true;
        this.gameOver = true;
        this.currentPrize = MoneyTier.currentPrize(15);
    }   
}
