package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.interfaces.InterviewContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsNoteDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.ExportReceivingFacilityDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PageActProxyContainerTest {

    @Test
    void testGettersAndSetters() {
        PageActProxyContainer container = new PageActProxyContainer();

        String pageProxyTypeCd = "INV";
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        InterviewContainer interviewContainer = new InterviewContainer();
        NotificationContainer notificationContainer = new NotificationContainer();
        InterventionContainer interventionContainer = new InterventionContainer();
        Long patientUid = 123L;
        String currentInvestigator = "Investigator A";
        String fieldSupervisor = "Supervisor B";
        String caseSupervisor = "Supervisor C";
        boolean isSTDProgramArea = true;
        Collection<PersonContainer> personContainerCollection = new ArrayList<>();
        BasePamContainer pageVO = new BasePamContainer();
        Collection<Object> vaccinationSummaryVOCollection = new ArrayList<>();
        Collection<Object> notificationSummaryVOCollection = new ArrayList<>();
        Collection<Object> treatmentSummaryVOCollection = new ArrayList<>();
        Collection<Object> labReportSummaryVOCollection = new ArrayList<>();
        Collection<Object> morbReportSummaryVOCollection = new ArrayList<>();
        Collection<ParticipationDto> participationDtoCollection = new ArrayList<>();
        Collection<ActRelationshipDto> actRelationshipDtoCollection = new ArrayList<>();
        Collection<Object> investigationAuditLogSummaryVOCollection = new ArrayList<>();
        Collection<OrganizationContainer> organizationContainerCollection = new ArrayList<>();
        Collection<Object> ctContactSummaryDTCollection = new ArrayList<>();
        Collection<Object> interviewSummaryDTCollection = new ArrayList<>();
        Collection<Object> notificationVOCollection = new ArrayList<>();
        Collection<Object> cssSummaryVOCollection = new ArrayList<>();
        Collection<Object> nbsAttachmentDTColl = new ArrayList<>();
        Collection<NbsNoteDto> nbsNoteDTColl = new ArrayList<>();
        Collection<Object> documentSummaryVOCollection = new ArrayList<>();
        boolean isOOSystemInd = true;
        boolean isOOSystemPendInd = true;
        boolean associatedNotificationsInd = true;
        boolean isUnsavedNote = true;
        boolean isMergeCase = true;
        Collection<Object> edxDocumentDTCollection = new ArrayList<>();
        boolean isRenterant = true;
        boolean isConversionHasModified = true;
        ExportReceivingFacilityDto exportReceivingFacilityDto = new ExportReceivingFacilityDto();
        Map<String, MessageLogDto> messageLogDTMap = new HashMap<>();

        container.setPageProxyTypeCd(pageProxyTypeCd);
        container.setPublicHealthCaseContainer(publicHealthCaseContainer);
        container.setInterviewContainer(interviewContainer);
        container.setTheNotificationContainer(notificationContainer);
        container.setInterventionContainer(interventionContainer);
        container.setPatientUid(patientUid);
        container.setCurrentInvestigator(currentInvestigator);
        container.setFieldSupervisor(fieldSupervisor);
        container.setCaseSupervisor(caseSupervisor);
        container.setSTDProgramArea(isSTDProgramArea);
        container.setThePersonContainerCollection(personContainerCollection);
        container.setPageVO(pageVO);
        container.setTheVaccinationSummaryVOCollection(vaccinationSummaryVOCollection);
        container.setTheNotificationSummaryVOCollection(notificationSummaryVOCollection);
        container.setTheTreatmentSummaryVOCollection(treatmentSummaryVOCollection);
        container.setTheLabReportSummaryVOCollection(labReportSummaryVOCollection);
        container.setTheMorbReportSummaryVOCollection(morbReportSummaryVOCollection);
        container.setTheParticipationDtoCollection(participationDtoCollection);
        container.setTheActRelationshipDtoCollection(actRelationshipDtoCollection);
        container.setTheInvestigationAuditLogSummaryVOCollection(investigationAuditLogSummaryVOCollection);
        container.setTheOrganizationContainerCollection(organizationContainerCollection);
        container.setTheCTContactSummaryDTCollection(ctContactSummaryDTCollection);
        container.setTheInterviewSummaryDTCollection(interviewSummaryDTCollection);
        container.setTheNotificationVOCollection(notificationVOCollection);
        container.setTheCSSummaryVOCollection(cssSummaryVOCollection);
        container.setNbsAttachmentDTColl(nbsAttachmentDTColl);
        container.setNbsNoteDTColl(nbsNoteDTColl);
        container.setTheDocumentSummaryVOCollection(documentSummaryVOCollection);
        container.setOOSystemInd(isOOSystemInd);
        container.setOOSystemPendInd(isOOSystemPendInd);
        container.setAssociatedNotificationsInd(associatedNotificationsInd);
        container.setUnsavedNote(isUnsavedNote);
        container.setMergeCase(isMergeCase);
        container.setTheEDXDocumentDTCollection(edxDocumentDTCollection);
        container.setRenterant(isRenterant);
        container.setConversionHasModified(isConversionHasModified);
        container.setExportReceivingFacilityDto(exportReceivingFacilityDto);
        container.setMessageLogDTMap(messageLogDTMap);

        assertEquals(pageProxyTypeCd, container.getPageProxyTypeCd());
        assertEquals(publicHealthCaseContainer, container.getPublicHealthCaseContainer());
        assertEquals(interviewContainer, container.getInterviewContainer());
        assertEquals(notificationContainer, container.getTheNotificationContainer());
        assertEquals(interventionContainer, container.getInterventionContainer());
        assertEquals(patientUid, container.getPatientUid());
        assertEquals(currentInvestigator, container.getCurrentInvestigator());
        assertEquals(fieldSupervisor, container.getFieldSupervisor());
        assertEquals(caseSupervisor, container.getCaseSupervisor());
        assertEquals(isSTDProgramArea, container.isSTDProgramArea());
        assertEquals(personContainerCollection, container.getThePersonContainerCollection());
        assertEquals(pageVO, container.getPageVO());
        assertEquals(vaccinationSummaryVOCollection, container.getTheVaccinationSummaryVOCollection());
        assertEquals(notificationSummaryVOCollection, container.getTheNotificationSummaryVOCollection());
        assertEquals(treatmentSummaryVOCollection, container.getTheTreatmentSummaryVOCollection());
        assertEquals(labReportSummaryVOCollection, container.getTheLabReportSummaryVOCollection());
        assertEquals(morbReportSummaryVOCollection, container.getTheMorbReportSummaryVOCollection());
        assertEquals(participationDtoCollection, container.getTheParticipationDtoCollection());
        assertEquals(actRelationshipDtoCollection, container.getTheActRelationshipDtoCollection());
        assertEquals(investigationAuditLogSummaryVOCollection, container.getTheInvestigationAuditLogSummaryVOCollection());
        assertEquals(organizationContainerCollection, container.getTheOrganizationContainerCollection());
        assertEquals(ctContactSummaryDTCollection, container.getTheCTContactSummaryDTCollection());
        assertEquals(interviewSummaryDTCollection, container.getTheInterviewSummaryDTCollection());
        assertEquals(notificationVOCollection, container.getTheNotificationVOCollection());
        assertEquals(cssSummaryVOCollection, container.getTheCSSummaryVOCollection());
        assertEquals(nbsAttachmentDTColl, container.getNbsAttachmentDTColl());
        assertEquals(nbsNoteDTColl, container.getNbsNoteDTColl());
        assertEquals(documentSummaryVOCollection, container.getTheDocumentSummaryVOCollection());
        assertEquals(isOOSystemInd, container.isOOSystemInd());
        assertEquals(isOOSystemPendInd, container.isOOSystemPendInd());
        assertEquals(associatedNotificationsInd, container.isAssociatedNotificationsInd());
        assertEquals(isUnsavedNote, container.isUnsavedNote());
        assertEquals(isMergeCase, container.isMergeCase());
        assertEquals(edxDocumentDTCollection, container.getTheEDXDocumentDTCollection());
        assertEquals(isRenterant, container.isRenterant());
        assertEquals(isConversionHasModified, container.isConversionHasModified());
        assertEquals(exportReceivingFacilityDto, container.getExportReceivingFacilityDto());
        assertEquals(messageLogDTMap, container.getMessageLogDTMap());

        assertNotNull(container.getThePersonContainerCollection());
        assertNotNull(container.getTheVaccinationSummaryVOCollection());
        assertNotNull(container.getTheNotificationSummaryVOCollection());
        assertNotNull(container.getTheTreatmentSummaryVOCollection());
        assertNotNull(container.getTheLabReportSummaryVOCollection());
        assertNotNull(container.getTheMorbReportSummaryVOCollection());
        assertNotNull(container.getTheParticipationDtoCollection());
        assertNotNull(container.getTheActRelationshipDtoCollection());
        assertNotNull(container.getTheInvestigationAuditLogSummaryVOCollection());
        assertNotNull(container.getTheOrganizationContainerCollection());
        assertNotNull(container.getTheCTContactSummaryDTCollection());
        assertNotNull(container.getTheInterviewSummaryDTCollection());
        assertNotNull(container.getTheNotificationVOCollection());
        assertNotNull(container.getTheCSSummaryVOCollection());
        assertNotNull(container.getNbsAttachmentDTColl());
        assertNotNull(container.getNbsNoteDTColl());
        assertNotNull(container.getTheDocumentSummaryVOCollection());
        assertNotNull(container.getTheEDXDocumentDTCollection());
    }
}