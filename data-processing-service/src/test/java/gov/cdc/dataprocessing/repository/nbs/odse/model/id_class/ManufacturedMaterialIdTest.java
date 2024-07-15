package gov.cdc.dataprocessing.repository.nbs.odse.model.id_class;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManufacturedMaterialIdTest {

    @Test
    public void testGettersAndSetters() {
        ManufacturedMaterialId id = new ManufacturedMaterialId();
        id.setMaterialUid(1L);
        id.setManufacturedMaterialSeq(2);

        assertEquals(1L, id.getMaterialUid());
        assertEquals(2, id.getManufacturedMaterialSeq());
    }

    @Test
    public void testEqualsAndHashCode() {
        ManufacturedMaterialId id1 = new ManufacturedMaterialId();
        id1.setMaterialUid(1L);
        id1.setManufacturedMaterialSeq(2);

        ManufacturedMaterialId id2 = new ManufacturedMaterialId();
        id2.setMaterialUid(1L);
        id2.setManufacturedMaterialSeq(2);

        ManufacturedMaterialId id3 = new ManufacturedMaterialId();
        id3.setMaterialUid(1L);
        id3.setManufacturedMaterialSeq(3);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }
}
