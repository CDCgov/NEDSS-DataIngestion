package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class AlgorithmPassTest {

    @Test
    void testAlgorithmPass() {
        Evaluator evaluator = new Evaluator("FIRST_NAME", "func:recordlinker.linking.matchers.compare_fuzzy_match");

        AlgorithmPass algorithmPass = new AlgorithmPass(
                List.of("FIRST_NAME", "LAST_NAME"),
                List.of(evaluator),
                "func:recordlinker.linking.matchers.rule_match",
                null
        );

        assertEquals(2, algorithmPass.blockingKeys().size());
        assertTrue(algorithmPass.blockingKeys().contains("FIRST_NAME"));
        assertTrue(algorithmPass.blockingKeys().contains("LAST_NAME"));

        assertEquals(1, algorithmPass.evaluators().size());
        assertEquals("FIRST_NAME", algorithmPass.evaluators().get(0).feature());  // Using feature() directly
        assertEquals("func:recordlinker.linking.matchers.compare_fuzzy_match", algorithmPass.evaluators().get(0).func());  // Using func() directly

        assertEquals("func:recordlinker.linking.matchers.rule_match", algorithmPass.rule());

        assertNull(algorithmPass.kwargs());
    }
}
