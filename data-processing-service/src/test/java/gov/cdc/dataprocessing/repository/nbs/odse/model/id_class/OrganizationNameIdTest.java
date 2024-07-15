package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrganizationNameIdTest {

    @Test
    public void testGettersAndSetters() {
        OrganizationNameId id = new OrganizationNameId();
        id.setOrganizationUid(1L);
        id.setOrganizationNameSeq(2);

        assertEquals(1L, id.getOrganizationUid());
        assertEquals(2, id.getOrganizationNameSeq());
    }

    @Test
    public void testEqualsAndHashCode() {
        OrganizationNameId id1 = new OrganizationNameId();
        id1.setOrganizationUid(1L);
        id1.setOrganizationNameSeq(2);

        OrganizationNameId id2 = new OrganizationNameId();
        id2.setOrganizationUid(1L);
        id2.setOrganizationNameSeq(2);

        OrganizationNameId id3 = new OrganizationNameId();
        id3.setOrganizationUid(1L);
        id3.setOrganizationNameSeq(3);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
