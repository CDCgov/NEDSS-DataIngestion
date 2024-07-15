package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonNameIdTest {

    @Test
    public void testGettersAndSetters() {
        PersonNameId id = new PersonNameId();
        id.setPersonUid(1L);
        id.setPersonNameSeq(2);

        assertEquals(1L, id.getPersonUid());
        assertEquals(2, id.getPersonNameSeq());
    }

    @Test
    public void testEqualsAndHashCode() {
        PersonNameId id1 = new PersonNameId();
        id1.setPersonUid(1L);
        id1.setPersonNameSeq(2);

        PersonNameId id2 = new PersonNameId();
        id2.setPersonUid(1L);
        id2.setPersonNameSeq(2);

        PersonNameId id3 = new PersonNameId();
        id3.setPersonUid(1L);
        id3.setPersonNameSeq(3);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
