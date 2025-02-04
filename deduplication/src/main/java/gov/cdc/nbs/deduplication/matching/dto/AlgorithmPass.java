package gov.cdc.nbs.deduplication.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AlgorithmPass {

    @JsonProperty("blocking_keys")
    private List<String> blockingKeys;

    @JsonProperty("evaluators")
    private List<Evaluator> evaluators;

    @JsonProperty("rule")
    private String rule;

    @JsonProperty("kwargs")
    private Object kwargs;

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

    public Object getKwargs() {
        return kwargs;
    }

    public void setKwargs(Object kwargs) {
        this.kwargs = kwargs;
    }
}
