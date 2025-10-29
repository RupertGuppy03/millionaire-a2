package com.mycompany.millionareapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MillionareGameTests {

    private QuestionBank bank;
    private GameEngine engine;
    private GameState state;

    @BeforeEach
    void setup() {
        bank = new QuestionBank();
        // three tiny questions
        bank.add(Question.of("Q1?", "A", "B", "C", "D", 1)); // correct = B
        bank.add(Question.of("Q2?", "A", "B", "C", "D", 2)); // correct = C
        bank.add(Question.of("Q3?", "A", "B", "C", "D", 0)); // correct = A

        engine = new GameEngine(bank);
        state = engine.startGame("Tester");
    }

    @Test
    void startState_isClean() {
        assertNotNull(engine.getCurrentQuestion(state), "Q1 does exist");
        assertFalse(engine.gameIsOver(state), "Game will not be over at start");
        assertEquals(0, engine.currentPrizeGet(state), "Prize starts at $0");
    }

    @Test
    void correctAnswer_advancesToNextQuestion() {
        Question q1 = engine.getCurrentQuestion(state);
        int idx = q1.getCorrectAnswer();

        engine.answer(state, idx);

        assertFalse(engine.gameIsOver(state), "Game continues after a correct answer");
        Question q2 = engine.getCurrentQuestion(state);
        assertNotNull(q2, "Q2 should exist");
        assertEquals("Q2?", q2.getQuestion());
        assertTrue(engine.currentPrizeGet(state) > 0, "Prize increased after getting a correct answer");
    }

    @Test
    void wrongAnswer_endsGameAndPrizeStaysAtZero() {
        Question q1 = engine.getCurrentQuestion(state);
        int wrong = (q1.getCorrectAnswer() + 1) % 4;

        engine.answer(state, wrong);

        assertTrue(engine.gameIsOver(state), "Wrong answer, ending the game");
        assertEquals(0, engine.currentPrizeGet(state), "No prize after losing at Q1");
    }

    @Test
    void fiftyFifty_returnsTwoIncorrect_andOnlyOnce() {
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
