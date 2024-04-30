package gov.cdc.dataprocessing.model.container;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
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
