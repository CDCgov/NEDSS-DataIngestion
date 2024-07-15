package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizationNameHistIdTest {

    @Test
    public void testGettersAndSetters() {
        OrganizationNameHistId id = new OrganizationNameHistId();
        id.setOrganizationUid(1L);
        id.setOrganizationNameSeq(2);
        id.setVersionCtrlNbr(3);

        assertEquals(1L, id.getOrganizationUid());
        assertEquals(2, id.getOrganizationNameSeq());
        assertEquals(3, id.getVersionCtrlNbr());
    }

    @Test
    public void testEqualsAndHashCode() {
        OrganizationNameHistId id1 = new OrganizationNameHistId();
        id1.setOrganizationUid(1L);
        id1.setOrganizationNameSeq(2);
        id1.setVersionCtrlNbr(3);

        OrganizationNameHistId id2 = new OrganizationNameHistId();
        id2.setOrganizationUid(1L);
        id2.setOrganizationNameSeq(2);
        id2.setVersionCtrlNbr(3);

        OrganizationNameHistId id3 = new OrganizationNameHistId();
        id3.setOrganizationUid(1L);
        id3.setOrganizationNameSeq(2);
        id3.setVersionCtrlNbr(4);
        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
