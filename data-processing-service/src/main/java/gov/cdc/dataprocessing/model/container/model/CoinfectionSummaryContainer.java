package gov.cdc.dataprocessing.model.container.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class CoinfectionSummaryContainer implements Serializable, Cloneable{

    private static final long serialVersionUID = 1L;
    private Long publicHealthCaseUid;
    private String localId;
    private String coinfectionId;
    private String investigatorLastNm;
    private String intestigatorFirstNm;
    private String conditionCd;
    private String jurisdictionCd;
    private Long programJurisdictionOid;
    private String progAreaCd;
    private String investigationStatus;
    private String caseClassCd;
    private Timestamp createDate;
    private Timestamp updateDate;
    private Timestamp investigationStartDate;
    private Long patientRevisionUid;
    private String epiLinkId;
    private String fieldRecordNumber;
    private String patIntvStatusCd; //Patient Interview Status Cd
    private boolean associated;
    private String checkBoxId;
    private String disabled;

    private String processingDecisionCode;



}
