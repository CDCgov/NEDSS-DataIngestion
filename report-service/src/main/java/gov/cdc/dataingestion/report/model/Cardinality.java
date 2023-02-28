package gov.cdc.dataingestion.report.model;

public enum Cardinality {
    /**
     * Zero or one.
     */
    ZERO_OR_ONE,
    /**
     * one.
     */
    ONE;

    /**
     * ZERO is not a value, just remove the element to represent this concept
     * Other values including conditionals in the future.
     * @return formatted string.
     */
    String toFormatted() {
        if (this == ZERO_OR_ONE) {
            return "[0..1]";
        } else if (this == ONE) {
            return "[1..1]";
        }
        return "";
    }
}
