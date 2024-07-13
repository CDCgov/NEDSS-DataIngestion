package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.ReferralContainer;
import gov.cdc.dataprocessing.model.dto.phc.ReferralDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReferralContainerTest {

    @Test
    void testGettersAndSetters() {
        ReferralContainer container = new ReferralContainer();

        ReferralDto referralDto = new ReferralDto();
        Collection<Object> activityLocatorParticipationDTCollection = new ArrayList<>();
        Collection<Object> actIdDTCollection = new ArrayList<>();
        Collection<Object> participationDTCollection = new ArrayList<>();
        Collection<Object> actRelationshipDTCollection = new ArrayList<>();

        container.setTheReferralDT(referralDto);
        container.setTheActivityLocatorParticipationDTCollection(activityLocatorParticipationDTCollection);
        container.setTheActIdDTCollection(actIdDTCollection);
        container.setTheParticipationDTCollection(participationDTCollection);
        container.setTheActRelationshipDTCollection(actRelationshipDTCollection);

        assertEquals(referralDto, container.getTheReferralDT());
        assertEquals(activityLocatorParticipationDTCollection, container.getTheActivityLocatorParticipationDTCollection());
        assertEquals(actIdDTCollection, container.getTheActIdDTCollection());
        assertEquals(participationDTCollection, container.getTheParticipationDTCollection());
        assertEquals(actRelationshipDTCollection, container.getTheActRelationshipDTCollection());
    }
}
