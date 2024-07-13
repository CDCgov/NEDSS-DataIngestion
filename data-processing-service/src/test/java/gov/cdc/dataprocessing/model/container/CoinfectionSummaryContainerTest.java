package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.CoinfectionSummaryContainer;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class CoinfectionSummaryContainerTest {

    @Test
    void testGettersAndSetters() {
        CoinfectionSummaryContainer container = new CoinfectionSummaryContainer();

        Long publicHealthCaseUid = 12345L;
        String localId = "localId123";
        String coinfectionId = "coinfectionId456";
        String investigatorLastNm = "Doe";
        String investigatorFirstNm = "John";
        String conditionCd = "condition123";
        String jurisdictionCd = "jurisdiction456";
        Long programJurisdictionOid = 67890L;
        String progAreaCd = "progArea123";
        String investigationStatus = "Open";
        String caseClassCd = "Confirmed";
        Timestamp createDate = new Timestamp(System.currentTimeMillis());
        Timestamp updateDate = new Timestamp(System.currentTimeMillis());
        Timestamp investigationStartDate = new Timestamp(System.currentTimeMillis());
        Long patientRevisionUid = 98765L;
        String epiLinkId = "epiLink789";
        String fieldRecordNumber = "fieldRecord456";
        String patIntvStatusCd = "Interviewed";
        boolean associated = true;
        String checkBoxId = "checkBox123";
        String disabled = "false";
        String processingDecisionCode = "decisionCode";

        container.setPublicHealthCaseUid(publicHealthCaseUid);
        container.setLocalId(localId);
        container.setCoinfectionId(coinfectionId);
        container.setInvestigatorLastNm(investigatorLastNm);
        container.setIntestigatorFirstNm(investigatorFirstNm);
        container.setConditionCd(conditionCd);
        container.setJurisdictionCd(jurisdictionCd);
        container.setProgramJurisdictionOid(programJurisdictionOid);
        container.setProgAreaCd(progAreaCd);
        container.setInvestigationStatus(investigationStatus);
        container.setCaseClassCd(caseClassCd);
        container.setCreateDate(createDate);
        container.setUpdateDate(updateDate);
        container.setInvestigationStartDate(investigationStartDate);
        container.setPatientRevisionUid(patientRevisionUid);
        container.setEpiLinkId(epiLinkId);
        container.setFieldRecordNumber(fieldRecordNumber);
        container.setPatIntvStatusCd(patIntvStatusCd);
        container.setAssociated(associated);
        container.setCheckBoxId(checkBoxId);
        container.setDisabled(disabled);
        container.setProcessingDecisionCode(processingDecisionCode);

        assertEquals(publicHealthCaseUid, container.getPublicHealthCaseUid());
        assertEquals(localId, container.getLocalId());
        assertEquals(coinfectionId, container.getCoinfectionId());
        assertEquals(investigatorLastNm, container.getInvestigatorLastNm());
        assertEquals(investigatorFirstNm, container.getIntestigatorFirstNm());
        assertEquals(conditionCd, container.getConditionCd());
        assertEquals(jurisdictionCd, container.getJurisdictionCd());
        assertEquals(programJurisdictionOid, container.getProgramJurisdictionOid());
        assertEquals(progAreaCd, container.getProgAreaCd());
        assertEquals(investigationStatus, container.getInvestigationStatus());
        assertEquals(caseClassCd, container.getCaseClassCd());
        assertEquals(createDate, container.getCreateDate());
        assertEquals(updateDate, container.getUpdateDate());
        assertEquals(investigationStartDate, container.getInvestigationStartDate());
        assertEquals(patientRevisionUid, container.getPatientRevisionUid());
        assertEquals(epiLinkId, container.getEpiLinkId());
        assertEquals(fieldRecordNumber, container.getFieldRecordNumber());
        assertEquals(patIntvStatusCd, container.getPatIntvStatusCd());
        assertTrue(container.isAssociated());
        assertEquals(checkBoxId, container.getCheckBoxId());
        assertEquals(disabled, container.getDisabled());
        assertEquals(processingDecisionCode, container.getProcessingDecisionCode());
    }
}