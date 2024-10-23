package gov.cdc.dataprocessing.constant.elr;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
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
