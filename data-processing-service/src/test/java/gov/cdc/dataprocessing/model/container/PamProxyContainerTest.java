package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.NotificationContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PersonContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.nbs.NbsNoteDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.ExportReceivingFacilityDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PamProxyContainerTest {

    @Test
    void testGettersAndSetters() {
        PamProxyContainer pamProxyContainer = new PamProxyContainer();

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        Collection<PersonContainer> thePersonVOCollection = new ArrayList<>();
        BasePamContainer pamVO = new BasePamContainer();
        Collection<Object> theVaccinationSummaryVOCollection = new ArrayList<>();
        Collection<Object> theNotificationSummaryVOCollection = new ArrayList<>();
        Collection<Object> theTreatmentSummaryVOCollection = new ArrayList<>();
        Collection<Object> theLabReportSummaryVOCollection = new ArrayList<>();
        Collection<Object> theMorbReportSummaryVOCollection = new ArrayList<>();
        Collection<ParticipationDto> theParticipationDTCollection = new ArrayList<>();
        Collection<Object> theInvestigationAuditLogSummaryVOCollection = new ArrayList<>();
        Collection<Object> theOrganizationVOCollection = new ArrayList<>();
        Collection<Object> theNotificationVOCollection = new ArrayList<>();
        boolean associatedNotificationsInd = true;
        NotificationContainer theNotificationContainer = new NotificationContainer();
        Collection<Object> theDocumentSummaryVOCollection = new ArrayList<>();
        boolean isOOSystemInd = true;
        boolean isOOSystemPendInd = true;
        Collection<Object> theCTContactSummaryDTCollection = new ArrayList<>();
        Collection<Object> nbsAttachmentDTColl = new ArrayList<>();
        Collection<NbsNoteDto> nbsNoteDTColl = new ArrayList<>();
        boolean isUnsavedNote = true;
        ExportReceivingFacilityDto exportReceivingFacilityDto = new ExportReceivingFacilityDto();

        pamProxyContainer.setPublicHealthCaseContainer(publicHealthCaseContainer);
        pamProxyContainer.setThePersonVOCollection(thePersonVOCollection);
        pamProxyContainer.setPamVO(pamVO);
        pamProxyContainer.setTheVaccinationSummaryVOCollection(theVaccinationSummaryVOCollection);
        pamProxyContainer.setTheNotificationSummaryVOCollection(theNotificationSummaryVOCollection);
        pamProxyContainer.setTheTreatmentSummaryVOCollection(theTreatmentSummaryVOCollection);
        pamProxyContainer.setTheLabReportSummaryVOCollection(theLabReportSummaryVOCollection);
        pamProxyContainer.setTheMorbReportSummaryVOCollection(theMorbReportSummaryVOCollection);
        pamProxyContainer.setTheParticipationDTCollection(theParticipationDTCollection);
        pamProxyContainer.setTheInvestigationAuditLogSummaryVOCollection(theInvestigationAuditLogSummaryVOCollection);
        pamProxyContainer.setTheOrganizationVOCollection(theOrganizationVOCollection);
        pamProxyContainer.setTheNotificationVOCollection(theNotificationVOCollection);
        pamProxyContainer.setAssociatedNotificationsInd(associatedNotificationsInd);
        pamProxyContainer.setTheNotificationContainer(theNotificationContainer);
        pamProxyContainer.setTheDocumentSummaryVOCollection(theDocumentSummaryVOCollection);
        pamProxyContainer.setOOSystemInd(isOOSystemInd);
        pamProxyContainer.setOOSystemPendInd(isOOSystemPendInd);
        pamProxyContainer.setTheCTContactSummaryDTCollection(theCTContactSummaryDTCollection);
        pamProxyContainer.setNbsAttachmentDTColl(nbsAttachmentDTColl);
        pamProxyContainer.setNbsNoteDTColl(nbsNoteDTColl);
        pamProxyContainer.setUnsavedNote(isUnsavedNote);
        pamProxyContainer.setExportReceivingFacilityDto(exportReceivingFacilityDto);

        assertEquals(publicHealthCaseContainer, pamProxyContainer.getPublicHealthCaseContainer());
        assertEquals(thePersonVOCollection, pamProxyContainer.getThePersonVOCollection());
        assertEquals(pamVO, pamProxyContainer.getPamVO());
        assertEquals(theVaccinationSummaryVOCollection, pamProxyContainer.getTheVaccinationSummaryVOCollection());
        assertEquals(theNotificationSummaryVOCollection, pamProxyContainer.getTheNotificationSummaryVOCollection());
        assertEquals(theTreatmentSummaryVOCollection, pamProxyContainer.getTheTreatmentSummaryVOCollection());
        assertEquals(theLabReportSummaryVOCollection, pamProxyContainer.getTheLabReportSummaryVOCollection());
        assertEquals(theMorbReportSummaryVOCollection, pamProxyContainer.getTheMorbReportSummaryVOCollection());
        assertEquals(theParticipationDTCollection, pamProxyContainer.getTheParticipationDTCollection());
        assertEquals(theInvestigationAuditLogSummaryVOCollection, pamProxyContainer.getTheInvestigationAuditLogSummaryVOCollection());
        assertEquals(theOrganizationVOCollection, pamProxyContainer.getTheOrganizationVOCollection());
        assertEquals(theNotificationVOCollection, pamProxyContainer.getTheNotificationVOCollection());
        assertEquals(associatedNotificationsInd, pamProxyContainer.isAssociatedNotificationsInd());
        assertEquals(theNotificationContainer, pamProxyContainer.getTheNotificationContainer());
        assertEquals(theDocumentSummaryVOCollection, pamProxyContainer.getTheDocumentSummaryVOCollection());
        assertEquals(isOOSystemInd, pamProxyContainer.isOOSystemInd());
        assertEquals(isOOSystemPendInd, pamProxyContainer.isOOSystemPendInd());
        assertEquals(theCTContactSummaryDTCollection, pamProxyContainer.getTheCTContactSummaryDTCollection());
        assertEquals(nbsAttachmentDTColl, pamProxyContainer.getNbsAttachmentDTColl());
        assertEquals(nbsNoteDTColl, pamProxyContainer.getNbsNoteDTColl());
        assertEquals(isUnsavedNote, pamProxyContainer.isUnsavedNote());
        assertEquals(exportReceivingFacilityDto, pamProxyContainer.getExportReceivingFacilityDto());
    }
}
