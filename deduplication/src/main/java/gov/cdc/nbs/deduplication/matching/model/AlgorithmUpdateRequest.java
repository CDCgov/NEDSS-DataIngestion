package gov.cdc.nbs.deduplication.matching.model;
import java.util.List;
import gov.cdc.nbs.deduplication.matching.dto.AlgorithmPass;

public class AlgorithmUpdateRequest {
    private String label;
    private String description;
    private boolean isDefault;
    private boolean includeMultipleMatches;
    private Double[] belongingnessRatio;
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
