package gov.cdc.nbs.deduplication.matching.model;

import java.util.List;

public class MatchingConfigRequest {
    private String label;
    private String description;
    private boolean isDefault;
    private boolean includeMultipleMatches;
    private List<Pass> passes;  // List of passes defining the algorithm behavior

    // Constructor
    public MatchingConfigRequest(String label, String description, boolean isDefault,
                                 boolean includeMultipleMatches, List<Pass> passes) {
        this.label = label;
        this.description = description;
        this.isDefault = isDefault;
        this.includeMultipleMatches = includeMultipleMatches;
        this.passes = passes;
    }

    // Getters and setters
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isIncludeMultipleMatches() {
        return includeMultipleMatches;
    }

    public void setIncludeMultipleMatches(boolean includeMultipleMatches) {
        this.includeMultipleMatches = includeMultipleMatches;
    }

    public List<Pass> getPasses() {
        return passes;
    }

    public void setPasses(List<Pass> passes) {
        this.passes = passes;
    }
}

