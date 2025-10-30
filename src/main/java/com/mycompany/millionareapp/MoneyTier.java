/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

/**
 *
 * @author rupertguppy
 * 
 *  What this class :
 * 
 * it is an enum type class where Each enum value represents a step on the ladder 
 * (Q1..Q15) with a prize amount. Some steps are “safe havens” (Q5 = $1,000 and 
 * Q10 = $32,000). If you lose after passing one of these, you take home that 
 * safe amount. Provides tiny helpers to look up prizes by question number, 
 * check if a step is safe, and work out payouts for losing or walking away.
*/

public enum MoneyTier {
    
    Q1(1, 100, false),
    Q2(2, 200, false),
    Q3(3, 300, false),
    Q4(4, 500, false),
    Q5(5, 1000, true),  // safe haven
    Q6(6, 2000, false),
    Q7(7, 4000, false),
    Q8(8, 8000, false),
    Q9(9, 16000, false),
    Q10(10, 32000, true), // safe haven
    Q11(11, 64000, false),
    Q12(12, 125000, false),
    Q13(13, 250000, false),
    Q14(14, 500000, false),
    Q15(15, 1000000, false);
    
    private final int questionNumber;
    private final int prizeMoney;
    private final boolean safeHaven;
    // basic enum constructor to initialise my variables
    MoneyTier(int questionNumber, int prizeMoney, boolean safeHaven){
        this.questionNumber = questionNumber;
        this.prizeMoney = prizeMoney;
        this.safeHaven = safeHaven;
    }
    //get mthod for question number
    public int getNumber(){
        return questionNumber;
    }
    //get method for prize money
    public int getPrize(){
        return prizeMoney;
    }
    // get method for safe havens
    public boolean getSafeHaven(){
        return safeHaven;
    }
    //method to find the money tier by the question number
    public static MoneyTier byNumber(int number){
        for(MoneyTier t : values()){
            if(t.questionNumber == number){
                return t;
            }
        }
        return null;
    }
    
    //method to check if safe haven is the current question number
    public static boolean isSafe(int number){
        MoneyTier tier = byNumber(number);
        return tier != null && tier.safeHaven;
    
    }
    //method to get the prize money from the current question number
    public static int currentPrize(int number) {
        MoneyTier t = byNumber(number);
        return t == null ? 0 : t.getPrize();
    }
    //mthod to get the loss payout after the player loses
    public static int payoutLoss(int lastCorrect){
        int safeHavenPrize = 0;
        for(MoneyTier t : values()){
            if(t.getNumber() <= lastCorrect && t.getSafeHaven()){
                safeHavenPrize = t.prizeMoney;
                
            }
        }
        return safeHavenPrize;
    }
    // method to get the payout if the player quits halfway through the game
    public static int walkAwayPay(int lastCorrect){
        return currentPrize(lastCorrect);
    }

}
