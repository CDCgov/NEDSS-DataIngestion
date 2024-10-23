package gov.cdc.dataprocessing.constant.elr;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
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
