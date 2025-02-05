package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    @Test
    void testSetAndGetFeature() {
        Evaluator evaluator = new Evaluator("FIRST_NAME", "func:recordlinker.linking.matchers.compare_fuzzy_match");

        evaluator.setFeature("LAST_NAME");

        assertEquals("LAST_NAME", evaluator.getFeature());
    }

    @Test
    void testSetAndGetFunc() {
        Evaluator evaluator = new Evaluator("FIRST_NAME", "func:recordlinker.linking.matchers.compare_fuzzy_match");

        evaluator.setFunc("func:recordlinker.linking.matchers.compare_match_any");

        assertEquals("func:recordlinker.linking.matchers.compare_match_any", evaluator.getFunc());
    }

    @Test
    void testFullObject() {
        Evaluator evaluator = new Evaluator("FIRST_NAME", "func:recordlinker.linking.matchers.compare_fuzzy_match");

        assertNotNull(evaluator.getFeature());
        assertEquals("FIRST_NAME", evaluator.getFeature());

        assertNotNull(evaluator.getFunc());
        assertEquals("func:recordlinker.linking.matchers.compare_fuzzy_match", evaluator.getFunc());
    }
}

