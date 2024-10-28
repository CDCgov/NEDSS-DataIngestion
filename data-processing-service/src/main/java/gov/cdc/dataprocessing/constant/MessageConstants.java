package gov.cdc.dataprocessing.constant;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
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
public class MessageConstants {
    public static final String New="New";
    public static final String Read="Read";
    public static final String N = "N";
    public static final String R = "R";
    public static final String UPDATED="UPDATED";

    public static final String Delete ="Delete";

    public static final String NEW_PROVIDER ="New Provider";
    public static final String NEW_ASSIGNMENT="New assignment";
    public static final String LAB_REPORT ="Lab Report";
    public static final String NEW_CLUSTER_TO_YOUR_CASE ="New Cluster to your case";
    public static final String NEW_CLUSTER_TO_YOUR_CASE_KEY ="NEW_CLUSTER";
    public static final String INVESTIGATION_REOPENED ="Investigation Reopened";
    public static final String NEW_SECONDARY_ADDED ="New Secondary Added";
    public static final String NEW_SECONDARY_ADDED_KEY ="NEW_SECONDARY";
    public static final String UPDATED_AWAITING_LAB_RESULTS="Updated Awaiting Lab Results";
    public static final String REVIEW_NOTES_UPDATED ="Review Notes Updated";
    public static final String LAB_MORB_MESSAGE_QUEUE_TEXT="New Provider/Lab Report ";
    /**
     * Question identifiers for Message Queue
     */
    //public static final String SUPERVISOR_REVIEW_QUESTION_IDENTIFIER="NBS268";
    public static final String CASE_SUPERVISOR_REVIEW_COMMENTS_QUESTION_IDENTIFIER="NBS200";
    public static final String FIELD_SUPERVISOR_REVIEW_COMMENTS_QUESTION_IDENTIFIER="NBS268";

    /**
     *Log Message for the queue
     */
    public static final String FIELD_SUPERVISOR_REVIEW_COMMENT_MODIFIED="Field Supervisory Review/Comments Modified";
    public static final String PENDING_LAB_RESULT_UPDATED="Updated Pending Lab Result";
    public static final String NEW_PROVIDER_LAB_REPORT="New provider/lab report";
    public static final String CASE_SUPERVISORY_REVIEW_COMMENT_MODIFIED="Case Supervisory Review/Comments Modified";

    /**
     * Constant for Lab
     */
    public static final String RESULT_PENDING_CD="P";
    /**
     * Constants for LAB and Morb
     */
    public static final String REFERRAL_CODE_FOR_LAB="T1";
    public static final String REFERRAL_CODE_FOR_MORB="T2";


    public static final String DISPOSITION_SPECIFIED = "Disposition specified for all Contacts";
    public static final String DISPOSITION_SPECIFIED_KEY ="DISPOSITION_SPECIFIED";
}
