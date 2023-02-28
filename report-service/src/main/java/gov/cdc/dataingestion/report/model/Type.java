package gov.cdc.dataingestion.report.model;

/**
 * Type
 */
public enum Type {
    /**
     *  Text
     */
    TEXT,
    /**
     *  Text or blank.
     */
    TEXT_OR_BLANK, // Blank values are valid (not null)
    /**
     *  Number
     */
    NUMBER,
    /**
     *  Date
     */
    DATE,
    /**
     * Date time.
     */
    DATETIME,
    /**
     * Duration.
     */
    DURATION,
    /**
     * code
     * // .CODED with a HL7, SNOMED-CT, LONIC valueSet
     */
    CODE,
    /**
     * A table column value
     */
    TABLE,
    /**
     * Table or blank.
     */
    TABLE_OR_BLANK,
    /**
     * EI
     * A HL7 Entity Identifier (4 parts)
     */
    EI,
    /**
     * ISO Hierarchic Designator
     */
    HD,
    /**
     * Generic ID
     */
    ID,
    /**
     * CMS CLIA number (must follow CLIA format rules)
     */
    ID_CLIA,
    /**
     * id
     */
    ID_DLN,
    /**
     * id ssn
     */
    ID_SSN,

    ID_NPI,
    STREET,
    STREET_OR_BLANK,
    CITY,
    POSTAL_CODE,
    PERSON_NAME,
    TELEPHONE,
   EMAIL,
   BLANK
}
