package gov.cdc.dataprocessing.constant.elr;

public class EdxELRConstant {
    public static final String ELR_DOC_TYPE_CD = "11648804";
    public static final String ELR_ACTIVE="ACTIVE";
    public static final String ELR_ACTIVE_CD="A";
    public static final String ELR_ROLE_REASON="because";

    public static final Long ELR_NBS_DOC_META_UID= 1005L;
    public static final String ELR_SENDING_FACILITY_CD="SF";
    public static final String ELR_SENDING_FACILITY_DESC="Sending Facility";
    public static final String ELR_SENDING_HCFAC="HCFAC";
    public static final String ELR_SENDING_LAB_CD="LAB";
    public static final String ELR_LABORATORY_DESC="Laboratory";
    public static final String ELR_STANDARD_INDUSTRY_DESC_TXT ="Medical Laboratory";
    public static final String ELR_FACILITY_CD="FI";
    public static final String ELR_FACILITY_DESC="Facility Identifier";
    public static final String ELR_OBS="OBS";
    public static final String ELR_ORG = "ORG";
    public static final String ELR_AUTHOR_CD="AUT";
    public static final String ELR_AUTHOR_DESC="Author";
    public static final String ELR_ELECTRONIC_IND="Y";
    public static final String ELR_LEGAL_NAME="L";

    public static final String ELR_DEFAULT_CLIA="DEFAULT";
    public static final String ELR_CLIA_CD="CLIA";
    public static final String ELR_CLIA_DESC="Clinical Laboratory Improvement Amendment";


    public static final String ELR_MASTER_LOG_ID_1="1";//"Jurisdiction and/or Program Area could not be derived.  The Lab Report is logged in Documents Requiring Security Assignment queue.";
    public static final String ELR_MASTER_LOG_ID_2="2";//	"A matching algorithm was not found.  The Lab Report is logged in Documents Requiring Review queue.";
    public static final String ELR_MASTER_LOG_ID_3="3";//	"Successfully Create Investigation";
    public static final String ELR_MASTER_LOG_ID_4="4";//"Error creating investigation.  See Activity Details.";
    public static final String ELR_MASTER_LOG_ID_5="5";//"Missing fields required to create investigation.";
    public static final String ELR_MASTER_LOG_ID_6="6";//"Successfully Create Notification";
    public static final String ELR_MASTER_LOG_ID_7="7";//"Error creating notification.  See Activity Details.";
    public static final String ELR_MASTER_LOG_ID_8="8";//"Missing fields required to create notification.";
    public static final String ELR_MASTER_LOG_ID_9="9";//"Error creating investigation.  See Activity Details.";
    public static final String ELR_MASTER_LOG_ID_10="10";//"Error creating notification.  See Activity Details.";
    public static final String ELR_MASTER_LOG_ID_11="11";//"Successfully Mark Lab as Reviewed";
    public static final String ELR_MASTER_LOG_ID_12="12";//"Error marking lab as reviewed.  See Activity Details.";
    public static final String ELR_MASTER_LOG_ID_13="13";//"Error creating lab.  See Activity Details.";
    public static final String ELR_MASTER_LOG_ID_14="14";//"Error updating Lab.  See Activity Details.";
    public static final String ELR_MASTER_LOG_ID_15="15";//"Lab updated successfully and logged in Documents Requiring Review queue";
    public static final String ELR_MASTER_LOG_ID_16="16";//"Unexpected exception.";
    public static final String ELR_MASTER_LOG_ID_17="17";//"Error creating lab.  See Activity Details.";
    public static final String ELR_MASTER_LOG_ID_18="18";//"String or binary data would be truncated.";
    public static final String ELR_MASTER_LOG_ID_19="19";//"Create Lab Blank Identifier";
    public static final String ELR_MASTER_LOG_ID_20="20";//"Invalid date. See Activity Details.";
    public static final String ELR_MASTER_LOG_ID_21="21";//"Successfully Mark Lab as Reviewed and associated to existing investigation";
    public static final String ELR_MASTER_LOG_ID_22="22";//"Lab updated successfully and logged in Documents Requiring Security Assignment queue";

    public static final String MULTIPLE_SUBJECT="Multiple Patients were included in the ELR.";
    public static final String NO_SUBJECT="The Patient segment is missing from the ELR.";


}
