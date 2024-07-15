package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.Referral;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ReferralDtoTest {

    @Test
    void testGettersAndSetters() {
        ReferralDto dto = new ReferralDto();

        // Set values
        dto.setReferralUid(1L);
        dto.setActivityDurationAmt("ActivityDurationAmt");
        dto.setActivityDurationUnitCd("ActivityDurationUnitCd");
        dto.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setActivityToTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddReasonCd("AddReasonCd");
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setCd("Cd");
        dto.setCdDescTxt("CdDescTxt");
        dto.setConfidentialityCd("ConfidentialityCd");
        dto.setConfidentialityDescTxt("ConfidentialityDescTxt");
        dto.setEffectiveDurationAmt("EffectiveDurationAmt");
        dto.setEffectiveDurationUnitCd("EffectiveDurationUnitCd");
        dto.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgReasonCd("LastChgReasonCd");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(3L);
        dto.setLocalId("LocalId");
        dto.setReasonTxt("ReasonTxt");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setReferralDescTxt("ReferralDescTxt");
        dto.setRepeatNbr(4);
        dto.setStatusCd("StatusCd");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setTxt("Txt");
        dto.setUserAffiliationTxt("UserAffiliationTxt");
        dto.setProgramJurisdictionOid(5L);
        dto.setSharedInd("SharedInd");
        dto.setVersionCtrlNbr(6);
        dto.setProgAreaCd("ProgAreaCd");
        dto.setJurisdictionCd("JurisdictionCd");
        dto.setItDirty(false);
        dto.setItNew(true);
        dto.setItDelete(false);

        // Assert values
        assertEquals(1L, dto.getReferralUid());
        assertEquals("ActivityDurationAmt", dto.getActivityDurationAmt());
        assertEquals("ActivityDurationUnitCd", dto.getActivityDurationUnitCd());
        assertNotNull(dto.getActivityFromTime());
        assertNotNull(dto.getActivityToTime());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("ConfidentialityCd", dto.getConfidentialityCd());
        assertEquals("ConfidentialityDescTxt", dto.getConfidentialityDescTxt());
        assertEquals("EffectiveDurationAmt", dto.getEffectiveDurationAmt());
        assertEquals("EffectiveDurationUnitCd", dto.getEffectiveDurationUnitCd());
        assertNotNull(dto.getEffectiveFromTime());
        assertNotNull(dto.getEffectiveToTime());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("ReasonTxt", dto.getReasonTxt());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals("ReferralDescTxt", dto.getReferralDescTxt());
        assertEquals(4, dto.getRepeatNbr());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals("Txt", dto.getTxt());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals(5L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(6, dto.getVersionCtrlNbr());
        assertEquals("ProgAreaCd", dto.getProgAreaCd());
        assertEquals("JurisdictionCd", dto.getJurisdictionCd());
        assertFalse(dto.isItDirty());
        assertTrue(dto.isItNew());
        assertFalse(dto.isItDelete());
    }

    @Test
    void testSpecialConstructor() {
        Referral referral = new Referral();
        referral.setReferralUid(1L);
        referral.setActivityDurationAmt("ActivityDurationAmt");
        referral.setActivityDurationUnitCd("ActivityDurationUnitCd");
        referral.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        referral.setActivityToTime(new Timestamp(System.currentTimeMillis()));
        referral.setAddReasonCd("AddReasonCd");
        referral.setAddTime(new Timestamp(System.currentTimeMillis()));
        referral.setAddUserId(2L);
        referral.setCd("Cd");
        referral.setCdDescTxt("CdDescTxt");
        referral.setConfidentialityCd("ConfidentialityCd");
        referral.setConfidentialityDescTxt("ConfidentialityDescTxt");
        referral.setEffectiveDurationAmt("EffectiveDurationAmt");
        referral.setEffectiveDurationUnitCd("EffectiveDurationUnitCd");
        referral.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        referral.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        referral.setLastChgReasonCd("LastChgReasonCd");
        referral.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        referral.setLastChgUserId(3L);
        referral.setLocalId("LocalId");
        referral.setReasonTxt("ReasonTxt");
        referral.setRecordStatusCd("RecordStatusCd");
        referral.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        referral.setReferralDescTxt("ReferralDescTxt");
        referral.setRepeatNbr(4);
        referral.setStatusCd("StatusCd");
        referral.setStatusTime(new Timestamp(System.currentTimeMillis()));
        referral.setTxt("Txt");
        referral.setUserAffiliationTxt("UserAffiliationTxt");
        referral.setProgramJurisdictionOid(5L);
        referral.setSharedInd("SharedInd");
        referral.setVersionCtrlNbr(6);

        ReferralDto dto = new ReferralDto(referral);

        // Assert values
        assertEquals(1L, dto.getReferralUid());
        assertEquals("ActivityDurationAmt", dto.getActivityDurationAmt());
        assertEquals("ActivityDurationUnitCd", dto.getActivityDurationUnitCd());
        assertNotNull(dto.getActivityFromTime());
        assertNotNull(dto.getActivityToTime());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("ConfidentialityCd", dto.getConfidentialityCd());
        assertEquals("ConfidentialityDescTxt", dto.getConfidentialityDescTxt());
        assertEquals("EffectiveDurationAmt", dto.getEffectiveDurationAmt());
        assertEquals("EffectiveDurationUnitCd", dto.getEffectiveDurationUnitCd());
        assertNotNull(dto.getEffectiveFromTime());
        assertNotNull(dto.getEffectiveToTime());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("ReasonTxt", dto.getReasonTxt());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals("ReferralDescTxt", dto.getReferralDescTxt());
        assertEquals(4, dto.getRepeatNbr());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals("Txt", dto.getTxt());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals(5L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(6, dto.getVersionCtrlNbr());
    }

    @Test
    void testOverriddenMethods() {
        ReferralDto dto = new ReferralDto();

        // Test overridden methods
        assertEquals(dto.getReferralUid(), dto.getUid());
        assertNull(dto.getSuperclass());
        assertNull(dto.getLastChgUserId());
        assertNull(dto.getJurisdictionCd());
        assertNull(dto.getProgAreaCd());
        assertNull(dto.getLastChgTime());
        assertNull(dto.getLocalId());
        assertNull(dto.getAddUserId());
        assertNull(dto.getLastChgReasonCd());
        assertNull(dto.getRecordStatusCd());
        assertNull(dto.getRecordStatusTime());
        assertNull(dto.getStatusCd());
        assertNull(dto.getStatusTime());
        assertNull(dto.getUid());
        assertNull(dto.getAddTime());
        assertNull(dto.getProgramJurisdictionOid());
        assertNull(dto.getSharedInd());
        assertNull(dto.getVersionCtrlNbr());
    }
}
