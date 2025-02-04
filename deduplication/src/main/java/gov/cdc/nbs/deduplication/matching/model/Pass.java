package gov.cdc.nbs.deduplication.matching.model;

import java.util.List;
import java.util.Map;

public class Pass {
    private List<String> blockingKeys;
    private List<Evaluator> evaluators;
    private String rule;
    private Map<String, Object> kwargs; // Additional parameters for the rule

    // Constructor
    public Pass(List<String> blockingKeys, List<Evaluator> evaluators, String rule, Map<String, Object> kwargs) {
        this.blockingKeys = blockingKeys;
        this.evaluators = evaluators;
        this.rule = rule;
        this.kwargs = kwargs;
    }

    // Getters and setters
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
