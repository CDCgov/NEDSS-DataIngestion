package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.phc.ReferralDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.Referral;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ReferralTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        Referral referral = new Referral();

        // Assert
        assertNull(referral.getReferralUid());
        assertNull(referral.getActivityDurationAmt());
        assertNull(referral.getActivityDurationUnitCd());
        assertNull(referral.getActivityFromTime());
        assertNull(referral.getActivityToTime());
        assertNull(referral.getAddReasonCd());
        assertNull(referral.getAddTime());
        assertNull(referral.getAddUserId());
        assertNull(referral.getCd());
        assertNull(referral.getCdDescTxt());
        assertNull(referral.getConfidentialityCd());
        assertNull(referral.getConfidentialityDescTxt());
        assertNull(referral.getEffectiveDurationAmt());
        assertNull(referral.getEffectiveDurationUnitCd());
        assertNull(referral.getEffectiveFromTime());
        assertNull(referral.getEffectiveToTime());
        assertNull(referral.getLastChgReasonCd());
        assertNull(referral.getLastChgTime());
        assertNull(referral.getLastChgUserId());
        assertNull(referral.getLocalId());
        assertNull(referral.getReasonTxt());
        assertNull(referral.getRecordStatusCd());
        assertNull(referral.getRecordStatusTime());
        assertNull(referral.getReferralDescTxt());
        assertNull(referral.getRepeatNbr());
        assertNull(referral.getStatusCd());
        assertNull(referral.getStatusTime());
        assertNull(referral.getTxt());
        assertNull(referral.getUserAffiliationTxt());
        assertNull(referral.getProgramJurisdictionOid());
        assertNull(referral.getSharedInd());
        assertNull(referral.getVersionCtrlNbr());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long referralUid = 1L;
        String activityDurationAmt = "durationAmt";
        String activityDurationUnitCd = "durationUnitCd";
        Timestamp activityFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp activityToTime = new Timestamp(System.currentTimeMillis());
        String addReasonCd = "reasonCd";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String cd = "cd";
        String cdDescTxt = "cdDesc";
        String confidentialityCd = "confidentialityCd";
        String confidentialityDescTxt = "confidentialityDesc";
        String effectiveDurationAmt = "effectiveDurationAmt";
        String effectiveDurationUnitCd = "effectiveDurationUnitCd";
        Timestamp effectiveFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp effectiveToTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "chgReasonCd";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "localId";
        String reasonTxt = "reasonTxt";
        String recordStatusCd = "statusCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String referralDescTxt = "referralDescTxt";
        Integer repeatNbr = 1;
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String txt = "txt";
        String userAffiliationTxt = "affiliation";
        Long programJurisdictionOid = 4L;
        String sharedInd = "sharedInd";
        Integer versionCtrlNbr = 1;

        ReferralDto referralDto = new ReferralDto();
        referralDto.setReferralUid(referralUid);
        referralDto.setActivityDurationAmt(activityDurationAmt);
        referralDto.setActivityDurationUnitCd(activityDurationUnitCd);
        referralDto.setActivityFromTime(activityFromTime);
        referralDto.setActivityToTime(activityToTime);
        referralDto.setAddReasonCd(addReasonCd);
        referralDto.setAddTime(addTime);
        referralDto.setAddUserId(addUserId);
        referralDto.setCd(cd);
        referralDto.setCdDescTxt(cdDescTxt);
        referralDto.setConfidentialityCd(confidentialityCd);
        referralDto.setConfidentialityDescTxt(confidentialityDescTxt);
        referralDto.setEffectiveDurationAmt(effectiveDurationAmt);
        referralDto.setEffectiveDurationUnitCd(effectiveDurationUnitCd);
        referralDto.setEffectiveFromTime(effectiveFromTime);
        referralDto.setEffectiveToTime(effectiveToTime);
        referralDto.setLastChgReasonCd(lastChgReasonCd);
        referralDto.setLastChgTime(lastChgTime);
        referralDto.setLastChgUserId(lastChgUserId);
        referralDto.setLocalId(localId);
        referralDto.setReasonTxt(reasonTxt);
        referralDto.setRecordStatusCd(recordStatusCd);
        referralDto.setRecordStatusTime(recordStatusTime);
        referralDto.setReferralDescTxt(referralDescTxt);
        referralDto.setRepeatNbr(repeatNbr);
        referralDto.setStatusCd(statusCd);
        referralDto.setStatusTime(statusTime);
        referralDto.setTxt(txt);
        referralDto.setUserAffiliationTxt(userAffiliationTxt);
        referralDto.setProgramJurisdictionOid(programJurisdictionOid);
        referralDto.setSharedInd(sharedInd);
        referralDto.setVersionCtrlNbr(versionCtrlNbr);

        // Act
        Referral referral = new Referral(referralDto);

        // Assert
        assertEquals(referralUid, referral.getReferralUid());
        assertEquals(activityDurationAmt, referral.getActivityDurationAmt());
        assertEquals(activityDurationUnitCd, referral.getActivityDurationUnitCd());
        assertEquals(activityFromTime, referral.getActivityFromTime());
        assertEquals(activityToTime, referral.getActivityToTime());
        assertEquals(addReasonCd, referral.getAddReasonCd());
        assertEquals(addTime, referral.getAddTime());
        assertEquals(addUserId, referral.getAddUserId());
        assertEquals(cd, referral.getCd());
        assertEquals(cdDescTxt, referral.getCdDescTxt());
        assertEquals(confidentialityCd, referral.getConfidentialityCd());
        assertEquals(confidentialityDescTxt, referral.getConfidentialityDescTxt());
        assertEquals(effectiveDurationAmt, referral.getEffectiveDurationAmt());
        assertEquals(effectiveDurationUnitCd, referral.getEffectiveDurationUnitCd());
        assertEquals(effectiveFromTime, referral.getEffectiveFromTime());
        assertEquals(effectiveToTime, referral.getEffectiveToTime());
        assertEquals(lastChgReasonCd, referral.getLastChgReasonCd());
        assertEquals(lastChgTime, referral.getLastChgTime());
        assertEquals(lastChgUserId, referral.getLastChgUserId());
        assertEquals(localId, referral.getLocalId());
        assertEquals(reasonTxt, referral.getReasonTxt());
        assertEquals(recordStatusCd, referral.getRecordStatusCd());
        assertEquals(recordStatusTime, referral.getRecordStatusTime());
        assertEquals(referralDescTxt, referral.getReferralDescTxt());
        assertEquals(repeatNbr, referral.getRepeatNbr());
        assertEquals(statusCd, referral.getStatusCd());
        assertEquals(statusTime, referral.getStatusTime());
        assertEquals(txt, referral.getTxt());
        assertEquals(userAffiliationTxt, referral.getUserAffiliationTxt());
        assertEquals(programJurisdictionOid, referral.getProgramJurisdictionOid());
        assertEquals(sharedInd, referral.getSharedInd());
        assertEquals(versionCtrlNbr, referral.getVersionCtrlNbr());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Referral referral = new Referral();

        Long referralUid = 1L;
        String activityDurationAmt = "durationAmt";
        String activityDurationUnitCd = "durationUnitCd";
        Timestamp activityFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp activityToTime = new Timestamp(System.currentTimeMillis());
        String addReasonCd = "reasonCd";
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        String cd = "cd";
        String cdDescTxt = "cdDesc";
        String confidentialityCd = "confidentialityCd";
        String confidentialityDescTxt = "confidentialityDesc";
        String effectiveDurationAmt = "effectiveDurationAmt";
        String effectiveDurationUnitCd = "effectiveDurationUnitCd";
        Timestamp effectiveFromTime = new Timestamp(System.currentTimeMillis());
        Timestamp effectiveToTime = new Timestamp(System.currentTimeMillis());
        String lastChgReasonCd = "chgReasonCd";
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        String localId = "localId";
        String reasonTxt = "reasonTxt";
        String recordStatusCd = "statusCd";
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        String referralDescTxt = "referralDescTxt";
        Integer repeatNbr = 1;
        String statusCd = "statusCd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String txt = "txt";
        String userAffiliationTxt = "affiliation";
        Long programJurisdictionOid = 4L;
        String sharedInd = "sharedInd";
        Integer versionCtrlNbr = 1;

        // Act
        referral.setReferralUid(referralUid);
        referral.setActivityDurationAmt(activityDurationAmt);
        referral.setActivityDurationUnitCd(activityDurationUnitCd);
        referral.setActivityFromTime(activityFromTime);
        referral.setActivityToTime(activityToTime);
        referral.setAddReasonCd(addReasonCd);
        referral.setAddTime(addTime);
        referral.setAddUserId(addUserId);
        referral.setCd(cd);
        referral.setCdDescTxt(cdDescTxt);
        referral.setConfidentialityCd(confidentialityCd);
        referral.setConfidentialityDescTxt(confidentialityDescTxt);
        referral.setEffectiveDurationAmt(effectiveDurationAmt);
        referral.setEffectiveDurationUnitCd(effectiveDurationUnitCd);
        referral.setEffectiveFromTime(effectiveFromTime);
        referral.setEffectiveToTime(effectiveToTime);
        referral.setLastChgReasonCd(lastChgReasonCd);
        referral.setLastChgTime(lastChgTime);
        referral.setLastChgUserId(lastChgUserId);
        referral.setLocalId(localId);
        referral.setReasonTxt(reasonTxt);
        referral.setRecordStatusCd(recordStatusCd);
        referral.setRecordStatusTime(recordStatusTime);
        referral.setReferralDescTxt(referralDescTxt);
        referral.setRepeatNbr(repeatNbr);
        referral.setStatusCd(statusCd);
        referral.setStatusTime(statusTime);
        referral.setTxt(txt);
        referral.setUserAffiliationTxt(userAffiliationTxt);
        referral.setProgramJurisdictionOid(programJurisdictionOid);
        referral.setSharedInd(sharedInd);
        referral.setVersionCtrlNbr(versionCtrlNbr);

        // Assert
        assertEquals(referralUid, referral.getReferralUid());
        assertEquals(activityDurationAmt, referral.getActivityDurationAmt());
        assertEquals(activityDurationUnitCd, referral.getActivityDurationUnitCd());
        assertEquals(activityFromTime, referral.getActivityFromTime());
        assertEquals(activityToTime, referral.getActivityToTime());
        assertEquals(addReasonCd, referral.getAddReasonCd());
        assertEquals(addTime, referral.getAddTime());
        assertEquals(addUserId, referral.getAddUserId());
        assertEquals(cd, referral.getCd());
        assertEquals(cdDescTxt, referral.getCdDescTxt());
        assertEquals(confidentialityCd, referral.getConfidentialityCd());
        assertEquals(confidentialityDescTxt, referral.getConfidentialityDescTxt());
        assertEquals(effectiveDurationAmt, referral.getEffectiveDurationAmt());
        assertEquals(effectiveDurationUnitCd, referral.getEffectiveDurationUnitCd());
        assertEquals(effectiveFromTime, referral.getEffectiveFromTime());
        assertEquals(effectiveToTime, referral.getEffectiveToTime());
        assertEquals(lastChgReasonCd, referral.getLastChgReasonCd());
        assertEquals(lastChgTime, referral.getLastChgTime());
        assertEquals(lastChgUserId, referral.getLastChgUserId());
        assertEquals(localId, referral.getLocalId());
        assertEquals(reasonTxt, referral.getReasonTxt());
        assertEquals(recordStatusCd, referral.getRecordStatusCd());
        assertEquals(recordStatusTime, referral.getRecordStatusTime());
        assertEquals(referralDescTxt, referral.getReferralDescTxt());
        assertEquals(repeatNbr, referral.getRepeatNbr());
        assertEquals(statusCd, referral.getStatusCd());
        assertEquals(statusTime, referral.getStatusTime());
        assertEquals(txt, referral.getTxt());
        assertEquals(userAffiliationTxt, referral.getUserAffiliationTxt());
        assertEquals(programJurisdictionOid, referral.getProgramJurisdictionOid());
        assertEquals(sharedInd, referral.getSharedInd());
        assertEquals(versionCtrlNbr, referral.getVersionCtrlNbr());
    }
}
