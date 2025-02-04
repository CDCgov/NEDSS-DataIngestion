package gov.cdc.nbs.deduplication.matching.model;

import gov.cdc.nbs.deduplication.matching.dto.Pass;

import java.util.List;

public class MatchingConfiguration {
    private Long id;  // Primary key if needed
    private String label;
    private String description;
    private boolean isDefault;
    private boolean includeMultipleMatches;
    private List<Pass> passes;  // Matching logic passes

    // Default Constructor
    public MatchingConfiguration() {
    }

    // Constructor with fields
    public MatchingConfiguration(Long id, String label, String description, boolean isDefault, boolean includeMultipleMatches, List<Pass> passes) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.isDefault = isDefault;
        this.includeMultipleMatches = includeMultipleMatches;
        this.passes = passes;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
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
