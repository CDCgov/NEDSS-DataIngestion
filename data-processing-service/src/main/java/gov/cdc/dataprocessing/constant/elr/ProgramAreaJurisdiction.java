package gov.cdc.dataprocessing.constant.elr;

public class ProgramAreaJurisdiction {
    /**
     * Constant for Shared = true
     */
    public static final String SHAREDISTRUE = "T";

    /**
     * Constant for Shared = false
     */
    public static final String SHAREDISFALSE = "F";

    /**
     * Constant used to denote ALL jurisdictions.
     */
    public static final String ALL_JURISDICTIONS = "ALL";

    /**
     * Constant used to denote ANY jurisdiction.  Used when it doesn't matter what
     * jurisdiction applies.
     */
    public static final String ANY_JURISDICTION = "ANY";

    /**
     * Constant used to denote ANY program area.  Used when it doesn't matter what
     * program area applies.
     */
    public static final String ANY_PROGRAM_AREA = "ANY";

    /**
     * Constant used to denote program area set to NONE.  Used when program area
     * isn't to be assigned and derivation should be attempted.
     */
    public static final String PROGRAM_AREA_NONE = "NONE";

    /**
     * Constant used to denote jurisdiction set to NONE.  Used when jurisdiction
     * isn't to be assigned and derivation should be attempted.
     */
    public static final String JURISDICTION_NONE = "NONE";
}
