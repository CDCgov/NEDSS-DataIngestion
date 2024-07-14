package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.model.dto.material.MaterialDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NbsAnswerDtoTest {

    @Test
    void testGettersAndSetters() {
        NbsAnswerDto dto = new NbsAnswerDto();

        dto.setAnswerLargeTxt(null);

        assertNull(dto.getAnswerLargeTxt());
    }
}
