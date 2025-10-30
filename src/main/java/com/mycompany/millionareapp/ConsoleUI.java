/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.millionareapp;

import java.util.Scanner;
import java.io.PrintStream;


/**
 *
 * @author rupertguppy
 * 
 * this class prints out user friendly prompts to the UI for
 * example, confirm() which confirms the user input before executing the next step
 */

public class ConsoleUI {
    
    private final Scanner in;
    private final PrintStream out;
    
    // deafult constructor
    public ConsoleUI() {
        this.in = new Scanner(System.in);
        this.out = System.out;
    }
    
    // method that prints a line and moves onto the next line
    public void println(String msg){
        out.println(msg == null? "" : msg);
    }
    // method to print out text without a new line
    public void print(String msg){
        out.print(msg == null ? "" : msg);
        
        
    }
    
    // this method handles the answer part of the questionare and returns the trimmed text
    public String promptLine(String prompt, boolean allowEmpty){
        
        while(true) {
            print(prompt);
            
            if(!in.hasNextLine()){
                println("");
                continue;
            }
            
            String l = in.nextLine();
            
            if(!allowEmpty && l.trim().isEmpty()){
                println("Please enter an answer");
                continue;
            }
            if(allowEmpty) {
                return l;
            } else {
                return l.trim();
            }           
        }        
    }
    
    //this method makes sure the user input is within the correct range
    
    public int promptInt(String prompt, int min, int max){
        
        while(true){
            print(prompt);
            
            if(!in.hasNextLine()){
                print("");
                continue;
            }
            
            String s = in.nextLine().trim();
            
            try{
                int v = Integer.parseInt(s);
                
                if(v < min || v > max){
                    println("[ enter a number between "+min+" and "+max+" ]");
                    continue;
                }
                
                return v;
                
            } catch (NumberFormatException e){
                println("Invalid number, please pick a number from the options");
            }
            
        }
        
    }
    // this method conforms the user input
    public boolean confirm(String q) {
        while(true){
            print(q + " [ type 'y' for yes and 'n' for no ] ");
            
            if(!in.hasNextLine()){
                println("");
                continue;
            }
            
            String input = in.nextLine().trim().toLowerCase();
            
            switch(input){
                case "y":
                    return true;
                case "n":
                    return false;                    
                default:
                    println(" [ Please type 'y' for yes or 'n' for no ] ");      
            }
        }
    }
}
