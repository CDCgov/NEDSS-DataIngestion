package gov.cdc.dataprocessing.service.model.person;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonIdTest {

    @Test
    void testPersonIdSettersAndGetters() {
        PersonId personId = new PersonId();
        Long personIdValue = 1L;
        Long personParentIdValue = 2L;
        String localIdValue = "local123";

        Long revisionIdValue = 10L;
        Long revisionParentIdValue = 20L;
        String revisionLocalIdValue = "revisionLocal123";

        personId.setPersonId(personIdValue);
        personId.setPersonParentId(personParentIdValue);
        personId.setLocalId(localIdValue);
        personId.setRevisionId(revisionIdValue);
        personId.setRevisionParentId(revisionParentIdValue);
        personId.setRevisionLocalId(revisionLocalIdValue);

        assertEquals(personIdValue, personId.getPersonId());
        assertEquals(personParentIdValue, personId.getPersonParentId());
        assertEquals(localIdValue, personId.getLocalId());
        assertEquals(revisionIdValue, personId.getRevisionId());
        assertEquals(revisionParentIdValue, personId.getRevisionParentId());
        assertEquals(revisionLocalIdValue, personId.getRevisionLocalId());
    }

    @Test
    void testToString() {
        PersonId personId = new PersonId();
        personId.setPersonId(1L);
        personId.setPersonParentId(2L);
        personId.setLocalId("local123");
        personId.setRevisionId(10L);
        personId.setRevisionParentId(20L);
        personId.setRevisionLocalId("revisionLocal123");

        assertNotNull(personId.toString());
    }

    @Test
    void testHashCode() {
        PersonId personId1 = new PersonId();
        personId1.setPersonId(1L);
        personId1.setPersonParentId(2L);
        personId1.setLocalId("local123");
        personId1.setRevisionId(10L);
        personId1.setRevisionParentId(20L);
        personId1.setRevisionLocalId("revisionLocal123");

        PersonId personId2 = new PersonId();
        personId2.setPersonId(1L);
        personId2.setPersonParentId(2L);
        personId2.setLocalId("local123");
        personId2.setRevisionId(10L);
        personId2.setRevisionParentId(20L);
        personId2.setRevisionLocalId("revisionLocal123");

        assertNotEquals(personId1.hashCode(), personId2.hashCode());
    }

    @Test
    void testEquals() {
        PersonId personId1 = new PersonId();
        personId1.setPersonId(1L);
        personId1.setPersonParentId(2L);
        personId1.setLocalId("local123");
        personId1.setRevisionId(10L);
        personId1.setRevisionParentId(20L);
        personId1.setRevisionLocalId("revisionLocal123");

        PersonId personId2 = new PersonId();
        personId2.setPersonId(1L);
        personId2.setPersonParentId(2L);
        personId2.setLocalId("local123");
        personId2.setRevisionId(10L);
        personId2.setRevisionParentId(20L);
        personId2.setRevisionLocalId("revisionLocal123");

        assertNotEquals(personId1, personId2);
    }

    @Test
    void testNotEquals() {
        PersonId personId1 = new PersonId();
        personId1.setPersonId(1L);
        personId1.setPersonParentId(2L);
        personId1.setLocalId("local123");
        personId1.setRevisionId(10L);
        personId1.setRevisionParentId(20L);
        personId1.setRevisionLocalId("revisionLocal123");

        PersonId personId2 = new PersonId();
        personId2.setPersonId(3L);  // different value
        personId2.setPersonParentId(2L);
        personId2.setLocalId("local123");
        personId2.setRevisionId(10L);
        personId2.setRevisionParentId(20L);
        personId2.setRevisionLocalId("revisionLocal123");

        assertNotEquals(personId1, personId2);
    }
}
