package gov.cdc.dataingestion.report.model;

/**
 * CSV field.
 */
public class CsvField  {
    /**
     * Name
     */
    private String name;

    /**
     * Format
     */
    private String format;

    /**
     * Constructor
     * @param name name
     * @param format format
     */
    public CsvField(final String name, final String format) {
        this.name = name;
        this.format = format;
    }

    /**
     * returns name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name
     * @param name name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * sets format
     * @param format format
     */
    public void setFormat(final String format) {
        this.format = format;
    }

    /**
     * returns format
     * @return format
     */
    public String getFormat() {
        return format;
    }
}
