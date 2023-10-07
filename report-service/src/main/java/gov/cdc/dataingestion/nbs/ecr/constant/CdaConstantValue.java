package gov.cdc.dataingestion.nbs.ecr.constant;

public class CdaConstantValue {
    private CdaConstantValue() {

    }
    public static final String ROOT_ID = "2.16.840.1.113883.19";
    public static final String CODE_SYSTEM = "2.16.840.1.113883.6.1";
    public static final String CODE_SYSTEM_NAME = "LOINC";
    public static final String XML_NAME_SPACE_HOLDER = "urn:hl7-org:v3";
    public static final String NAME_SPACE_URL = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String STATE = "STATE";
    public static final String COUNTY = "COUNTY";
    public static final String COUNTRY = "COUNTRY";
    public static final String USESABLE_PERIOD = "useablePeriod";
    public static final String CODE = "123-4567";
    public static final String CLINICAL_CODE_SYSTEM = "Local-codesystem-oid";
    public static final String CLINICAL_CODE_SYSTEM_NAME = "LocalSystem";
    public static final String CODE_DISPLAY_NAME = "Interested Parties Section";
    public static final String CLINICAL_TITLE = "INTERESTED PARTIES SECTION";
    public static final String ACT_CODE_DISPLAY_NAME = "Interested Party";
    public static final String NOT_FOUND_VALUE = "NOT_FOUND";
    public static final String ID_ROOT = "2.16.840.999999";
    public static final String VALUE_NAME = "value";
    public static final String CHANGE = "CHANGED";
    public static final String ID_ARR_ROOT = "2.16.840.1.113883.4.6";
    public static final String EXTN_STR = ";extn=";
    public static final String NOT_MAPPED_VALUE = "NOT_MAPPED";
    public static final String VALUE_TAG = "<value></value>";
    public static final String CODE_NODE_MAPPED_VALUE = "CODE NOT MAPPED";

    public static final String CDATA = "[CDATA]";
    public static final String STUD = "<stud>stud</stud>";

    public static final String MAIL_TO = "mailto:";

    /** COL NAME */
    public static final String PAT_LOCAL_ID_CONST = "patLocalId";
    public static final String COL_QUES_GROUP_SEQ_NBR = "questionGroupSeqNbr";
    public static final String COL_ANS_GROUP_SEQ_NBR = "answerGroupSeqNbr";
    public static final String COL_DATA_TYPE = "dataType";
    public static final String COL_SEQ_NBR = "seqNbr";
    public static final String COL_ANS_TXT = "answerTxt";
    public static final String COL_ANS_CODE_SYSTEM_CD = "ansCodeSystemCd";
    public static final String COL_ANS_CODE_SYSTEM_DESC_TXT = "ansCodeSystemDescTxt";
    public static final String COL_ANS_DISPLAY_TXT = "ansDisplayTxt";
    public static final String COL_ANS_TO_CODE = "ansToCode";
    public static final String COL_ANS_TO_CODE_SYSTEM_CD = "ansToCodeSystemCd";
    public static final String COL_ANS_TO_CODE_SYSTEM_DESC_TXT = "ansToCodeSystemDescTxt";
    public static final String COL_ANS_TO_DISPLAY_NM = "ansToDisplayNm";
    public static final String COL_QUES_IDENTIFIER = "questionIdentifier";
    public static final String COL_QUES_CODE_SYSTEM_CD = "quesCodeSystemCd";
    public static final String COL_QUES_CODE_SYSTEM_DESC_TXT = "quesCodeSystemDescTxt";
    public static final String COL_QUES_DISPLAY_TXT = "quesDisplayTxt";


    /**DATA TYPE*/
    public static final String DATA_TYPE_CODE = "CODED";
    public static final String DATA_TYPE_NUMERIC = "NUMERIC";
}
