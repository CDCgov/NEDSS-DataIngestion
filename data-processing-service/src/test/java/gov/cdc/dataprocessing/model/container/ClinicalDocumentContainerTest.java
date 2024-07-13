package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.ClinicalDocumentContainer;
import gov.cdc.dataprocessing.model.dto.phc.ClinicalDocumentDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ClinicalDocumentContainerTest {

    @Test
    void testGettersAndSetters() {
        ClinicalDocumentContainer clinicalDocumentContainer = new ClinicalDocumentContainer();

        // Test inherited boolean fields from BaseContainer
        clinicalDocumentContainer.setItNew(true);
        clinicalDocumentContainer.setItOld(true);
        clinicalDocumentContainer.setItDirty(true);
        clinicalDocumentContainer.setItDelete(true);

        assertTrue(clinicalDocumentContainer.isItNew());
        assertTrue(clinicalDocumentContainer.isItOld());
        assertTrue(clinicalDocumentContainer.isItDirty());
        assertTrue(clinicalDocumentContainer.isItDelete());

        // Test inherited String field from BaseContainer
        String superClassType = "TestSuperClass";
        clinicalDocumentContainer.setSuperClassType(superClassType);
        assertEquals(superClassType, clinicalDocumentContainer.getSuperClassType());

        // Test inherited Collection field from BaseContainer
        Collection<Object> ldfs = new ArrayList<>();
        ldfs.add("TestObject");
        clinicalDocumentContainer.setLdfs(ldfs);
        assertEquals(ldfs, clinicalDocumentContainer.getLdfs());

        // Test ClinicalDocumentContainer specific fields
        ClinicalDocumentDto clinicalDocumentDto = new ClinicalDocumentDto();
        clinicalDocumentContainer.setTheClinicalDocumentDT(clinicalDocumentDto);
        assertEquals(clinicalDocumentDto, clinicalDocumentContainer.getTheClinicalDocumentDT());

        Collection<Object> activityLocatorParticipationDTCollection = new ArrayList<>();
        activityLocatorParticipationDTCollection.add(new Object());
        clinicalDocumentContainer.setTheActivityLocatorParticipationDTCollection(activityLocatorParticipationDTCollection);
        assertEquals(activityLocatorParticipationDTCollection, clinicalDocumentContainer.getTheActivityLocatorParticipationDTCollection());

        Collection<Object> actIdDTCollection = new ArrayList<>();
        actIdDTCollection.add(new Object());
        clinicalDocumentContainer.setTheActIdDTCollection(actIdDTCollection);
        assertEquals(actIdDTCollection, clinicalDocumentContainer.getTheActIdDTCollection());

        Collection<Object> participationDTCollection = new ArrayList<>();
        participationDTCollection.add(new Object());
        clinicalDocumentContainer.setTheParticipationDTCollection(participationDTCollection);
        assertEquals(participationDTCollection, clinicalDocumentContainer.getTheParticipationDTCollection());

        Collection<Object> actRelationshipDTCollection = new ArrayList<>();
        actRelationshipDTCollection.add(new Object());
        clinicalDocumentContainer.setTheActRelationshipDTCollection(actRelationshipDTCollection);
        assertEquals(actRelationshipDTCollection, clinicalDocumentContainer.getTheActRelationshipDTCollection());
    }
}