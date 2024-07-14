package gov.cdc.dataprocessing.model.dto.material;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MaterialDtoTest {

    @Test
    void testGettersAndSetters() {
        MaterialDto materialDto = new MaterialDto();

        materialDto.setMaterialUid(1L);

        assertNotNull(materialDto.getUid());
        assertNotNull(materialDto.getSuperclass());
    }
}
