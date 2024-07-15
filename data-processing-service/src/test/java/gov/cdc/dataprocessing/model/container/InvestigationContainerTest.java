package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.InvestigationContainer;
import gov.cdc.dataprocessing.model.container.model.NotificationContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
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


    @Test
    void testGettersAndSetters() {
        InvestigationContainer container = new InvestigationContainer();

        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        container.setThePublicHealthCaseContainer(publicHealthCaseContainer);
        assertEquals(publicHealthCaseContainer, container.getThePublicHealthCaseContainer());

        Collection<Object> participationDTCollection = new ArrayList<>();
        container.setTheParticipationDTCollection(participationDTCollection);
        assertEquals(participationDTCollection, container.getTheParticipationDTCollection());

        Collection<Object> roleDTCollection = new ArrayList<>();
        container.setTheRoleDTCollection(roleDTCollection);
        assertEquals(roleDTCollection, container.getTheRoleDTCollection());

        Collection<Object> actRelationshipDTCollection = new ArrayList<>();
        container.setTheActRelationshipDTCollection(actRelationshipDTCollection);
        assertEquals(actRelationshipDTCollection, container.getTheActRelationshipDTCollection());

        Collection<Object> personVOCollection = new ArrayList<>();
        container.setThePersonVOCollection(personVOCollection);
        assertEquals(personVOCollection, container.getThePersonVOCollection());

        Collection<Object> organizationVOCollection = new ArrayList<>();
        container.setTheOrganizationVOCollection(organizationVOCollection);
        assertEquals(organizationVOCollection, container.getTheOrganizationVOCollection());

        Collection<Object> materialVOCollection = new ArrayList<>();
        container.setTheMaterialVOCollection(materialVOCollection);
        assertEquals(materialVOCollection, container.getTheMaterialVOCollection());

        Collection<ObservationContainer> observationVOCollection = new ArrayList<>();
        container.setTheObservationVOCollection(observationVOCollection);
        assertEquals(observationVOCollection, container.getTheObservationVOCollection());

        Collection<Object> interventionVOCollection = new ArrayList<>();
        container.setTheInterventionVOCollection(interventionVOCollection);
        assertEquals(interventionVOCollection, container.getTheInterventionVOCollection());

        Collection<Object> entityGroupVOCollection = new ArrayList<>();
        container.setTheEntityGroupVOCollection(entityGroupVOCollection);
        assertEquals(entityGroupVOCollection, container.getTheEntityGroupVOCollection());

        Collection<Object> nonPersonLivingSubjectVOCollection = new ArrayList<>();
        container.setTheNonPersonLivingSubjectVOCollection(nonPersonLivingSubjectVOCollection);
        assertEquals(nonPersonLivingSubjectVOCollection, container.getTheNonPersonLivingSubjectVOCollection());

        Collection<Object> placeVOCollection = new ArrayList<>();
        container.setThePlaceVOCollection(placeVOCollection);
        assertEquals(placeVOCollection, container.getThePlaceVOCollection());

        Collection<Object> notificationVOCollection = new ArrayList<>();
        container.setTheNotificationVOCollection(notificationVOCollection);
        assertEquals(notificationVOCollection, container.getTheNotificationVOCollection());

        Collection<Object> referralVOCollection = new ArrayList<>();
        container.setTheReferralVOCollection(referralVOCollection);
        assertEquals(referralVOCollection, container.getTheReferralVOCollection());

        Collection<Object> patientEncounterVOCollection = new ArrayList<>();
        container.setThePatientEncounterVOCollection(patientEncounterVOCollection);
        assertEquals(patientEncounterVOCollection, container.getThePatientEncounterVOCollection());

        Collection<Object> clinicalDocumentVOCollection = new ArrayList<>();
        container.setTheClinicalDocumentVOCollection(clinicalDocumentVOCollection);
        assertEquals(clinicalDocumentVOCollection, container.getTheClinicalDocumentVOCollection());

        Collection<Object> observationSummaryVOCollection = new ArrayList<>();
        container.setTheObservationSummaryVOCollection(observationSummaryVOCollection);
        assertEquals(observationSummaryVOCollection, container.getTheObservationSummaryVOCollection());

        Collection<Object> vaccinationSummaryVOCollection = new ArrayList<>();
        container.setTheVaccinationSummaryVOCollection(vaccinationSummaryVOCollection);
        assertEquals(vaccinationSummaryVOCollection, container.getTheVaccinationSummaryVOCollection());

        Collection<Object> notificationSummaryVOCollection = new ArrayList<>();
        container.setTheNotificationSummaryVOCollection(notificationSummaryVOCollection);
        assertEquals(notificationSummaryVOCollection, container.getTheNotificationSummaryVOCollection());

        Collection<Object> treatmentSummaryVOCollection = new ArrayList<>();
        container.setTheTreatmentSummaryVOCollection(treatmentSummaryVOCollection);
        assertEquals(treatmentSummaryVOCollection, container.getTheTreatmentSummaryVOCollection());

        Collection<Object> labReportSummaryVOCollection = new ArrayList<>();
        container.setTheLabReportSummaryVOCollection(labReportSummaryVOCollection);
        assertEquals(labReportSummaryVOCollection, container.getTheLabReportSummaryVOCollection());

        Collection<Object> morbReportSummaryVOCollection = new ArrayList<>();
        container.setTheMorbReportSummaryVOCollection(morbReportSummaryVOCollection);
        assertEquals(morbReportSummaryVOCollection, container.getTheMorbReportSummaryVOCollection());

        NotificationContainer notificationContainer = new NotificationContainer();
        container.setTheNotificationContainer(notificationContainer);
        assertEquals(notificationContainer, container.getTheNotificationContainer());

        container.setAssociatedNotificationsInd(true);
        assertTrue(container.isAssociatedNotificationsInd());

        String businessObjectName = "businessObjectName";
        container.setBusinessObjectName(businessObjectName);
        assertEquals(businessObjectName, container.getBusinessObjectName());

        container.setOOSystemInd(true);
        assertTrue(container.isOOSystemInd());

        container.setOOSystemPendInd(true);
        assertTrue(container.isOOSystemPendInd());

        Collection<Object> contactVOColl = new ArrayList<>();
        container.setTheContactVOColl(contactVOColl);
        assertEquals(contactVOColl, container.getTheContactVOColl());

        Collection<Object> ctContactSummaryDTCollection = new ArrayList<>();
        container.setTheCTContactSummaryDTCollection(ctContactSummaryDTCollection);
        assertEquals(ctContactSummaryDTCollection, container.getTheCTContactSummaryDTCollection());

        Collection<Object> documentSummaryVOCollection = new ArrayList<>();
        container.setTheDocumentSummaryVOCollection(documentSummaryVOCollection);
        assertEquals(documentSummaryVOCollection, container.getTheDocumentSummaryVOCollection());
    }
}
