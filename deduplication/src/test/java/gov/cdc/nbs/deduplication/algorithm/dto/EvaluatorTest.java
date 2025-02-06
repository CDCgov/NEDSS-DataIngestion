package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EvaluatorTest {

    @Test
    void testEvaluator() {
        Evaluator evaluator = new Evaluator("FIRST_NAME", "func:recordlinker.linking.matchers.compare_fuzzy_match");

        assertEquals("FIRST_NAME", evaluator.feature());
        assertEquals("func:recordlinker.linking.matchers.compare_fuzzy_match", evaluator.func());
    }
}
