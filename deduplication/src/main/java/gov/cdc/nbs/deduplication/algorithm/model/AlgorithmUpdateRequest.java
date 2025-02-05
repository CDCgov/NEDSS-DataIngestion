package gov.cdc.nbs.deduplication.algorithm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import gov.cdc.nbs.deduplication.algorithm.dto.AlgorithmPass;

public class AlgorithmUpdateRequest {

    @JsonProperty("label")
    private String label;

    @JsonProperty("description")
    private String description;

    @JsonProperty("is_default")
    private boolean isDefault;

    @JsonProperty("include_multiple_matches")
    private boolean includeMultipleMatches;

    @JsonProperty("belongingness_ratio")
    private Double[] belongingnessRatio;

    @JsonProperty("passes")
    private List<AlgorithmPass> passes;

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

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isIncludeMultipleMatches() {
        return includeMultipleMatches;
    }

    public void setIncludeMultipleMatches(boolean includeMultipleMatches) {
        this.includeMultipleMatches = includeMultipleMatches;
    }

    public Double[] getBelongingnessRatio() {
        return belongingnessRatio;
    }

    public void setBelongingnessRatio(Double[] belongingnessRatio) {
        this.belongingnessRatio = belongingnessRatio;
    }

    public List<AlgorithmPass> getPasses() {
        return passes;
    }

    public void setPasses(List<AlgorithmPass> passes) {
        this.passes = passes;
    }
}
