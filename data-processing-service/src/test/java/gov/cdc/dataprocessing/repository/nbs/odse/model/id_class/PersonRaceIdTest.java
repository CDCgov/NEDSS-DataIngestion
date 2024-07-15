package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonRaceIdTest {

    @Test
    public void testGettersAndSetters() {
        PersonRaceId id = new PersonRaceId();
        id.setPersonUid(1L);
        id.setRaceCd("raceCd");

        assertEquals(1L, id.getPersonUid());
        assertEquals("raceCd", id.getRaceCd());
    }

    @Test
    public void testEqualsAndHashCode() {
        PersonRaceId id1 = new PersonRaceId();
        id1.setPersonUid(1L);
        id1.setRaceCd("raceCd");

        PersonRaceId id2 = new PersonRaceId();
        id2.setPersonUid(1L);
        id2.setRaceCd("raceCd");

        PersonRaceId id3 = new PersonRaceId();
        id3.setPersonUid(1L);
        id3.setRaceCd("differentRaceCd");

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
