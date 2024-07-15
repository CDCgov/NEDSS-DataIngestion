package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.PlaceContainer;
import gov.cdc.dataprocessing.model.dto.phc.PlaceDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaceContainerTest {

    @Test
    void testGettersAndSetters() {
        PlaceContainer placeContainer = new PlaceContainer();

        PlaceDto placeDto = new PlaceDto();
        Collection<Object> entityLocatorParticipationDTCollection = new ArrayList<>();
        Collection<Object> entityIdDTCollection = new ArrayList<>();
        Collection<Object> participationDTCollection = new ArrayList<>();
        Collection<Object> roleDTCollection = new ArrayList<>();

        placeContainer.setThePlaceDT(placeDto);
        placeContainer.setTheEntityLocatorParticipationDTCollection(entityLocatorParticipationDTCollection);
        placeContainer.setTheEntityIdDTCollection(entityIdDTCollection);
        placeContainer.setTheParticipationDTCollection(participationDTCollection);
        placeContainer.setTheRoleDTCollection(roleDTCollection);
        placeContainer.setLocalIdentifier("testLocalIdentifier");

        assertEquals(placeDto, placeContainer.getThePlaceDT());
        assertEquals(entityLocatorParticipationDTCollection, placeContainer.getTheEntityLocatorParticipationDTCollection());
        assertEquals(entityIdDTCollection, placeContainer.getTheEntityIdDTCollection());
        assertEquals(participationDTCollection, placeContainer.getTheParticipationDTCollection());
        assertEquals(roleDTCollection, placeContainer.getTheRoleDTCollection());
        assertEquals("testLocalIdentifier", placeContainer.getLocalIdentifier());
    }
}
