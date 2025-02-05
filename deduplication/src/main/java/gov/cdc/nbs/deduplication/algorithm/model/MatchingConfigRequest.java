package gov.cdc.nbs.deduplication.algorithm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.cdc.nbs.deduplication.algorithm.dto.Pass;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchingConfigRequest {
    private String label;
    private String description;
    private boolean isDefault;
    private boolean includeMultipleMatches;
    private List<Pass> passes;

    public MatchingConfigRequest() {
        // Default constructor
    }

    // Getters and Setters
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

