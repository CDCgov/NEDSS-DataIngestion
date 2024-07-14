package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonEthnicGroupIdTest {

    @Test
    public void testGettersAndSetters() {
        PersonEthnicGroupId id = new PersonEthnicGroupId();
        id.setPersonUid(1L);
        id.setEthnicGroupCd("testGroup");

        assertEquals(1L, id.getPersonUid());
        assertEquals("testGroup", id.getEthnicGroupCd());
    }

    @Test
    public void testEqualsAndHashCode() {
        PersonEthnicGroupId id1 = new PersonEthnicGroupId();
        id1.setPersonUid(1L);
        id1.setEthnicGroupCd("testGroup");

        PersonEthnicGroupId id2 = new PersonEthnicGroupId();
        id2.setPersonUid(1L);
        id2.setEthnicGroupCd("testGroup");

        PersonEthnicGroupId id3 = new PersonEthnicGroupId();
        id3.setPersonUid(1L);
        id3.setEthnicGroupCd("differentGroup");

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
