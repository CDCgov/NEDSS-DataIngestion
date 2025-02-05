package gov.cdc.nbs.deduplication.algorithm.dto;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmPassTest {

    @Test
    void testSetAndGetBlockingKeys() {
        AlgorithmPass pass = new AlgorithmPass();
        List<String> blockingKeys = List.of("FIRST_NAME", "LAST_NAME", "ADDRESS");

        pass.setBlockingKeys(blockingKeys);

        assertEquals(blockingKeys, pass.getBlockingKeys());
    }

    @Test
    void testSetAndGetEvaluators() {
        AlgorithmPass pass = new AlgorithmPass();
        Evaluator evaluator = new Evaluator("FIRST_NAME", "func:recordlinker.linking.matchers.compare_fuzzy_match");
        List<Evaluator> evaluators = List.of(evaluator);

        pass.setEvaluators(evaluators);

        assertEquals(evaluators, pass.getEvaluators());
        assertEquals("FIRST_NAME", pass.getEvaluators().get(0).getFeature());  // Verify evaluator's feature
        assertEquals("func:recordlinker.linking.matchers.compare_fuzzy_match", pass.getEvaluators().get(0).getFunc());  // Verify evaluator's func
    }

    @Test
    void testSetAndGetRule() {
        AlgorithmPass pass = new AlgorithmPass();
        String rule = "func:recordlinker.linking.matchers.rule_match";

        pass.setRule(rule);

        assertEquals(rule, pass.getRule());
    }

    @Test
    void testSetAndGetKwargs() {
        AlgorithmPass pass = new AlgorithmPass();

        // Test with a simple String object
        String kwargsString = "some_parameter_value";
        pass.setKwargs(kwargsString);

        assertEquals(kwargsString, pass.getKwargs());

        // Test with a Map object
        Map<String, String> kwargsMap = Map.of("key1", "value1", "key2", "value2");
        pass.setKwargs(kwargsMap);

        assertEquals(kwargsMap, pass.getKwargs());
    }

    @Test
    void testFullObject() {
        AlgorithmPass pass = new AlgorithmPass();
        pass.setBlockingKeys(List.of("FIRST_NAME", "LAST_NAME"));
        pass.setEvaluators(List.of(new Evaluator("FIRST_NAME", "func:recordlinker.linking.matchers.compare_fuzzy_match")));
        pass.setRule("func:recordlinker.linking.matchers.rule_match");
        pass.setKwargs(Map.of("key1", "value1"));

        assertNotNull(pass.getBlockingKeys());
        assertEquals(2, pass.getBlockingKeys().size());
        assertEquals("FIRST_NAME", pass.getBlockingKeys().get(0));

        assertNotNull(pass.getEvaluators());
        assertEquals(1, pass.getEvaluators().size());
        assertEquals("FIRST_NAME", pass.getEvaluators().get(0).getFeature());

        assertEquals("func:recordlinker.linking.matchers.rule_match", pass.getRule());

        assertNotNull(pass.getKwargs());
        assertTrue(pass.getKwargs() instanceof Map);
        assertEquals("value1", ((Map) pass.getKwargs()).get("key1"));
    }
}

