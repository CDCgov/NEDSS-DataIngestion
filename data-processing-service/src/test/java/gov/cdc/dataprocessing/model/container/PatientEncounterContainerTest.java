package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.PatientEncounterContainer;
import gov.cdc.dataprocessing.model.dto.phc.PatientEncounterDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientEncounterContainerTest {

    @Test
    void testGettersAndSetters() {
        PatientEncounterContainer patientEncounterContainer = new PatientEncounterContainer();

        PatientEncounterDto patientEncounterDto = new PatientEncounterDto();
        Collection<Object> activityLocatorParticipationDTCollection = new ArrayList<>();
        Collection<Object> actIdDTCollection = new ArrayList<>();
        Collection<Object> participationDTCollection = new ArrayList<>();
        Collection<Object> actRelationshipDTCollection = new ArrayList<>();

        patientEncounterContainer.setThePatientEncounterDT(patientEncounterDto);
        patientEncounterContainer.setTheActivityLocatorParticipationDTCollection(activityLocatorParticipationDTCollection);
        patientEncounterContainer.setTheActIdDTCollection(actIdDTCollection);
        patientEncounterContainer.setTheParticipationDTCollection(participationDTCollection);
        patientEncounterContainer.setTheActRelationshipDTCollection(actRelationshipDTCollection);

        assertEquals(patientEncounterDto, patientEncounterContainer.getThePatientEncounterDT());
        assertEquals(activityLocatorParticipationDTCollection, patientEncounterContainer.getTheActivityLocatorParticipationDTCollection());
        assertEquals(actIdDTCollection, patientEncounterContainer.getTheActIdDTCollection());
        assertEquals(participationDTCollection, patientEncounterContainer.getTheParticipationDTCollection());
        assertEquals(actRelationshipDTCollection, patientEncounterContainer.getTheActRelationshipDTCollection());
    }
}
