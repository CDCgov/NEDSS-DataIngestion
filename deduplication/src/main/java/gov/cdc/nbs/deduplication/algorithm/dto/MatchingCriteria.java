package gov.cdc.nbs.deduplication.algorithm.dto;

public class MatchingCriteria {
    private Field field;
    private Method method;

    public MatchingCriteria() {
        // Default constructor
    }

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
