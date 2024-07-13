package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.InvestigationContainer;
import gov.cdc.dataprocessing.model.container.model.NotificationContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class InvestigationContainerTest {

    @Test
    void testSetAndGetThePublicHealthCaseContainer() {
        InvestigationContainer container = new InvestigationContainer();
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();

        // Set values in the publicHealthCaseContainer for testing

        container.setThePublicHealthCaseContainer(publicHealthCaseContainer);
        PublicHealthCaseContainer retrievedContainer = container.getThePublicHealthCaseContainer();

        assertNotNull(retrievedContainer, "The publicHealthCaseContainer should not be null");

    }

    @Test
    void testSetAndGetCollections() {
        InvestigationContainer container = new InvestigationContainer();

        Collection<Object> participationDTCollection = new ArrayList<>();
        participationDTCollection.add("Participation1");
        container.setTheParticipationDTCollection(participationDTCollection);
        assertEquals(participationDTCollection, container.getTheParticipationDTCollection(), "The participationDTCollection should match the set one");

        Collection<Object> roleDTCollection = new ArrayList<>();
        roleDTCollection.add("Role1");
        container.setTheRoleDTCollection(roleDTCollection);
        assertEquals(roleDTCollection, container.getTheRoleDTCollection(), "The roleDTCollection should match the set one");

        Collection<Object> actRelationshipDTCollection = new ArrayList<>();
        actRelationshipDTCollection.add("ActRelationship1");
        container.setTheActRelationshipDTCollection(actRelationshipDTCollection);
        assertEquals(actRelationshipDTCollection, container.getTheActRelationshipDTCollection(), "The actRelationshipDTCollection should match the set one");

        // Add similar tests for other collections if necessary
    }

    @Test
    void testSetAndGetNotificationContainer() {
        InvestigationContainer container = new InvestigationContainer();
        NotificationContainer notificationContainer = new NotificationContainer();



        container.setTheNotificationContainer(notificationContainer);
        NotificationContainer retrievedContainer = container.getTheNotificationContainer();

        assertNotNull(retrievedContainer, "The notificationContainer should not be null");

    }

    @Test
    void testSetAndGetBooleanFields() {
        InvestigationContainer container = new InvestigationContainer();

        container.setAssociatedNotificationsInd(true);
        assertTrue(container.isAssociatedNotificationsInd(), "The associatedNotificationsInd should be true");

        container.setBusinessObjectName("TestBusinessObject");
        assertEquals("TestBusinessObject", container.getBusinessObjectName(), "The businessObjectName should be 'TestBusinessObject'");

        container.setOOSystemInd(true);
        assertTrue(container.isOOSystemInd(), "The isOOSystemInd should be true");

        container.setOOSystemPendInd(true);
        assertTrue(container.isOOSystemPendInd(), "The isOOSystemPendInd should be true");
    }
}
