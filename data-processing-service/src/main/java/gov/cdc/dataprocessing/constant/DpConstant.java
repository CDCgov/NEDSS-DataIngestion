package gov.cdc.dataprocessing.constant;


public class DpConstant {
    private DpConstant(){

    }
    public static final String DP_QUEUED = "RTI_QUEUED";
    public static final String DP_SUCCESS_STEP_1 = "RTI_SUCCESS_STEP_1";
    public static final String DP_FAILURE_STEP_1 = "RTI_FAILURE_STEP_1";
    public static final String DP_SUCCESS_STEP_2 = "RTI_SUCCESS_STEP_2";
    public static final String DP_FAILURE_STEP_2 = "RTI_FAILURE_STEP_2";
    public static final String DP_SUCCESS_STEP_3 = "RTI_SUCCESS";
    public static final String DP_FAILURE_STEP_3 = "RTI_FAILURE_STEP_3";
    public static final String DP_COMPLETED_STEP_1 = "RTI_COMPLETED_WITHOUT_WDS";

    public static final String OPERATION_CREATE = "CREATE";
    public static final String OPERATION_UPDATE = "UPDATE";

    public static final String ERROR_DB_LOCKING = "DB Locking Error";
    public static final String ERROR_DB_DATA_INTEGERITY = "Data Integrity Error";
    public static final String STEP_1 = "STEP 1";
    public static final String STEP_2 = "STEP 2";
    public static final String DASH = " - ";
    public static final String EMPTY = "";
}
