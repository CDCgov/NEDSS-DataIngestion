package gov.cdc.dataprocessing.model.dto;

import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.person.PersonDto;
import gov.cdc.dataprocessing.model.dto.person.PersonEthnicGroupDto;
import gov.cdc.dataprocessing.model.dto.person.PersonNameDto;
import gov.cdc.dataprocessing.model.dto.phc.CTContactSummaryDto;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class mixTest {
    @Test
    void  testParticipation() {
        ParticipationDto entity = new ParticipationDto();
        entity.setSubjectEntityClassCd("TEST");
        assertNotNull(entity.getSubjectEntityClassCd());
    }

    @Test
    void  testPerson() {
        PersonDto entity = new PersonDto();
        entity.setBirthOrderNbrStr("TEST");
        entity.setRecordStatusTime(null);
        entity.setAdultsInHouseNbrStr("TEST");
        entity.setChildrenInHouseNbrStr("TEST");
        assertNotNull(entity.getBirthOrderNbrStr());
        assertNull(entity.getRecordStatusTime());
        assertNotNull(entity.getAdultsInHouseNbrStr());
        assertNotNull(entity.getChildrenInHouseNbrStr());
        assertNotNull(entity.getSuperclass());
    }

    @Test
    void testPersonEth() {
        PersonEthnicGroupDto entity = new PersonEthnicGroupDto();
        entity.setAddTime(null);
        entity.setLastChgTime(null);
        entity.setRecordStatusTime(null);
        assertNull(entity.getAddTime());
        assertNull(entity.getLastChgTime());
        assertNull(entity.getRecordStatusTime());
    }

    @Test
    void testPersonName() {
        PersonNameDto entity = new PersonNameDto();
        entity.setNmSuffixCd("TEST");
        entity.setRecordStatusTime(null);
        entity.setVersionCtrlNbr(1);
        entity.setLocalId("TEST");
        assertNotNull(entity.getNmSuffixCd());
        assertNull(entity.getRecordStatusTime());
        assertNotNull(entity.getVersionCtrlNbr());
        assertNotNull(entity.getLocalId());
        assertNotNull(entity.getSuperclass());
        assertNull(entity.getUid());

    }

    @Test
    void testCaseMg() {
        CaseManagementDto entity = new CaseManagementDto();
        entity.setCaseManagementDTPopulated(true);
        entity.setLocalId("TEST");
        assertNotNull(entity.getLocalId());
        assertTrue(entity.isCaseManagementDTPopulated);
    }

    @Test
    void testConfirmMethod() {
        ConfirmationMethodDto entity = new ConfirmationMethodDto();
        entity.setPublicHealthCaseUid(10L);
        assertNotNull(entity.getPublicHealthCaseUid());

    }

    @Test
    void testContact() {
        CTContactSummaryDto entity = new CTContactSummaryDto();
        entity.setLastChgUserId(null);
        entity.setJurisdictionCd(null);
        entity.setLastChgTime(null);
        entity.setAddUserId(null);
        entity.setLastChgReasonCd(null);
        entity.setRecordStatusCd(null);
        entity.setRecordStatusTime(null);
        entity.setStatusCd(null);
        entity.setStatusTime(null);
        entity.setAddTime(null);
        entity.setProgramJurisdictionOid(null);
        entity.setSharedInd(null);

        assertNull(entity.getLastChgUserId());
        assertNull(entity.getJurisdictionCd());
        assertNull(entity.getLastChgTime());
        assertNull(entity.getAddUserId());
        assertNull(entity.getLastChgReasonCd());
        assertNull(entity.getRecordStatusCd());
        assertNull(entity.getRecordStatusTime());
        assertNull(entity.getStatusCd());
        assertNull(entity.getStatusTime());
        assertNull(entity.getAddTime());
        assertNull(entity.getProgramJurisdictionOid());
        assertNull(entity.getSharedInd());
    }

    @Test
    void testPhc() {
        PublicHealthCaseDto entity = new PublicHealthCaseDto();
        entity.setPamCase(true);
        entity.setPageCase(true);
        entity.setAddUserName("TEST");
        entity.setLastChgUserName("TEST");
        entity.setCurrentInvestigatorUid(10L);
        entity.setCurrentPatientUid(10L);
        entity.setRptSentTime(null);
        entity.setSummaryCase(true);
        entity.setContactInvStatus("TEST");
        entity.setConfirmationMethodCd("TEST");
        entity.setConfirmationMethodTime(null);

        assertTrue(entity.isPamCase());
        assertTrue(entity.isPageCase());
        assertEquals("TEST", entity.getAddUserName());
        assertEquals("TEST", entity.getLastChgUserName());
        assertEquals(10L, entity.getCurrentInvestigatorUid());
        assertEquals(10L, entity.getCurrentPatientUid());
        assertNull(entity.getRptSentTime());
        assertTrue(entity.isSummaryCase());
        assertEquals("TEST", entity.getContactInvStatus());
        assertEquals("TEST", entity.getConfirmationMethodCd());
        assertNull(entity.getConfirmationMethodTime());
        assertNotNull(entity.getSuperclass());    }
}
