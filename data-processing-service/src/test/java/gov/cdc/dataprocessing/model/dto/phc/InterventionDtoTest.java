package gov.cdc.dataprocessing.model.dto.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.intervention.Intervention;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InterventionDtoTest {

    @Test
    void testGettersAndSetters() {
        InterventionDto dto = new InterventionDto();

        // Set values
        dto.setInterventionUid(1L);
        dto.setActivityDurationAmt("ActivityDurationAmt");
        dto.setActivityDurationUnitCd("ActivityDurationUnitCd");
        dto.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setActivityToTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddReasonCd("AddReasonCd");
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setCd("Cd");
        dto.setCdDescTxt("CdDescTxt");
        dto.setCdSystemCd("CdSystemCd");
        dto.setCdSystemDescTxt("CdSystemDescTxt");
        dto.setClassCd("ClassCd");
        dto.setConfidentialityCd("ConfidentialityCd");
        dto.setConfidentialityDescTxt("ConfidentialityDescTxt");
        dto.setEffectiveDurationAmt("EffectiveDurationAmt");
        dto.setEffectiveDurationUnitCd("EffectiveDurationUnitCd");
        dto.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        dto.setJurisdictionCd("JurisdictionCd");
        dto.setLastChgReasonCd("LastChgReasonCd");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(3L);
        dto.setLocalId("LocalId");
        dto.setMethodCd("MethodCd");
        dto.setMethodDescTxt("MethodDescTxt");
        dto.setProgAreaCd("ProgAreaCd");
        dto.setPriorityCd("PriorityCd");
        dto.setPriorityDescTxt("PriorityDescTxt");
        dto.setQtyAmt("QtyAmt");
        dto.setQtyUnitCd("QtyUnitCd");
        dto.setReasonCd("ReasonCd");
        dto.setReasonDescTxt("ReasonDescTxt");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setRepeatNbr(4);
        dto.setStatusCd("StatusCd");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setTargetSiteCd("TargetSiteCd");
        dto.setTargetSiteDescTxt("TargetSiteDescTxt");
        dto.setTxt("Txt");
        dto.setUserAffiliationTxt("UserAffiliationTxt");
        dto.setProgramJurisdictionOid(5L);
        dto.setSharedInd("SharedInd");
        dto.setVersionCtrlNbr(6);
        dto.setMaterialCd("MaterialCd");
        dto.setAgeAtVacc(7);
        dto.setAgeAtVaccUnitCd("AgeAtVaccUnitCd");
        dto.setVaccMfgrCd("VaccMfgrCd");
        dto.setMaterialLotNm("MaterialLotNm");
        dto.setMaterialExpirationTime(new Timestamp(System.currentTimeMillis()));
        dto.setVaccDoseNbr(8);
        dto.setVaccInfoSourceCd("VaccInfoSourceCd");
        dto.setElectronicInd("ElectronicInd");

        // Assert values
        assertEquals(1L, dto.getInterventionUid());
        assertEquals("ActivityDurationAmt", dto.getActivityDurationAmt());
        assertEquals("ActivityDurationUnitCd", dto.getActivityDurationUnitCd());
        assertNotNull(dto.getActivityFromTime());
        assertNotNull(dto.getActivityToTime());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("CdSystemCd", dto.getCdSystemCd());
        assertEquals("CdSystemDescTxt", dto.getCdSystemDescTxt());
        assertEquals("ClassCd", dto.getClassCd());
        assertEquals("ConfidentialityCd", dto.getConfidentialityCd());
        assertEquals("ConfidentialityDescTxt", dto.getConfidentialityDescTxt());
        assertEquals("EffectiveDurationAmt", dto.getEffectiveDurationAmt());
        assertEquals("EffectiveDurationUnitCd", dto.getEffectiveDurationUnitCd());
        assertNotNull(dto.getEffectiveFromTime());
        assertNotNull(dto.getEffectiveToTime());
        assertEquals("JurisdictionCd", dto.getJurisdictionCd());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("MethodCd", dto.getMethodCd());
        assertEquals("MethodDescTxt", dto.getMethodDescTxt());
        assertEquals("ProgAreaCd", dto.getProgAreaCd());
        assertEquals("PriorityCd", dto.getPriorityCd());
        assertEquals("PriorityDescTxt", dto.getPriorityDescTxt());
        assertEquals("QtyAmt", dto.getQtyAmt());
        assertEquals("QtyUnitCd", dto.getQtyUnitCd());
        assertEquals("ReasonCd", dto.getReasonCd());
        assertEquals("ReasonDescTxt", dto.getReasonDescTxt());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals(4, dto.getRepeatNbr());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals("TargetSiteCd", dto.getTargetSiteCd());
        assertEquals("TargetSiteDescTxt", dto.getTargetSiteDescTxt());
        assertEquals("Txt", dto.getTxt());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals(5L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(6, dto.getVersionCtrlNbr());
        assertEquals("MaterialCd", dto.getMaterialCd());
        assertEquals(7, dto.getAgeAtVacc());
        assertEquals("AgeAtVaccUnitCd", dto.getAgeAtVaccUnitCd());
        assertEquals("VaccMfgrCd", dto.getVaccMfgrCd());
        assertEquals("MaterialLotNm", dto.getMaterialLotNm());
        assertNotNull(dto.getMaterialExpirationTime());
        assertEquals(8, dto.getVaccDoseNbr());
        assertEquals("VaccInfoSourceCd", dto.getVaccInfoSourceCd());
        assertEquals("ElectronicInd", dto.getElectronicInd());
    }

    @Test
    void testSpecialConstructor() {
        Intervention domain = new Intervention();
        domain.setInterventionUid(1L);
        domain.setActivityDurationAmt("ActivityDurationAmt");
        domain.setActivityDurationUnitCd("ActivityDurationUnitCd");
        domain.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        domain.setActivityToTime(new Timestamp(System.currentTimeMillis()));
        domain.setAddReasonCd("AddReasonCd");
        domain.setAddTime(new Timestamp(System.currentTimeMillis()));
        domain.setAddUserId(2L);
        domain.setCd("Cd");
        domain.setCdDescTxt("CdDescTxt");
        domain.setCdSystemCd("CdSystemCd");
        domain.setCdSystemDescTxt("CdSystemDescTxt");
        domain.setClassCd("ClassCd");
        domain.setConfidentialityCd("ConfidentialityCd");
        domain.setConfidentialityDescTxt("ConfidentialityDescTxt");
        domain.setEffectiveDurationAmt("EffectiveDurationAmt");
        domain.setEffectiveDurationUnitCd("EffectiveDurationUnitCd");
        domain.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        domain.setEffectiveToTime(new Timestamp(System.currentTimeMillis()));
        domain.setJurisdictionCd("JurisdictionCd");
        domain.setLastChgReasonCd("LastChgReasonCd");
        domain.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        domain.setLastChgUserId(3L);
        domain.setLocalId("LocalId");
        domain.setMethodCd("MethodCd");
        domain.setMethodDescTxt("MethodDescTxt");
        domain.setProgAreaCd("ProgAreaCd");
        domain.setPriorityCd("PriorityCd");
        domain.setPriorityDescTxt("PriorityDescTxt");
        domain.setQtyAmt("QtyAmt");
        domain.setQtyUnitCd("QtyUnitCd");
        domain.setReasonCd("ReasonCd");
        domain.setReasonDescTxt("ReasonDescTxt");
        domain.setRecordStatusCd("RecordStatusCd");
        domain.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        domain.setRepeatNbr(4);
        domain.setStatusCd("StatusCd");
        domain.setStatusTime(new Timestamp(System.currentTimeMillis()));
        domain.setTargetSiteCd("TargetSiteCd");
        domain.setTargetSiteDescTxt("TargetSiteDescTxt");
        domain.setTxt("Txt");
        domain.setUserAffiliationTxt("UserAffiliationTxt");
        domain.setProgramJurisdictionOid(5L);
        domain.setSharedInd("SharedInd");
        domain.setVersionCtrlNbr(6);
        domain.setMaterialCd("MaterialCd");
        domain.setAgeAtVacc(7);
        domain.setAgeAtVaccUnitCd("AgeAtVaccUnitCd");
        domain.setVaccMfgrCd("VaccMfgrCd");
        domain.setMaterialLotNm("MaterialLotNm");
        domain.setMaterialExpirationTime(new Timestamp(System.currentTimeMillis()));
        domain.setVaccDoseNbr(8);
        domain.setVaccInfoSourceCd("VaccInfoSourceCd");
        domain.setElectronicInd("ElectronicInd");

        InterventionDto dto = new InterventionDto(domain);

        // Assert values
        assertEquals(1L, dto.getInterventionUid());
        assertEquals("ActivityDurationAmt", dto.getActivityDurationAmt());
        assertEquals("ActivityDurationUnitCd", dto.getActivityDurationUnitCd());
        assertNotNull(dto.getActivityFromTime());
        assertNotNull(dto.getActivityToTime());
        assertEquals("AddReasonCd", dto.getAddReasonCd());
        assertNotNull(dto.getAddTime());
        assertEquals(2L, dto.getAddUserId());
        assertEquals("Cd", dto.getCd());
        assertEquals("CdDescTxt", dto.getCdDescTxt());
        assertEquals("CdSystemCd", dto.getCdSystemCd());
        assertEquals("CdSystemDescTxt", dto.getCdSystemDescTxt());
        assertEquals("ClassCd", dto.getClassCd());
        assertEquals("ConfidentialityCd", dto.getConfidentialityCd());
        assertEquals("ConfidentialityDescTxt", dto.getConfidentialityDescTxt());
        assertEquals("EffectiveDurationAmt", dto.getEffectiveDurationAmt());
        assertEquals("EffectiveDurationUnitCd", dto.getEffectiveDurationUnitCd());
        assertNotNull(dto.getEffectiveFromTime());
        assertNotNull(dto.getEffectiveToTime());
        assertEquals("JurisdictionCd", dto.getJurisdictionCd());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertNotNull(dto.getLastChgTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("MethodCd", dto.getMethodCd());
        assertEquals("MethodDescTxt", dto.getMethodDescTxt());
        assertEquals("ProgAreaCd", dto.getProgAreaCd());
        assertEquals("PriorityCd", dto.getPriorityCd());
        assertEquals("PriorityDescTxt", dto.getPriorityDescTxt());
        assertEquals("QtyAmt", dto.getQtyAmt());
        assertEquals("QtyUnitCd", dto.getQtyUnitCd());
        assertEquals("ReasonCd", dto.getReasonCd());
        assertEquals("ReasonDescTxt", dto.getReasonDescTxt());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals(4, dto.getRepeatNbr());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals("TargetSiteCd", dto.getTargetSiteCd());
        assertEquals("TargetSiteDescTxt", dto.getTargetSiteDescTxt());
        assertEquals("Txt", dto.getTxt());
        assertEquals("UserAffiliationTxt", dto.getUserAffiliationTxt());
        assertEquals(5L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(6, dto.getVersionCtrlNbr());
        assertEquals("MaterialCd", dto.getMaterialCd());
        assertEquals(7, dto.getAgeAtVacc());
        assertEquals("AgeAtVaccUnitCd", dto.getAgeAtVaccUnitCd());
        assertEquals("VaccMfgrCd", dto.getVaccMfgrCd());
        assertEquals("MaterialLotNm", dto.getMaterialLotNm());
        assertNotNull(dto.getMaterialExpirationTime());
        assertEquals(8, dto.getVaccDoseNbr());
        assertEquals("VaccInfoSourceCd", dto.getVaccInfoSourceCd());
        assertEquals("ElectronicInd", dto.getElectronicInd());
    }

    @Test
    void testOverriddenMethods() {
        InterventionDto dto = new InterventionDto();

        // Test overridden methods
        assertEquals("Act", dto.getSuperclass());
        assertEquals(dto.getInterventionUid(), dto.getUid());
    }
}
