package gov.cdc.nbs.deduplication.algorithm.dto;

public class Field {
    private String value;
    private String name;

    public Field() {
        // Default constructor
    }

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
