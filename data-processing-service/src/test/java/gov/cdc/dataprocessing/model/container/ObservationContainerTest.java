package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import gov.cdc.dataprocessing.model.dto.observation.*;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ObservationContainerTest {

    @Test
    void testGettersAndSetters() {
        ObservationContainer container = new ObservationContainer();

        ObservationDto observationDto = new ObservationDto();
        Collection<ActIdDto> actIdDtoCollection = new ArrayList<>();
        Collection<ObservationReasonDto> observationReasonDtoCollection = new ArrayList<>();
        Collection<ObservationInterpDto> observationInterpDtoCollection = new ArrayList<>();
        Collection<ObsValueCodedDto> obsValueCodedDtoCollection = new ArrayList<>();
        Collection<Object> obsValueCodedModDTCollection = new ArrayList<>();
        Collection<ObsValueTxtDto> obsValueTxtDtoCollection = new ArrayList<>();
        Collection<ObsValueDateDto> obsValueDateDtoCollection = new ArrayList<>();
        Collection<ObsValueNumericDto> obsValueNumericDtoCollection = new ArrayList<>();
        Collection<ActivityLocatorParticipationDto> activityLocatorParticipationDtoCollection = new ArrayList<>();
        Collection<ParticipationDto> participationDtoCollection = new ArrayList<>();
        Collection<ActRelationshipDto> actRelationshipDtoCollection = new ArrayList<>();
        Collection<MaterialDto> materialDtoCollection = new ArrayList<>();

        container.setTheObservationDto(observationDto);
        container.setTheActIdDtoCollection(actIdDtoCollection);
        container.setTheObservationReasonDtoCollection(observationReasonDtoCollection);
        container.setTheObservationInterpDtoCollection(observationInterpDtoCollection);
        container.setTheObsValueCodedDtoCollection(obsValueCodedDtoCollection);
        container.setTheObsValueCodedModDTCollection(obsValueCodedModDTCollection);
        container.setTheObsValueTxtDtoCollection(obsValueTxtDtoCollection);
        container.setTheObsValueDateDtoCollection(obsValueDateDtoCollection);
        container.setTheObsValueNumericDtoCollection(obsValueNumericDtoCollection);
        container.setTheActivityLocatorParticipationDtoCollection(activityLocatorParticipationDtoCollection);
        container.setTheParticipationDtoCollection(participationDtoCollection);
        container.setTheActRelationshipDtoCollection(actRelationshipDtoCollection);
        container.setTheMaterialDtoCollection(materialDtoCollection);

        assertEquals(observationDto, container.getTheObservationDto());
        assertEquals(actIdDtoCollection, container.getTheActIdDtoCollection());
        assertEquals(observationReasonDtoCollection, container.getTheObservationReasonDtoCollection());
        assertEquals(observationInterpDtoCollection, container.getTheObservationInterpDtoCollection());
        assertEquals(obsValueCodedDtoCollection, container.getTheObsValueCodedDtoCollection());
        assertEquals(obsValueCodedModDTCollection, container.getTheObsValueCodedModDTCollection());
        assertEquals(obsValueTxtDtoCollection, container.getTheObsValueTxtDtoCollection());
        assertEquals(obsValueDateDtoCollection, container.getTheObsValueDateDtoCollection());
        assertEquals(obsValueNumericDtoCollection, container.getTheObsValueNumericDtoCollection());
        assertEquals(activityLocatorParticipationDtoCollection, container.getTheActivityLocatorParticipationDtoCollection());
        assertEquals(participationDtoCollection, container.getTheParticipationDtoCollection());
        assertEquals(actRelationshipDtoCollection, container.getTheActRelationshipDtoCollection());
        assertEquals(materialDtoCollection, container.getTheMaterialDtoCollection());

        assertNotNull(container.getTheObservationDto());
        assertNotNull(container.getTheActIdDtoCollection());
        assertNotNull(container.getTheObservationReasonDtoCollection());
        assertNotNull(container.getTheObservationInterpDtoCollection());
        assertNotNull(container.getTheObsValueCodedDtoCollection());
        assertNotNull(container.getTheObsValueCodedModDTCollection());
        assertNotNull(container.getTheObsValueTxtDtoCollection());
        assertNotNull(container.getTheObsValueDateDtoCollection());
        assertNotNull(container.getTheObsValueNumericDtoCollection());
        assertNotNull(container.getTheActivityLocatorParticipationDtoCollection());
        assertNotNull(container.getTheParticipationDtoCollection());
        assertNotNull(container.getTheActRelationshipDtoCollection());
        assertNotNull(container.getTheMaterialDtoCollection());
    }
}
