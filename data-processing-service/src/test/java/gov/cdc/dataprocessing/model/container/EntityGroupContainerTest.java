package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.EntityGroupContainer;
import gov.cdc.dataprocessing.model.dto.phc.EntityGroupDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityGroupContainerTest {

    @Test
    void testGetAndSetEntityGroupDto() {
        EntityGroupContainer container = new EntityGroupContainer();
        EntityGroupDto entityGroupDto = new EntityGroupDto();

        // Set some values to entityGroupDto for testing
        entityGroupDto.setEntityGroupUid(12345L);

        container.setTheEntityGroupDT(entityGroupDto);

        EntityGroupDto retrievedDto = container.getTheEntityGroupDT();
        assertNotNull(retrievedDto, "The entityGroupDto should not be null");
        assertEquals(entityGroupDto, retrievedDto, "The retrieved entityGroupDto should match the set one");
    }

    @Test
    void testDefaultEntityGroupDto() {
        EntityGroupContainer container = new EntityGroupContainer();

        EntityGroupDto defaultDto = container.getTheEntityGroupDT();
        assertNotNull(defaultDto, "The default entityGroupDto should not be null");
    }

    @Test
    void testSetAndGetCollections() {
        EntityGroupContainer container = new EntityGroupContainer();

        Collection<Object> entityLocatorParticipationDTCollection = new ArrayList<>();
        entityLocatorParticipationDTCollection.add("LocatorParticipation1");
        container.setTheEntityLocatorParticipationDTCollection(entityLocatorParticipationDTCollection);
        assertEquals(entityLocatorParticipationDTCollection, container.getTheEntityLocatorParticipationDTCollection(), "The entityLocatorParticipationDTCollection should match the set one");

        Collection<Object> entityIdDTCollection = new ArrayList<>();
        entityIdDTCollection.add("EntityId1");
        container.setTheEntityIdDTCollection(entityIdDTCollection);
        assertEquals(entityIdDTCollection, container.getTheEntityIdDTCollection(), "The entityIdDTCollection should match the set one");

        Collection<Object> participationDTCollection = new ArrayList<>();
        participationDTCollection.add("Participation1");
        container.setTheParticipationDTCollection(participationDTCollection);
        assertEquals(participationDTCollection, container.getTheParticipationDTCollection(), "The participationDTCollection should match the set one");

        Collection<Object> roleDTCollection = new ArrayList<>();
        roleDTCollection.add("Role1");
        container.setTheRoleDTCollection(roleDTCollection);
        assertEquals(roleDTCollection, container.getTheRoleDTCollection(), "The roleDTCollection should match the set one");
    }

    @Test
    void testDefaultCollections() {
        EntityGroupContainer container = new EntityGroupContainer();

        assertTrue(container.getTheEntityLocatorParticipationDTCollection() == null || container.getTheEntityLocatorParticipationDTCollection().isEmpty(), "The default entityLocatorParticipationDTCollection should be null or empty");
        assertTrue(container.getTheEntityIdDTCollection() == null || container.getTheEntityIdDTCollection().isEmpty(), "The default entityIdDTCollection should be null or empty");
        assertTrue(container.getTheParticipationDTCollection() == null || container.getTheParticipationDTCollection().isEmpty(), "The default participationDTCollection should be null or empty");
        assertTrue(container.getTheRoleDTCollection() == null || container.getTheRoleDTCollection().isEmpty(), "The default roleDTCollection should be null or empty");
    }
}