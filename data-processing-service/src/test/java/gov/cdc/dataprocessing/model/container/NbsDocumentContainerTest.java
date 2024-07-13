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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
