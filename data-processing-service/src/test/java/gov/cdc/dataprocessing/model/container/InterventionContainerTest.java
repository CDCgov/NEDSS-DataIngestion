package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.InterventionContainer;
import gov.cdc.dataprocessing.model.dto.phc.InterventionDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class InterventionContainerTest {

    @Test
    void testGetAndSetInterventionDto() {
        InterventionContainer container = new InterventionContainer();
        InterventionDto interventionDto = new InterventionDto();


        container.setTheInterventionDto(interventionDto);

        InterventionDto retrievedDto = container.getTheInterventionDto();
        assertNotNull(retrievedDto, "The interventionDto should not be null");

    }

    @Test
    void testDefaultInterventionDto() {
        InterventionContainer container = new InterventionContainer();

        InterventionDto defaultDto = container.getTheInterventionDto();
        assertNotNull(defaultDto, "The default interventionDto should not be null");
    }

    @Test
    void testSetAndGetCollections() {
        InterventionContainer container = new InterventionContainer();

        Collection<Object> procedure1DTCollection = new ArrayList<>();
        procedure1DTCollection.add("Procedure1");
        container.setTheProcedure1DTCollection(procedure1DTCollection);
        assertEquals(procedure1DTCollection, container.getTheProcedure1DTCollection(), "The procedure1DTCollection should match the set one");

        Collection<Object> substanceAdministrationDTCollection = new ArrayList<>();
        substanceAdministrationDTCollection.add("SubstanceAdministration1");
        container.setTheSubstanceAdministrationDTCollection(substanceAdministrationDTCollection);
        assertEquals(substanceAdministrationDTCollection, container.getTheSubstanceAdministrationDTCollection(), "The substanceAdministrationDTCollection should match the set one");

        Collection<Object> actIdDTCollection = new ArrayList<>();
        actIdDTCollection.add("ActId1");
        container.setTheActIdDTCollection(actIdDTCollection);
        assertEquals(actIdDTCollection, container.getTheActIdDTCollection(), "The actIdDTCollection should match the set one");

        Collection<Object> activityLocatorParticipationDTCollection = new ArrayList<>();
        activityLocatorParticipationDTCollection.add("ActivityLocatorParticipation1");
        container.setTheActivityLocatorParticipationDTCollection(activityLocatorParticipationDTCollection);
        assertEquals(activityLocatorParticipationDTCollection, container.getTheActivityLocatorParticipationDTCollection(), "The activityLocatorParticipationDTCollection should match the set one");

        Collection<Object> participationDTCollection = new ArrayList<>();
        participationDTCollection.add("Participation1");
        container.setTheParticipationDTCollection(participationDTCollection);
        assertEquals(participationDTCollection, container.getTheParticipationDTCollection(), "The participationDTCollection should match the set one");

        Collection<Object> actRelationshipDTCollection = new ArrayList<>();
        actRelationshipDTCollection.add("ActRelationship1");
        container.setTheActRelationshipDTCollection(actRelationshipDTCollection);
        assertEquals(actRelationshipDTCollection, container.getTheActRelationshipDTCollection(), "The actRelationshipDTCollection should match the set one");
    }

    @Test
    void testDefaultCollections() {
        InterventionContainer container = new InterventionContainer();

        assertTrue(container.getTheProcedure1DTCollection() == null || container.getTheProcedure1DTCollection().isEmpty(), "The default procedure1DTCollection should be null or empty");
        assertTrue(container.getTheSubstanceAdministrationDTCollection() == null || container.getTheSubstanceAdministrationDTCollection().isEmpty(), "The default substanceAdministrationDTCollection should be null or empty");
        assertTrue(container.getTheActIdDTCollection() == null || container.getTheActIdDTCollection().isEmpty(), "The default actIdDTCollection should be null or empty");
        assertTrue(container.getTheActivityLocatorParticipationDTCollection() == null || container.getTheActivityLocatorParticipationDTCollection().isEmpty(), "The default activityLocatorParticipationDTCollection should be null or empty");
        assertTrue(container.getTheParticipationDTCollection() == null || container.getTheParticipationDTCollection().isEmpty(), "The default participationDTCollection should be null or empty");
        assertTrue(container.getTheActRelationshipDTCollection() == null || container.getTheActRelationshipDTCollection().isEmpty(), "The default actRelationshipDTCollection should be null or empty");
    }
}