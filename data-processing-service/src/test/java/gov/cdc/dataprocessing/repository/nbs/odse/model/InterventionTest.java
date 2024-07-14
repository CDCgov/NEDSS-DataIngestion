package gov.cdc.dataprocessing.repository.nbs.odse.model.intervention;

import gov.cdc.dataprocessing.model.dto.phc.InterventionDto;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InterventionTest {

    @Test
    void testInterventionDtoConstructor() {
        // Arrange
        InterventionDto dto = new InterventionDto();
        dto.setInterventionUid(1L);
        dto.setActivityDurationAmt("2 hours");
        dto.setActivityDurationUnitCd("hours");
        dto.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setActivityToTime(new Timestamp(System.currentTimeMillis() + 3600000));
        dto.setAddReasonCd("new");
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(100L);
        dto.setCd("CD001");
        dto.setCdDescTxt("Description");
        dto.setCdSystemCd("System001");
        dto.setCdSystemDescTxt("System Description");
        dto.setClassCd("Class001");
        dto.setConfidentialityCd("Confidential");
        dto.setConfidentialityDescTxt("Confidential Description");
        dto.setEffectiveDurationAmt("1 hour");
        dto.setEffectiveDurationUnitCd("hours");
        dto.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        dto.setEffectiveToTime(new Timestamp(System.currentTimeMillis() + 1800000));
        dto.setJurisdictionCd("Jurisdiction001");
        dto.setLastChgReasonCd("Updated");
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(200L);
        dto.setLocalId("Local001");
        dto.setMethodCd("Method001");
        dto.setMethodDescTxt("Method Description");
        dto.setProgAreaCd("ProgArea001");
        dto.setPriorityCd("High");
        dto.setPriorityDescTxt("High Priority");
        dto.setQtyAmt("10");
        dto.setQtyUnitCd("Units");
        dto.setReasonCd("Reason001");
        dto.setReasonDescTxt("Reason Description");
        dto.setRecordStatusCd("Active");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setRepeatNbr(1);
        dto.setStatusCd("Completed");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setTargetSiteCd("TargetSite001");
        dto.setTargetSiteDescTxt("Target Site Description");
        dto.setTxt("Text");
        dto.setUserAffiliationTxt("User Affiliation");
        dto.setProgramJurisdictionOid(300L);
        dto.setSharedInd("Y");
        dto.setVersionCtrlNbr(1);
        dto.setMaterialCd("Material001");
        dto.setAgeAtVacc(5);
        dto.setAgeAtVaccUnitCd("years");
        dto.setVaccMfgrCd("Manufacturer001");
        dto.setMaterialLotNm("Lot001");
        dto.setMaterialExpirationTime(new Timestamp(System.currentTimeMillis() + 7200000));
        dto.setVaccDoseNbr(2);
        dto.setVaccInfoSourceCd("Source001");
        dto.setElectronicInd("Y");

        // Act
        Intervention intervention = new Intervention(dto);

        // Assert
        assertEquals(dto.getInterventionUid(), intervention.getInterventionUid());
        assertEquals(dto.getActivityDurationAmt(), intervention.getActivityDurationAmt());
        assertEquals(dto.getActivityDurationUnitCd(), intervention.getActivityDurationUnitCd());
        assertEquals(dto.getActivityFromTime(), intervention.getActivityFromTime());
        assertEquals(dto.getActivityToTime(), intervention.getActivityToTime());
        assertEquals(dto.getAddReasonCd(), intervention.getAddReasonCd());
        assertEquals(dto.getAddTime(), intervention.getAddTime());
        assertEquals(dto.getAddUserId(), intervention.getAddUserId());
        assertEquals(dto.getCd(), intervention.getCd());
        assertEquals(dto.getCdDescTxt(), intervention.getCdDescTxt());
        assertEquals(dto.getCdSystemCd(), intervention.getCdSystemCd());
        assertEquals(dto.getCdSystemDescTxt(), intervention.getCdSystemDescTxt());
        assertEquals(dto.getClassCd(), intervention.getClassCd());
        assertEquals(dto.getConfidentialityCd(), intervention.getConfidentialityCd());
        assertEquals(dto.getConfidentialityDescTxt(), intervention.getConfidentialityDescTxt());
        assertEquals(dto.getEffectiveDurationAmt(), intervention.getEffectiveDurationAmt());
        assertEquals(dto.getEffectiveDurationUnitCd(), intervention.getEffectiveDurationUnitCd());
        assertEquals(dto.getEffectiveFromTime(), intervention.getEffectiveFromTime());
        assertEquals(dto.getEffectiveToTime(), intervention.getEffectiveToTime());
        assertEquals(dto.getJurisdictionCd(), intervention.getJurisdictionCd());
        assertEquals(dto.getLastChgReasonCd(), intervention.getLastChgReasonCd());
        assertEquals(dto.getLastChgTime(), intervention.getLastChgTime());
        assertEquals(dto.getLastChgUserId(), intervention.getLastChgUserId());
        assertEquals(dto.getLocalId(), intervention.getLocalId());
        assertEquals(dto.getMethodCd(), intervention.getMethodCd());
        assertEquals(dto.getMethodDescTxt(), intervention.getMethodDescTxt());
        assertEquals(dto.getProgAreaCd(), intervention.getProgAreaCd());
        assertEquals(dto.getPriorityCd(), intervention.getPriorityCd());
        assertEquals(dto.getPriorityDescTxt(), intervention.getPriorityDescTxt());
        assertEquals(dto.getQtyAmt(), intervention.getQtyAmt());
        assertEquals(dto.getQtyUnitCd(), intervention.getQtyUnitCd());
        assertEquals(dto.getReasonCd(), intervention.getReasonCd());
        assertEquals(dto.getReasonDescTxt(), intervention.getReasonDescTxt());
        assertEquals(dto.getRecordStatusCd(), intervention.getRecordStatusCd());
        assertEquals(dto.getRecordStatusTime(), intervention.getRecordStatusTime());
        assertEquals(dto.getRepeatNbr(), intervention.getRepeatNbr());
        assertEquals(dto.getStatusCd(), intervention.getStatusCd());
        assertEquals(dto.getStatusTime(), intervention.getStatusTime());
        assertEquals(dto.getTargetSiteCd(), intervention.getTargetSiteCd());
        assertEquals(dto.getTargetSiteDescTxt(), intervention.getTargetSiteDescTxt());
        assertEquals(dto.getTxt(), intervention.getTxt());
        assertEquals(dto.getUserAffiliationTxt(), intervention.getUserAffiliationTxt());
        assertEquals(dto.getProgramJurisdictionOid(), intervention.getProgramJurisdictionOid());
        assertEquals(dto.getSharedInd(), intervention.getSharedInd());
        assertEquals(dto.getVersionCtrlNbr(), intervention.getVersionCtrlNbr());
        assertEquals(dto.getMaterialCd(), intervention.getMaterialCd());
        assertEquals(dto.getAgeAtVacc(), intervention.getAgeAtVacc());
        assertEquals(dto.getAgeAtVaccUnitCd(), intervention.getAgeAtVaccUnitCd());
        assertEquals(dto.getVaccMfgrCd(), intervention.getVaccMfgrCd());
        assertEquals(dto.getMaterialLotNm(), intervention.getMaterialLotNm());
        assertEquals(dto.getMaterialExpirationTime(), intervention.getMaterialExpirationTime());
        assertEquals(dto.getVaccDoseNbr(), intervention.getVaccDoseNbr());
        assertEquals(dto.getVaccInfoSourceCd(), intervention.getVaccInfoSourceCd());
        assertEquals(dto.getElectronicInd(), intervention.getElectronicInd());
    }

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        Intervention intervention = new Intervention();

        // Assert
        assertNull(intervention.getInterventionUid());
        assertNull(intervention.getActivityDurationAmt());
        assertNull(intervention.getActivityDurationUnitCd());
        assertNull(intervention.getActivityFromTime());
        assertNull(intervention.getActivityToTime());
        assertNull(intervention.getAddReasonCd());
        assertNull(intervention.getAddTime());
        assertNull(intervention.getAddUserId());
        assertNull(intervention.getCd());
        assertNull(intervention.getCdDescTxt());
        assertNull(intervention.getCdSystemCd());
        assertNull(intervention.getCdSystemDescTxt());
        assertNull(intervention.getClassCd());
        assertNull(intervention.getConfidentialityCd());
        assertNull(intervention.getConfidentialityDescTxt());
        assertNull(intervention.getEffectiveDurationAmt());
        assertNull(intervention.getEffectiveDurationUnitCd());
        assertNull(intervention.getEffectiveFromTime());
        assertNull(intervention.getEffectiveToTime());
        assertNull(intervention.getJurisdictionCd());
        assertNull(intervention.getLastChgReasonCd());
        assertNull(intervention.getLastChgTime());
        assertNull(intervention.getLastChgUserId());
        assertNull(intervention.getLocalId());
        assertNull(intervention.getMethodCd());
        assertNull(intervention.getMethodDescTxt());
        assertNull(intervention.getProgAreaCd());
        assertNull(intervention.getPriorityCd());
        assertNull(intervention.getPriorityDescTxt());
        assertNull(intervention.getQtyAmt());
        assertNull(intervention.getQtyUnitCd());
        assertNull(intervention.getReasonCd());
        assertNull(intervention.getReasonDescTxt());
        assertNull(intervention.getRecordStatusCd());
        assertNull(intervention.getRecordStatusTime());
        assertNull(intervention.getRepeatNbr());
        assertNull(intervention.getStatusCd());
        assertNull(intervention.getStatusTime());
        assertNull(intervention.getTargetSiteCd());
        assertNull(intervention.getTargetSiteDescTxt());
        assertNull(intervention.getTxt());
        assertNull(intervention.getUserAffiliationTxt());
        assertNull(intervention.getProgramJurisdictionOid());
        assertNull(intervention.getSharedInd());
        assertNull(intervention.getVersionCtrlNbr());
        assertNull(intervention.getMaterialCd());
        assertNull(intervention.getAgeAtVacc());
        assertNull(intervention.getAgeAtVaccUnitCd());
        assertNull(intervention.getVaccMfgrCd());
        assertNull(intervention.getMaterialLotNm());
        assertNull(intervention.getMaterialExpirationTime());
        assertNull(intervention.getVaccDoseNbr());
        assertNull(intervention.getVaccInfoSourceCd());
        assertNull(intervention.getElectronicInd());
    }
}
