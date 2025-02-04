package gov.cdc.nbs.deduplication.matching.dto;

public class MatchingCriteria {
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
