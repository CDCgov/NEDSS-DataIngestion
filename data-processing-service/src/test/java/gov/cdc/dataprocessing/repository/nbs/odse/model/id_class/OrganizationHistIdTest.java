package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizationHistIdTest {

    @Test
    public void testGettersAndSetters() {
        OrganizationHistId id = new OrganizationHistId();
        id.setOrganizationUid(1L);
        id.setVersionCtrlNbr(2);

        assertEquals(1L, id.getOrganizationUid());
        assertEquals(2, id.getVersionCtrlNbr());
    }

    @Test
    public void testEqualsAndHashCode() {
        OrganizationHistId id1 = new OrganizationHistId();
        id1.setOrganizationUid(1L);
        id1.setVersionCtrlNbr(2);

        OrganizationHistId id2 = new OrganizationHistId();
        id2.setOrganizationUid(1L);
        id2.setVersionCtrlNbr(2);

        OrganizationHistId id3 = new OrganizationHistId();
        id3.setOrganizationUid(1L);
        id3.setVersionCtrlNbr(3);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
