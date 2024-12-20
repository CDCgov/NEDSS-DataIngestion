package gov.cdc.dataprocessing.constant.elr;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class ELRConstant {
    public static final String PROGRAM_ASSIGN_1 = "PROGRAM_ASSIGN_1";
    public static final String PROGRAM_ASSIGN_2 = "PROGRAM_ASSIGN_2";
    //ODS
    public static final String ELR_OBS_STATUS_CD_COMPLETED = "D";
    public static final String ELR_OBS_STATUS_CD_SUPERCEDED = "T";
    public static final String ELR_SNOMED_CD = "SNM";
    public static final String TYPE = "LR";
    public static final String ELR_OBSERVATION_LOINC = "LN";

    public static final String JURISDICTION_HASHMAP_KEY = "Jurisdiction";
    public static final String PROGRAM_AREA_HASHMAP_KEY = "ProgramArea";
    //MsgIn
    public static final String ELR_OBS_STATUS_CD_FINAL = "F";
    public static final String ELR_OBS_STATUS_CD_CORRECTION = "C";

    public static final String ELECTRONIC_IND = "Y";
    public static final String ELR_LOG_PROCESS = "ELR_LOG_PROCESS";

    public static final String ELR_OBSERVATION_RESULT = "Result";
    public static final String ELR_OBSERVATION_ORDER = "Order";
    public static final String ELR_OBSERVATION_REFLEX_ORDER = "R_Order";
    public static final String ELR_OBSERVATION_REFLEX_RESULT = "R_Result";

}
