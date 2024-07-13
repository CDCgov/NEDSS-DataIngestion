package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.NbsDocumentContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.dto.dsm.DSMUpdateAlgorithmDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessCaseSummaryDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NBSDocumentDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NbsDocumentContainerTest {

    @Test
    void testGettersAndSetters() {
        NbsDocumentContainer container = new NbsDocumentContainer();

        NBSDocumentDto nbsDocumentDto = new NBSDocumentDto();
        EDXActivityLogDto edxActivityLogDto = new EDXActivityLogDto();
        ParticipationDto participationDto = new ParticipationDto();
        PersonContainer patientVO = new PersonContainer();
        DSMUpdateAlgorithmDto dsmUpdateAlgorithmDto = new DSMUpdateAlgorithmDto();
        Map<String, EDXEventProcessDto> edxEventProcessDtoMap = new HashMap<>();
        Map<String, EDXEventProcessCaseSummaryDto> edxEventProcessCaseSummaryDtoMap = new HashMap<>();

        container.setNbsDocumentDT(nbsDocumentDto);
        container.setEDXActivityLogDT(edxActivityLogDto);
        container.setParticipationDT(participationDto);
        container.setPatientVO(patientVO);
        container.setActRelColl(new ArrayList<>());
        container.setFromSecurityQueue(true);
        container.setIsExistingPatient(true);
        container.setIsMultiplePatFound(true);
        container.setConditionFound(true);
        container.setConditionName("conditionName");
        container.setAssociatedInv(true);
        container.setOriginalPHCRLocalId("originalPHCRLocalId");
        container.setEDXEventProcessDTMap(edxEventProcessDtoMap);
        container.setContactRecordDoc(true);
        container.setLabReportDoc(true);
        container.setCaseReportDoc(true);
        container.setMorbReportDoc(true);
        container.setOngoingCase(true);
        container.setAssoSummaryCaseList(new ArrayList<>());
        container.setSummaryCaseListWithInTimeFrame(new ArrayList<>());
        container.setDsmUpdateAlgorithmDT(dsmUpdateAlgorithmDto);
        container.setEDXEventProcessCaseSummaryDTMap(edxEventProcessCaseSummaryDtoMap);

        assertEquals(nbsDocumentDto, container.getNbsDocumentDT());
        assertEquals(edxActivityLogDto, container.getEDXActivityLogDT());
        assertEquals(participationDto, container.getParticipationDT());
        assertEquals(patientVO, container.getPatientVO());
        assertTrue(container.isFromSecurityQueue());
        assertTrue(container.getIsExistingPatient());
        assertTrue(container.getIsMultiplePatFound());
        assertTrue(container.isConditionFound());
        assertEquals("conditionName", container.getConditionName());
        assertTrue(container.isAssociatedInv());
        assertEquals("originalPHCRLocalId", container.getOriginalPHCRLocalId());
        assertEquals(edxEventProcessDtoMap, container.getEDXEventProcessDTMap());
        assertTrue(container.isContactRecordDoc());
        assertTrue(container.isLabReportDoc());
        assertTrue(container.isCaseReportDoc());
        assertTrue(container.isMorbReportDoc());
        assertTrue(container.isOngoingCase());
        assertEquals(dsmUpdateAlgorithmDto, container.getDsmUpdateAlgorithmDT());
        assertEquals(edxEventProcessCaseSummaryDtoMap, container.getEDXEventProcessCaseSummaryDTMap());
    }

    @Test
    void testDefaultValues() {
        NbsDocumentContainer container = new NbsDocumentContainer();

        assertFalse(container.isFromSecurityQueue());
        assertFalse(container.getIsExistingPatient());
        assertFalse(container.getIsMultiplePatFound());
        assertFalse(container.isConditionFound());
        assertFalse(container.isAssociatedInv());
        assertFalse(container.isContactRecordDoc());
        assertFalse(container.isLabReportDoc());
        assertFalse(container.isCaseReportDoc());
        assertFalse(container.isMorbReportDoc());
        assertTrue(container.isOngoingCase());
    }

    @Test
    void testGetIsTouched() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        container.setItTouched(true);
        assertTrue(container.getIsTouched());
    }

    @Test
    void testSetItTouched() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        container.setItTouched(true);
        assertTrue(container.getIsTouched());
    }

    @Test
    void testGetIsAssociated() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        container.setItAssociated(true);
        assertTrue(container.getIsAssociated());
    }

    @Test
    void testSetItAssociated() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        container.setItAssociated(true);
        assertTrue(container.getIsAssociated());
    }

    @Test
    void testGetObservationUid() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Long observationUid = 123L;
        container.setObservationUid(observationUid);
        assertEquals(observationUid, container.getObservationUid());
    }

    @Test
    void testSetObservationUid() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Long observationUid = 123L;
        container.setObservationUid(observationUid);
        assertEquals(observationUid, container.getObservationUid());
    }

    @Test
    void testGetActivityFromTime() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Timestamp activityFromTime = new Timestamp(System.currentTimeMillis());
        container.setActivityFromTime(activityFromTime);
        assertEquals(activityFromTime, container.getActivityFromTime());
    }

    @Test
    void testSetActivityFromTime() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Timestamp activityFromTime = new Timestamp(System.currentTimeMillis());
        container.setActivityFromTime(activityFromTime);
        assertEquals(activityFromTime, container.getActivityFromTime());
    }

    @Test
    void testGetLastChgUserId() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Long lastChgUserId = 123L;
        container.setLastChgUserId(lastChgUserId);
        assertEquals(lastChgUserId, container.getLastChgUserId());
    }

    @Test
    void testSetLastChgUserId() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Long lastChgUserId = 123L;
        container.setLastChgUserId(lastChgUserId);
        assertEquals(lastChgUserId, container.getLastChgUserId());
    }

    @Test
    void testGetJurisdictionCd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String jurisdictionCd = "jurisdictionCd";
        container.setJurisdictionCd(jurisdictionCd);
        assertEquals(jurisdictionCd, container.getJurisdictionCd());
    }

    @Test
    void testSetJurisdictionCd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String jurisdictionCd = "jurisdictionCd";
        container.setJurisdictionCd(jurisdictionCd);
        assertEquals(jurisdictionCd, container.getJurisdictionCd());
    }

    @Test
    void testGetProgAreaCd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String progAreaCd = "progAreaCd";
        container.setProgAreaCd(progAreaCd);
        assertEquals(progAreaCd, container.getProgAreaCd());
    }

    @Test
    void testSetProgAreaCd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String progAreaCd = "progAreaCd";
        container.setProgAreaCd(progAreaCd);
        assertEquals(progAreaCd, container.getProgAreaCd());
    }

    @Test
    void testGetLastChgTime() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        container.setLastChgTime(lastChgTime);
        assertEquals(lastChgTime, container.getLastChgTime());
    }

    @Test
    void testSetLastChgTime() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        container.setLastChgTime(lastChgTime);
        assertEquals(lastChgTime, container.getLastChgTime());
    }

    @Test
    void testGetLocalId() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String localId = "localId";
        container.setLocalId(localId);
        assertEquals(localId, container.getLocalId());
    }

    @Test
    void testSetLocalId() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String localId = "localId";
        container.setLocalId(localId);
        assertEquals(localId, container.getLocalId());
    }

    @Test
    void testGetAddUserId() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Long addUserId = 123L;
        container.setAddUserId(addUserId);
        assertEquals(addUserId, container.getAddUserId());
    }

    @Test
    void testSetAddUserId() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Long addUserId = 123L;
        container.setAddUserId(addUserId);
        assertEquals(addUserId, container.getAddUserId());
    }

    @Test
    void testGetLastChgReasonCd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String lastChgReasonCd = "lastChgReasonCd";
        container.setLastChgReasonCd(lastChgReasonCd);
        assertEquals(lastChgReasonCd, container.getLastChgReasonCd());
    }

    @Test
    void testSetLastChgReasonCd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String lastChgReasonCd = "lastChgReasonCd";
        container.setLastChgReasonCd(lastChgReasonCd);
        assertEquals(lastChgReasonCd, container.getLastChgReasonCd());
    }

    @Test
    void testGetRecordStatusCd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String recordStatusCd = "recordStatusCd";
        container.setRecordStatusCd(recordStatusCd);
        assertEquals(recordStatusCd, container.getRecordStatusCd());
    }

    @Test
    void testSetRecordStatusCd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String recordStatusCd = "recordStatusCd";
        container.setRecordStatusCd(recordStatusCd);
        assertEquals(recordStatusCd, container.getRecordStatusCd());
    }

    @Test
    void testGetRecordStatusTime() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        container.setRecordStatusTime(recordStatusTime);
        assertEquals(recordStatusTime, container.getRecordStatusTime());
    }

    @Test
    void testSetRecordStatusTime() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        container.setRecordStatusTime(recordStatusTime);
        assertEquals(recordStatusTime, container.getRecordStatusTime());
    }

    @Test
    void testGetStatusCd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String statusCd = "statusCd";
        container.setStatusCd(statusCd);
        assertEquals(statusCd, container.getStatusCd());
    }

    @Test
    void testSetStatusCd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String statusCd = "statusCd";
        container.setStatusCd(statusCd);
        assertEquals(statusCd, container.getStatusCd());
    }

    @Test
    void testGetStatusTime() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        container.setStatusTime(statusTime);
        assertEquals(statusTime, container.getStatusTime());
    }

    @Test
    void testSetStatusTime() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        container.setStatusTime(statusTime);
        assertEquals(statusTime, container.getStatusTime());
    }

    @Test
    void testGetSuperclass() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        assertEquals("gov.cdc.dataprocessing.model.container.base.BaseContainer", container.getSuperclass());
    }

    @Test
    void testGetUid() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Long uid = 123L;
        NBSDocumentDto nbsDocumentDto = new NBSDocumentDto();
        nbsDocumentDto.setNbsDocumentUid(uid);
        container.setNbsDocumentDT(nbsDocumentDto);
        assertNull(container.getUid());
    }

    @Test
    void testSetAddTime() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        container.setAddTime(addTime);
        assertEquals(addTime, container.getAddTime());
    }

    @Test
    void testGetAddTime() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        container.setAddTime(addTime);
        assertEquals(addTime, container.getAddTime());
    }

    @Test
    void testGetProgramJurisdictionOid() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Long programJurisdictionOid = 123L;
        container.setProgramJurisdictionOid(programJurisdictionOid);
        assertEquals(programJurisdictionOid, container.getProgramJurisdictionOid());
    }

    @Test
    void testSetProgramJurisdictionOid() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Long programJurisdictionOid = 123L;
        container.setProgramJurisdictionOid(programJurisdictionOid);
        assertEquals(programJurisdictionOid, container.getProgramJurisdictionOid());
    }

    @Test
    void testGetSharedInd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String sharedInd = "sharedInd";
        container.setSharedInd(sharedInd);
        assertEquals(sharedInd, container.getSharedInd());
    }

    @Test
    void testSetSharedInd() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        String sharedInd = "sharedInd";
        container.setSharedInd(sharedInd);
        assertEquals(sharedInd, container.getSharedInd());
    }

    @Test
    void testGetVersionCtrlNbr() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Integer versionCtrlNbr = 1;
        container.setVersionCtrlNbr(versionCtrlNbr);
        assertEquals(versionCtrlNbr, container.getVersionCtrlNbr());
    }

    @Test
    void testSetVersionCtrlNbr() {
        NbsDocumentContainer container = new NbsDocumentContainer();
        Integer versionCtrlNbr = 1;
        container.setVersionCtrlNbr(versionCtrlNbr);
        assertEquals(versionCtrlNbr, container.getVersionCtrlNbr());
    }
}
