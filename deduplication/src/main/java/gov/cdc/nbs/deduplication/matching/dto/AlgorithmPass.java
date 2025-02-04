package gov.cdc.nbs.deduplication.matching.dto;

import java.util.List;
import java.util.Map;

public class AlgorithmPass {
    private List<String> blockingKeys;
    private List<Evaluator> evaluators;
    private String rule;
    private Map<String, Object> kwargs;

    // Getters and Setters
    public List<String> getBlockingKeys() {
        return blockingKeys;
    }

    public void setBlockingKeys(List<String> blockingKeys) {
        this.blockingKeys = blockingKeys;
    }

    public List<Evaluator> getEvaluators() {
        return evaluators;
    }

    public void setEvaluators(List<Evaluator> evaluators) {
        this.evaluators = evaluators;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public Map<String, Object> getKwargs() {
        return kwargs;
    }

    public void setKwargs(Map<String, Object> kwargs) {
        this.kwargs = kwargs;
    }
}