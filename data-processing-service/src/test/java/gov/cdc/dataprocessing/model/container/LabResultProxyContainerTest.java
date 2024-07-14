package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.MaterialContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.log.MessageLogDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class LabResultProxyContainerTest {

    @Test
    void testDefaultConstructor() {
        LabResultProxyContainer container = new LabResultProxyContainer();
        assertNotNull(container);
        assertFalse(container.associatedNotificationInd);
        assertNull(container.getSendingFacilityUid());
        assertFalse(container.associatedInvInd);
        assertNotNull(container.getTheObservationContainerCollection());
        assertNotNull(container.getTheMaterialContainerCollection());
        assertNotNull(container.getTheRoleDtoCollection());
        assertNull(container.getTheActIdDTCollection());
        //    assertNotNull(container.getTheInterventionVOCollection());
        assertNull(container.getEDXDocumentCollection());
        assertNull(container.getTheConditionsList());
        assertNull(container.getMessageLogDCollection());
        assertNull(container.getLabClia());
        assertFalse(container.isManualLab());
        assertNotNull(container.getTheParticipationDtoCollection());
        assertNotNull(container.getTheOrganizationContainerCollection());
    }

    @Test
    void testSetAndGetValues() {
        LabResultProxyContainer container = new LabResultProxyContainer();
        container.setAssociatedNotificationInd(true);
        container.setSendingFacilityUid(123L);
        container.setAssociatedInvInd(true);
        container.setLabClia("LabClia");
        container.setManualLab(true);

        assertTrue(container.isAssociatedNotificationInd());
        assertEquals(123L, container.getSendingFacilityUid());
        assertTrue(container.isAssociatedInvInd());
        assertEquals("LabClia", container.getLabClia());
        assertTrue(container.isManualLab());
    }

    @Test
    void testSetAndGetCollections() {
        LabResultProxyContainer container = new LabResultProxyContainer();

        Collection<ObservationContainer> observationContainerCollection = new ArrayList<>();
        observationContainerCollection.add(new ObservationContainer());
        container.setTheObservationContainerCollection(observationContainerCollection);
        assertEquals(observationContainerCollection, container.getTheObservationContainerCollection());

        Collection<MaterialContainer> materialContainerCollection = new ArrayList<>();
        materialContainerCollection.add(new MaterialContainer());
        container.setTheMaterialContainerCollection(materialContainerCollection);
        assertEquals(materialContainerCollection, container.getTheMaterialContainerCollection());

        Collection<RoleDto> roleDtoCollection = new ArrayList<>();
        roleDtoCollection.add(new RoleDto());
        container.setTheRoleDtoCollection(roleDtoCollection);
        assertEquals(roleDtoCollection, container.getTheRoleDtoCollection());

        Collection<Object> actIdDTCollection = new ArrayList<>();
        actIdDTCollection.add(new Object());
        container.setTheActIdDTCollection(actIdDTCollection);
        assertEquals(actIdDTCollection, container.getTheActIdDTCollection());

        Collection<Object> interventionVOCollection = new ArrayList<>();
        interventionVOCollection.add(new Object());
        container.setTheInterventionVOCollection(interventionVOCollection);
        assertEquals(interventionVOCollection, container.getTheInterventionVOCollection());

        Collection<EDXDocumentDto> edxDocumentCollection = new ArrayList<>();
        edxDocumentCollection.add(new EDXDocumentDto());
        container.setEDXDocumentCollection(edxDocumentCollection);
        assertEquals(edxDocumentCollection, container.getEDXDocumentCollection());

        ArrayList<String> conditionsList = new ArrayList<>();
        conditionsList.add("Condition1");
        container.setTheConditionsList(conditionsList);
        assertEquals(conditionsList, container.getTheConditionsList());

        Collection<MessageLogDto> messageLogDCollection = new ArrayList<>();
        messageLogDCollection.add(new MessageLogDto());
        container.setMessageLogDCollection(messageLogDCollection);
        assertEquals(messageLogDCollection, container.getMessageLogDCollection());

        Collection<ParticipationDto> participationDtoCollection = new ArrayList<>();
        participationDtoCollection.add(new ParticipationDto());
        container.setTheParticipationDtoCollection(participationDtoCollection);
        assertEquals(participationDtoCollection, container.getTheParticipationDtoCollection());

        Collection<OrganizationContainer> organizationContainerCollection = new ArrayList<>();
        organizationContainerCollection.add(new OrganizationContainer());
        container.setTheOrganizationContainerCollection(organizationContainerCollection);
        assertEquals(organizationContainerCollection, container.getTheOrganizationContainerCollection());
    }
}
