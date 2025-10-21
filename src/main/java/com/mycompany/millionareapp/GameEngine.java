/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

/**
 *
 * @author rupertguppy
 * 
 * GameEngine: this is the “rules” brain of the game.

  What it’s responsible for:
  - Knows which question you’re on and decides what happens when you answer.
  - Moves you forward on correct answers, or ends the game on a wrong one.
  - Keeps lifelines honest: you can use 50/50 and Reveal once each per game.
  - No printing here — just pure logic. The controller/UI handles all text.
 */


/**
 * DOMAIN LOGIC ONLY (no UI/DB).
 * What to change:
 *  - Add/resetWithQuestions(List<Question>) to accept DB-loaded questions.
 *  - Expose GUI-friendly API: getCurrentQuestion(), getCurrentQuestionNumber(), answer(int),
 *    isGameOver(), isWon(), getCurrentPrize().
 *  - Implement lifelines via state flags: useFiftyFifty() / useReveal() return indices; one-time use enforced.
 *  - Use MoneyTier for prize/guaranteed tiers; do not print to console here.
 */

public class GameEngine {
    
    private final QuestionBank questionBank;
    
    private LifeLine reveal = new LifeLine();
    private FiftyFiftyLifeLine fifty = new FiftyFiftyLifeLine();
    
    public GameEngine(QuestionBank questionBank){
        this.questionBank = questionBank;
    }
    // method to create a new game with the fresh lifelines
    public GameState startGame(String name){
        this.reveal = new LifeLine();
        this.fifty = new FiftyFiftyLifeLine();
        return new GameState(name);

    }
    // method which returns the current question
    public Question getCurrentQuestion(GameState status){
        if(status == null) {
            return null;
        }
        
        int index = status.getQuestionNumber();
        
        if(index < 1 || index > questionBank.size()){
            return null;
        }
        return questionBank.getByNumber(index);
    }
    // method to check if game is over
    public boolean gameIsOver(GameState status){
        return status != null && status.getGameOver();
    }
    // method to check that the player has won
    public boolean isWon(GameState status){
        return status != null && status.getHasWon();
    }
    // method to return the prize if the player has won one
    public int currentPrizeGet(GameState status){
        return status == null ? 0: status.getCurrentPrize();
    }
    
    public void answer(GameState status, int chosenQuestionIndex){
    
        if(status == null || gameIsOver(status)) return;
        
        Question q = getCurrentQuestion(status);
        
        if(q == null){
            status.gameOver();
            return;
        }
        
        if(chosenQuestionIndex < 0 || chosenQuestionIndex > 3){
            status.gameOver();
            return;
        }
        
        boolean correct = q.isCorrect(chosenQuestionIndex);
        
        if(correct){
            status.updatePrize();
            
            boolean lastQuestion = (status.getQuestionNumber() >= questionBank.size());
            
            if(lastQuestion){
                status.hasWon(); // resets the state as the player has won and 
                                    // gets the final prize of $1,000,000
            } else {
                status.nextQuestion();
            }
        } else {
            status.gameOver();
        }       
    }
    
    //----------LifeLines-----------
    
    // this method reveals the correct answer like a lifeline
    public int revealCorrectAnswer(GameState status){
        if(status == null || gameIsOver(status)){
            return -1;
        }
        
        if(status.hasUsedLifeline() || reveal.isLifeLineUsed()){
            return -1;
        }
        
        Question q = getCurrentQuestion(status);
        int index = reveal.revealCorrectAnswer(q);
        
        if(index != -1){
            status.setHasUsedLifeline(true);
        }
        return index;
    }
    // this method hadles the logic for fiftyfifty lifelane. checks if it has been used, if not 
    // it removes half of the questions like a 5050 lifeline
    public int[] useFiftyFiftyLifeLine(GameState status){
        if(status == null || gameIsOver(status)){
            return new int[0];
        }
        
        if(status.hasUsed5050() || fifty.is5050Used()){
            return new int[0];
        }
        
        Question q = getCurrentQuestion(status);
        
        int[] hide = fifty.hidingQuestions(q);
        
        if(hide.length == 2){
            status.setHasUsed5050(true);
        }
        return hide;
    }
}
