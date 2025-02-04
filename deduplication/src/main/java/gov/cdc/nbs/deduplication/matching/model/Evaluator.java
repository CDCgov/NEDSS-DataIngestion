package gov.cdc.nbs.deduplication.matching.model;

public class Evaluator {
    private String feature;
    private String func;  // Function used for evaluation

    // Constructor
    public Evaluator(String feature, String func) {
        this.feature = feature;
        this.func = func;
    }

    // Getters and setters
    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }
}

