package gov.cdc.nbs.deduplication.matching.dto;

public class Method {
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
