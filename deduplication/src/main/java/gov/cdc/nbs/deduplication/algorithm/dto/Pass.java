package gov.cdc.nbs.deduplication.algorithm.dto;

import java.util.List;

public class Pass {
    private String name;
    private String description;
    private String lowerBound;
    private String upperBound;
    private List<BlockingCriteria> blockingCriteria;
    private List<MatchingCriteria> matchingCriteria;

    public Pass() {
        // Default constructor
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(String lowerBound) {
        this.lowerBound = lowerBound;
    }

    public String getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(String upperBound) {
        this.upperBound = upperBound;
    }

    public List<BlockingCriteria> getBlockingCriteria() {
        return blockingCriteria;
    }

    public void setBlockingCriteria(List<BlockingCriteria> blockingCriteria) {
        this.blockingCriteria = blockingCriteria;
    }

    public List<MatchingCriteria> getMatchingCriteria() {
        return matchingCriteria;
    }

    public void setMatchingCriteria(List<MatchingCriteria> matchingCriteria) {
        this.matchingCriteria = matchingCriteria;
    }
}

