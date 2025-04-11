package gov.cdc.nbs.deduplication.algorithm.pass.model;

public enum MatchingAttribute {
    FIRST_NAME("FIRST_NAME"),
    LAST_NAME("LAST_NAME"),
    SUFFIX("SUFFIX"),
    BIRTHDATE("BIRTHDATE"),
    SEX("SEX"),
    RACE("RACE"),
    ADDRESS("ADDRESS"),
    CITY("CITY"),
    STATE("STATE"),
    ZIP("ZIP"),
    COUNTY("COUNTY"),
    TELECOM("TELECOM"),
    PHONE("PHONE"),
    EMAIL("EMAIL"),
    IDENTIFIER("IDENTIFIER"),
    ACCOUNT_NUMBER("IDENTIFIER:AN"),
    DRIVERS_LICENSE_NUMBER("IDENTIFIER:DL"),
    MEDICAID_NUMBER("IDENTIFIER:MA"),
    MEDICAL_RECORD_NUMBER("IDENTIFIER:MR"),
    MEDICARE_NUMBER("IDENTIFIER:MC"),
    NATIONAL_UNIQUE_INDIVIDUAL_IDENTIFIER("IDENTIFIER:NI"),
    PATIENT_EXTERNAL_IDENTIFIER("IDENTIFIER:PT"),
    PATIENT_INTERNAL_IDENTIFIER("IDENTIFIER:PI"),
    PERSON_NUMBER("IDENTIFIER:PN"),
    SOCIAL_SECURITY("IDENTIFIER:SS"),
    VISA_PASSPORT("IDENTIFIER:VS"),
    WIC_IDENTIFIER("IDENTIFIER:WC");

    private final String value;

    MatchingAttribute(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
