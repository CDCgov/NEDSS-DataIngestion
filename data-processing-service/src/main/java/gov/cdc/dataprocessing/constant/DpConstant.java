package gov.cdc.dataprocessing.constant;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class DpConstant {
    private DpConstant(){

    }
    public static final String DP_QUEUED = "RTI_QUEUED";
    public static final String DP_SUCCESS_STEP_1 = "RTI_SUCCESS_STEP_1";
    public static final String DP_FAILURE_STEP_1 = "RTI_FAILURE_STEP_1";
    public static final String DP_SUCCESS_STEP_2 = "RTI_SUCCESS_STEP_2";
    public static final String DP_FAILURE_STEP_2 = "RTI_FAILURE_STEP_2";
    public static final String DP_SUCCESS_STEP_3 = "RTI_SUCCESS_STEP_3";
    public static final String DP_FAILURE_STEP_3 = "RTI_FAILURE_STEP_3";
    public static final String DP_COMPLETED_STEP_1 = "RTI_COMPLETED_WITHOUT_WDS";

}
