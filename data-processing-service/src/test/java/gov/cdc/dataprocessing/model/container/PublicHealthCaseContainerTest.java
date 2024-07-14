package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsCaseAnswerDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.ConfirmationMethodDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PublicHealthCaseContainerTest {

    @Test
    void testGettersAndSetters() {
        PublicHealthCaseContainer container = new PublicHealthCaseContainer();

        CaseManagementDto caseManagementDto = new CaseManagementDto();
        PublicHealthCaseDto publicHealthCaseDto = new PublicHealthCaseDto();
        Collection<ConfirmationMethodDto> confirmationMethodDtoCollection = new ArrayList<>();
        Collection<ActIdDto> actIdDtoCollection = new ArrayList<>();
        Collection<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection = new ArrayList<>();
        Collection<ParticipationDto> participationDtoCollection = new ArrayList<>();
        Collection<ActRelationshipDto> actRelationshipDtoCollection = new ArrayList<>();
        Collection<NbsActEntityDto> nbsCaseEntityCollection = new ArrayList<>();
        Collection<NbsCaseAnswerDto> nbsAnswerCollection = new ArrayList<>();
        Collection<EDXActivityDetailLogDto> edxPHCRLogDetailDtoCollection = new ArrayList<>();
        Collection<EDXEventProcessDto> edxEventProcessDtoCollection = new ArrayList<>();

        container.setPamCase(true);
        container.setTheCaseManagementDto(caseManagementDto);
        container.setThePublicHealthCaseDto(publicHealthCaseDto);
        container.setTheConfirmationMethodDTCollection(confirmationMethodDtoCollection);
        container.setTheActIdDTCollection(actIdDtoCollection);
        container.setTheActivityLocatorParticipationDTCollection(activityLocatorParticipationDtoCollection);
        container.setTheParticipationDTCollection(participationDtoCollection);
        container.setTheActRelationshipDTCollection(actRelationshipDtoCollection);
        container.setNbsCaseEntityCollection(nbsCaseEntityCollection);
        container.setNbsAnswerCollection(nbsAnswerCollection);
        container.setEdxPHCRLogDetailDTCollection(edxPHCRLogDetailDtoCollection);
        container.setEdxEventProcessDtoCollection(edxEventProcessDtoCollection);
        container.setErrorText("Error");
        container.setCoinfectionCondition(true);

        assertEquals(true, container.isPamCase());
        assertEquals(caseManagementDto, container.getTheCaseManagementDto());
        assertEquals(publicHealthCaseDto, container.getThePublicHealthCaseDto());
        assertEquals(confirmationMethodDtoCollection, container.getTheConfirmationMethodDTCollection());
        assertEquals(actIdDtoCollection, container.getTheActIdDTCollection());
        assertEquals(activityLocatorParticipationDtoCollection, container.getTheActivityLocatorParticipationDTCollection());
        assertEquals(participationDtoCollection, container.getTheParticipationDTCollection());
        assertEquals(actRelationshipDtoCollection, container.getTheActRelationshipDTCollection());
        assertEquals(nbsCaseEntityCollection, container.getNbsCaseEntityCollection());
        assertEquals(nbsAnswerCollection, container.getNbsAnswerCollection());
        assertEquals(edxPHCRLogDetailDtoCollection, container.getEdxPHCRLogDetailDTCollection());
        assertEquals(edxEventProcessDtoCollection, container.getEdxEventProcessDtoCollection());
        assertEquals("Error", container.getErrorText());
        assertEquals(true, container.isCoinfectionCondition());
    }
}
