package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.EdxActivityLogContainer;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EdxActivityLogContainerTest {

    @Test
    void testGetAndSetEdxActivityLogDto() {
        EdxActivityLogContainer container = new EdxActivityLogContainer();
        EDXActivityLogDto edxActivityLogDto = new EDXActivityLogDto();

        // Set some values to edxActivityLogDto for testing
        edxActivityLogDto.setEdxActivityLogUid(12345L);

        container.setEdxActivityLogDto(edxActivityLogDto);

        EDXActivityLogDto retrievedDto = container.getEdxActivityLogDto();
        assertNotNull(retrievedDto, "The edxActivityLogDto should not be null");
    }

    @Test
    void testDefaultEdxActivityLogDto() {
        EdxActivityLogContainer container = new EdxActivityLogContainer();

        EDXActivityLogDto defaultDto = container.getEdxActivityLogDto();
        assertNotNull(defaultDto, "The default edxActivityLogDto should not be null");
    }
}