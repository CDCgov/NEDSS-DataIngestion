package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.phc.ClinicalDocumentDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.ClinicalDocument;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ClinicalDocumentTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        ClinicalDocument clinicalDocument = new ClinicalDocument();

        // Assert
        assertNull(clinicalDocument.getClinicalDocumentUid());
        assertNull(clinicalDocument.getActivityDurationAmt());
        assertNull(clinicalDocument.getActivityDurationUnitCd());
        assertNull(clinicalDocument.getActivityFromTime());
        assertNull(clinicalDocument.getActivityToTime());
        assertNull(clinicalDocument.getAddReasonCd());
        assertNull(clinicalDocument.getAddTime());
        assertNull(clinicalDocument.getAddUserId());
        assertNull(clinicalDocument.getCd());
        assertNull(clinicalDocument.getCdDescTxt());
        assertNull(clinicalDocument.getConfidentialityCd());
        assertNull(clinicalDocument.getConfidentialityDescTxt());
        assertNull(clinicalDocument.getCopyFromTime());
        assertNull(clinicalDocument.getCopyToTime());
        assertNull(clinicalDocument.getEffectiveDurationAmt());
        assertNull(clinicalDocument.getEffectiveDurationUnitCd());
        assertNull(clinicalDocument.getEffectiveFromTime());
        assertNull(clinicalDocument.getEffectiveToTime());
        assertNull(clinicalDocument.getLastChgReasonCd());
        assertNull(clinicalDocument.getLastChgTime());
        assertNull(clinicalDocument.getLastChgUserId());
        assertNull(clinicalDocument.getLocalId());
        assertNull(clinicalDocument.getPracticeSettingCd());
        assertNull(clinicalDocument.getPracticeSettingDescTxt());
        assertNull(clinicalDocument.getRecordStatusCd());
        assertNull(clinicalDocument.getRecordStatusTime());
        assertNull(clinicalDocument.getStatusCd());
        assertNull(clinicalDocument.getStatusTime());
        assertNull(clinicalDocument.getTxt());
        assertNull(clinicalDocument.getUserAffiliationTxt());
        assertNull(clinicalDocument.getVersionNbr());
        assertNull(clinicalDocument.getProgramJurisdictionOid());
        assertNull(clinicalDocument.getSharedInd());
        assertNull(clinicalDocument.getVersionCtrlNbr());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long clinicalDocumentUid = 1L;
        String activityDurationAmt = "1h";
        String activityDurationUnitCd = "hour";
        Timestamp activityFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp activityToTime = new Timestamp(System.currentTimeMillis());
        String addReasonCd = "reason";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String cd = "code";
        String cdDescTxt = "description";
        String confidentialityCd = "confidential";
        String confidentialityDescTxt = "confidential description";
        Timestamp copyFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp copyToTime = new Timestamp(System.currentTimeMillis());
        String effectiveDurationAmt = "2h";
        String effectiveDurationUnitCd = "hour";
        Timestamp effectiveFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp effectiveToTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "last change reason";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "local123";
        String practiceSettingCd = "setting";
        String practiceSettingDescTxt = "setting description";
        String recordStatusCd = "active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String statusCd = "status";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String txt = "text";
        String userAffiliationTxt = "affiliation";
        Integer versionNbr = 1;
        Long programJurisdictionOid = 4L;
        String sharedInd = "Y";
        Integer versionCtrlNbr = 1;

        ClinicalDocumentDto dto = new ClinicalDocumentDto();
        dto.setClinicalDocumentUid(clinicalDocumentUid);
        dto.setActivityDurationAmt(activityDurationAmt);
        dto.setActivityDurationUnitCd(activityDurationUnitCd);
        dto.setActivityFromTime(activityFromTime);
        dto.setActivityToTime(activityToTime);
        dto.setAddReasonCd(addReasonCd);
        dto.setAddTime(addTime);
        dto.setAddUserId(addUserId);
        dto.setCd(cd);
        dto.setCdDescTxt(cdDescTxt);
        dto.setConfidentialityCd(confidentialityCd);
        dto.setConfidentialityDescTxt(confidentialityDescTxt);
        dto.setCopyFromTime(copyFromTime);
        dto.setCopyToTime(copyToTime);
        dto.setEffectiveDurationAmt(effectiveDurationAmt);
        dto.setEffectiveDurationUnitCd(effectiveDurationUnitCd);
        dto.setEffectiveFromTime(effectiveFromTime);
        dto.setEffectiveToTime(effectiveToTime);
        dto.setLastChgReasonCd(lastChgReasonCd);
        dto.setLastChgTime(lastChgTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setLocalId(localId);
        dto.setPracticeSettingCd(practiceSettingCd);
        dto.setPracticeSettingDescTxt(practiceSettingDescTxt);
        dto.setRecordStatusCd(recordStatusCd);
        dto.setRecordStatusTime(recordStatusTime);
        dto.setStatusCd(statusCd);
        dto.setStatusTime(statusTime);
        dto.setTxt(txt);
        dto.setUserAffiliationTxt(userAffiliationTxt);
        dto.setVersionNbr(versionNbr);
        dto.setProgramJurisdictionOid(programJurisdictionOid);
        dto.setSharedInd(sharedInd);
        dto.setVersionCtrlNbr(versionCtrlNbr);

        // Act
        ClinicalDocument clinicalDocument = new ClinicalDocument(dto);

        // Assert
        assertEquals(clinicalDocumentUid, clinicalDocument.getClinicalDocumentUid());
        assertEquals(activityDurationAmt, clinicalDocument.getActivityDurationAmt());
        assertEquals(activityDurationUnitCd, clinicalDocument.getActivityDurationUnitCd());
        assertEquals(activityFromTime, clinicalDocument.getActivityFromTime());
        assertEquals(activityToTime, clinicalDocument.getActivityToTime());
        assertEquals(addReasonCd, clinicalDocument.getAddReasonCd());
        assertEquals(addTime, clinicalDocument.getAddTime());
        assertEquals(addUserId, clinicalDocument.getAddUserId());
        assertEquals(cd, clinicalDocument.getCd());
        assertEquals(cdDescTxt, clinicalDocument.getCdDescTxt());
        assertEquals(confidentialityCd, clinicalDocument.getConfidentialityCd());
        assertEquals(confidentialityDescTxt, clinicalDocument.getConfidentialityDescTxt());
        assertEquals(copyFromTime, clinicalDocument.getCopyFromTime());
        assertEquals(copyToTime, clinicalDocument.getCopyToTime());
        assertEquals(effectiveDurationAmt, clinicalDocument.getEffectiveDurationAmt());
        assertEquals(effectiveDurationUnitCd, clinicalDocument.getEffectiveDurationUnitCd());
        assertEquals(effectiveFromTime, clinicalDocument.getEffectiveFromTime());
        assertEquals(effectiveToTime, clinicalDocument.getEffectiveToTime());
        assertEquals(lastChgReasonCd, clinicalDocument.getLastChgReasonCd());
        assertEquals(lastChgTime, clinicalDocument.getLastChgTime());
        assertEquals(lastChgUserId, clinicalDocument.getLastChgUserId());
        assertEquals(localId, clinicalDocument.getLocalId());
        assertEquals(practiceSettingCd, clinicalDocument.getPracticeSettingCd());
        assertEquals(practiceSettingDescTxt, clinicalDocument.getPracticeSettingDescTxt());
        assertEquals(recordStatusCd, clinicalDocument.getRecordStatusCd());
        assertEquals(recordStatusTime, clinicalDocument.getRecordStatusTime());
        assertEquals(statusCd, clinicalDocument.getStatusCd());
        assertEquals(statusTime, clinicalDocument.getStatusTime());
        assertEquals(txt, clinicalDocument.getTxt());
        assertEquals(userAffiliationTxt, clinicalDocument.getUserAffiliationTxt());
        assertEquals(versionNbr, clinicalDocument.getVersionNbr());
        assertEquals(programJurisdictionOid, clinicalDocument.getProgramJurisdictionOid());
        assertEquals(sharedInd, clinicalDocument.getSharedInd());
        assertEquals(versionCtrlNbr, clinicalDocument.getVersionCtrlNbr());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        ClinicalDocument clinicalDocument = new ClinicalDocument();

        Long clinicalDocumentUid = 1L;
        String activityDurationAmt = "1h";
        String activityDurationUnitCd = "hour";
        Timestamp activityFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp activityToTime = new Timestamp(System.currentTimeMillis());
        String addReasonCd = "reason";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String cd = "code";
        String cdDescTxt = "description";
        String confidentialityCd = "confidential";
        String confidentialityDescTxt = "confidential description";
        Timestamp copyFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp copyToTime = new Timestamp(System.currentTimeMillis());
        String effectiveDurationAmt = "2h";
        String effectiveDurationUnitCd = "hour";
        Timestamp effectiveFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp effectiveToTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "last change reason";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "local123";
        String practiceSettingCd = "setting";
        String practiceSettingDescTxt = "setting description";
        String recordStatusCd = "active";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String statusCd = "status";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String txt = "text";
        String userAffiliationTxt = "affiliation";
        Integer versionNbr = 1;
        Long programJurisdictionOid = 4L;
        String sharedInd = "Y";
        Integer versionCtrlNbr = 1;

        // Act
        clinicalDocument.setClinicalDocumentUid(clinicalDocumentUid);
        clinicalDocument.setActivityDurationAmt(activityDurationAmt);
        clinicalDocument.setActivityDurationUnitCd(activityDurationUnitCd);
        clinicalDocument.setActivityFromTime(activityFromTime);
        clinicalDocument.setActivityToTime(activityToTime);
        clinicalDocument.setAddReasonCd(addReasonCd);
        clinicalDocument.setAddTime(addTime);
        clinicalDocument.setAddUserId(addUserId);
        clinicalDocument.setCd(cd);
        clinicalDocument.setCdDescTxt(cdDescTxt);
        clinicalDocument.setConfidentialityCd(confidentialityCd);
        clinicalDocument.setConfidentialityDescTxt(confidentialityDescTxt);
        clinicalDocument.setCopyFromTime(copyFromTime);
        clinicalDocument.setCopyToTime(copyToTime);
        clinicalDocument.setEffectiveDurationAmt(effectiveDurationAmt);
        clinicalDocument.setEffectiveDurationUnitCd(effectiveDurationUnitCd);
        clinicalDocument.setEffectiveFromTime(effectiveFromTime);
        clinicalDocument.setEffectiveToTime(effectiveToTime);
        clinicalDocument.setLastChgReasonCd(lastChgReasonCd);
        clinicalDocument.setLastChgTime(lastChgTime);
        clinicalDocument.setLastChgUserId(lastChgUserId);
        clinicalDocument.setLocalId(localId);
        clinicalDocument.setPracticeSettingCd(practiceSettingCd);
        clinicalDocument.setPracticeSettingDescTxt(practiceSettingDescTxt);
        clinicalDocument.setRecordStatusCd(recordStatusCd);
        clinicalDocument.setRecordStatusTime(recordStatusTime);
        clinicalDocument.setStatusCd(statusCd);
        clinicalDocument.setStatusTime(statusTime);
        clinicalDocument.setTxt(txt);
        clinicalDocument.setUserAffiliationTxt(userAffiliationTxt);
        clinicalDocument.setVersionNbr(versionNbr);
        clinicalDocument.setProgramJurisdictionOid(programJurisdictionOid);
        clinicalDocument.setSharedInd(sharedInd);
        clinicalDocument.setVersionCtrlNbr(versionCtrlNbr);

        // Assert
        assertEquals(clinicalDocumentUid, clinicalDocument.getClinicalDocumentUid());
        assertEquals(activityDurationAmt, clinicalDocument.getActivityDurationAmt());
        assertEquals(activityDurationUnitCd, clinicalDocument.getActivityDurationUnitCd());
        assertEquals(activityFromTime, clinicalDocument.getActivityFromTime());
        assertEquals(activityToTime, clinicalDocument.getActivityToTime());
        assertEquals(addReasonCd, clinicalDocument.getAddReasonCd());
        assertEquals(addTime, clinicalDocument.getAddTime());
        assertEquals(addUserId, clinicalDocument.getAddUserId());
        assertEquals(cd, clinicalDocument.getCd());
        assertEquals(cdDescTxt, clinicalDocument.getCdDescTxt());
        assertEquals(confidentialityCd, clinicalDocument.getConfidentialityCd());
        assertEquals(confidentialityDescTxt, clinicalDocument.getConfidentialityDescTxt());
        assertEquals(copyFromTime, clinicalDocument.getCopyFromTime());
        assertEquals(copyToTime, clinicalDocument.getCopyToTime());
        assertEquals(effectiveDurationAmt, clinicalDocument.getEffectiveDurationAmt());
        assertEquals(effectiveDurationUnitCd, clinicalDocument.getEffectiveDurationUnitCd());
        assertEquals(effectiveFromTime, clinicalDocument.getEffectiveFromTime());
        assertEquals(effectiveToTime, clinicalDocument.getEffectiveToTime());
        assertEquals(lastChgReasonCd, clinicalDocument.getLastChgReasonCd());
        assertEquals(lastChgTime, clinicalDocument.getLastChgTime());
        assertEquals(lastChgUserId, clinicalDocument.getLastChgUserId());
        assertEquals(localId, clinicalDocument.getLocalId());
        assertEquals(practiceSettingCd, clinicalDocument.getPracticeSettingCd());
        assertEquals(practiceSettingDescTxt, clinicalDocument.getPracticeSettingDescTxt());
        assertEquals(recordStatusCd, clinicalDocument.getRecordStatusCd());
        assertEquals(recordStatusTime, clinicalDocument.getRecordStatusTime());
        assertEquals(statusCd, clinicalDocument.getStatusCd());
        assertEquals(statusTime, clinicalDocument.getStatusTime());
        assertEquals(txt, clinicalDocument.getTxt());
        assertEquals(userAffiliationTxt, clinicalDocument.getUserAffiliationTxt());
        assertEquals(versionNbr, clinicalDocument.getVersionNbr());
        assertEquals(programJurisdictionOid, clinicalDocument.getProgramJurisdictionOid());
        assertEquals(sharedInd, clinicalDocument.getSharedInd());
        assertEquals(versionCtrlNbr, clinicalDocument.getVersionCtrlNbr());
    }
}
