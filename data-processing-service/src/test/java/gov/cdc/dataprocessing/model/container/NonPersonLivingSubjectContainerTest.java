package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.NonPersonLivingSubjectContainer;
import gov.cdc.dataprocessing.model.dto.phc.NonPersonLivingSubjectDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NonPersonLivingSubjectContainerTest {

    @Test
    void testGettersAndSetters() {
        NonPersonLivingSubjectContainer container = new NonPersonLivingSubjectContainer();

        NonPersonLivingSubjectDto nonPersonLivingSubjectDto = new NonPersonLivingSubjectDto();
        Collection<Object> entityLocatorParticipationCollection = new ArrayList<>();
        Collection<Object> entityIdCollection = new ArrayList<>();
        Collection<Object> participationCollection = new ArrayList<>();
        Collection<Object> roleCollection = new ArrayList<>();

        container.setTheNonPersonLivingSubjectDT(nonPersonLivingSubjectDto);
        container.setTheEntityLocatorParticipationDTCollection(entityLocatorParticipationCollection);
        container.setTheEntityIdDTCollection(entityIdCollection);
        container.setTheParticipationDTCollection(participationCollection);
        container.setTheRoleDTCollection(roleCollection);

        assertEquals(nonPersonLivingSubjectDto, container.getTheNonPersonLivingSubjectDT());
        assertEquals(entityLocatorParticipationCollection, container.getTheEntityLocatorParticipationDTCollection());
        assertEquals(entityIdCollection, container.getTheEntityIdDTCollection());
        assertEquals(participationCollection, container.getTheParticipationDTCollection());
        assertEquals(roleCollection, container.getTheRoleDTCollection());
    }
}
