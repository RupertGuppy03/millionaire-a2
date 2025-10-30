package com.mycompany.millionareapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// this test class is resposible for ensuring the some key features are working and pass all tests

public class MillionareGameTests {

    private QuestionBank bank;
    private GameEngine engine;
    private GameState state;

    @BeforeEach
        // setup a set of questions for testing
    void setup() {
        bank = new QuestionBank();
        // three tiny questions
        bank.add(Question.of("Q1?", "A", "B", "C", "D", 1)); // correct = B
        bank.add(Question.of("Q2?", "A", "B", "C", "D", 2)); // correct = C
        bank.add(Question.of("Q3?", "A", "B", "C", "D", 0)); // correct = A

        engine = new GameEngine(bank);
        state = engine.startGame("Tester");
    }
    // this test ensures that a new game is fresh, while current prize is set to 0
    @Test
    void startStateTest() {
        assertNotNull(engine.getCurrentQuestion(state), "Q1 does exist");
        assertFalse(engine.gameIsOver(state), "Game will not be over at start");
        assertEquals(0, engine.currentPrizeGet(state), "Prize starts at $0");
    }
    // this tests checks that a correct answer should advance you to the next 
    // one and update the prize amount
    @Test
    void correctAnswerTest() {
        Question q1 = engine.getCurrentQuestion(state);
        int idx = q1.getCorrectAnswer();

        engine.answer(state, idx);

        assertFalse(engine.gameIsOver(state), "Game continues after a correct answer");
        Question q2 = engine.getCurrentQuestion(state);
        assertNotNull(q2, "Q2 should exist");
        assertEquals("Q2?", q2.getQuestion());
        assertTrue(engine.currentPrizeGet(state) > 0, "Prize increased after getting a correct answer");
    }
    // this test ensures that getting  wrong answer will end the game
    @Test
    void wrongAnswerTest() {
        Question q1 = engine.getCurrentQuestion(state);
        int wrong = (q1.getCorrectAnswer() + 1) % 4;

        engine.answer(state, wrong);

        assertTrue(engine.gameIsOver(state), "Wrong answer, ending the game");
        assertEquals(0, engine.currentPrizeGet(state), "No prize after losing at Q1");
    }
    // this test checks that the fifty fifty lifeline removes two incorrect options
    @Test
    void fiftyFiftyTest() {
        Question q1 = engine.getCurrentQuestion(state);
        int correct = q1.getCorrectAnswer();

        int[] hide = engine.useFiftyFiftyLifeLine(state);
        assertEquals(2, hide.length, "50/50 will return exactly two indices");
        assertNotEquals(hide[0], hide[1], "Indices should be distinct");
        assertNotEquals(correct, hide[0], "Hidden options must be incorrect");
        assertNotEquals(correct, hide[1], "Hidden options must be incorrect");

        int[] second = engine.useFiftyFiftyLifeLine(state);
        assertEquals(0, second.length, "50/50 can be used only once");
    }
    // this test checks the the reveal answer lifeline gets the correct answer 
    // based on the correct index of that question
    @Test
    void reveal_returnsCorrectIndex_andOnlyOnce() {
        Question q1 = engine.getCurrentQuestion(state);
        int correct = q1.getCorrectAnswer();

        int idx1 = engine.revealCorrectAnswer(state);
        assertEquals(correct, idx1, "Reveal must return the correct option index");

        int idx2 = engine.revealCorrectAnswer(state);
        assertEquals(-1, idx2, "Reveal can be used only once");
    }
}
