package gov.cdc.nbs.deduplication.matching.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchingConfigRequest {
    private String label;
    private String description;
    private boolean isDefault;
    private boolean includeMultipleMatches;
    private List<Pass> passes;

    // Default constructor
    public MatchingConfigRequest() {}

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

class Pass {
    private String name;
    private String description;
    private String lowerBound;
    private String upperBound;
    private List<BlockingCriteria> blockingCriteria;
    private List<MatchingCriteria> matchingCriteria;

    // Default constructor
    public Pass() {}

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

class BlockingCriteria {
    private Field field;
    private Method method;

    // Default constructor
    public BlockingCriteria() {}

    // Getters and Setters
    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}

class MatchingCriteria {
    private Field field;
    private Method method;

    // Default constructor
    public MatchingCriteria() {}

    // Getters and Setters
    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}

class Field {
    private String value;
    private String name;

    // Default constructor
    public Field() {}

    // Getters and Setters
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Method {
    private String value;
    private String name;

    // Default constructor
    public Method() {}

    // Getters and Setters
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
