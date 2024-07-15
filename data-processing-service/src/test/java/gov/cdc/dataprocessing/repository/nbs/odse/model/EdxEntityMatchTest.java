package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxEntityMatch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EdxEntityMatchTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        EdxEntityMatch edxEntityMatch = new EdxEntityMatch();

        // Assert
        assertNull(edxEntityMatch.getEdxEntityMatchUid());
        assertNull(edxEntityMatch.getEntityUid());
        assertNull(edxEntityMatch.getMatchString());
        assertNull(edxEntityMatch.getTypeCd());
        assertNull(edxEntityMatch.getMatchStringHashcode());
    }

    @Test
    void testDtoConstructor() {
        // Arrange
        Long edxEntityMatchUid = 1L;
        Long entityUid = 2L;
        String matchString = "testMatchString";
        String typeCd = "testTypeCd";
        Long matchStringHashcode = 123456789L;

        EdxEntityMatchDto dto = new EdxEntityMatchDto();
        dto.setEdxEntityMatchUid(edxEntityMatchUid);
        dto.setEntityUid(entityUid);
        dto.setMatchString(matchString);
        dto.setTypeCd(typeCd);
        dto.setMatchStringHashCode(matchStringHashcode);

        // Act
        EdxEntityMatch edxEntityMatch = new EdxEntityMatch(dto);

        // Assert
        assertEquals(edxEntityMatchUid, edxEntityMatch.getEdxEntityMatchUid());
        assertEquals(entityUid, edxEntityMatch.getEntityUid());
        assertEquals(matchString, edxEntityMatch.getMatchString());
        assertEquals(typeCd, edxEntityMatch.getTypeCd());
        assertEquals(matchStringHashcode, edxEntityMatch.getMatchStringHashcode());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        EdxEntityMatch edxEntityMatch = new EdxEntityMatch();

        Long edxEntityMatchUid = 1L;
        Long entityUid = 2L;
        String matchString = "testMatchString";
        String typeCd = "testTypeCd";
        Long matchStringHashcode = 123456789L;

        // Act
        edxEntityMatch.setEdxEntityMatchUid(edxEntityMatchUid);
        edxEntityMatch.setEntityUid(entityUid);
        edxEntityMatch.setMatchString(matchString);
        edxEntityMatch.setTypeCd(typeCd);
        edxEntityMatch.setMatchStringHashcode(matchStringHashcode);

        // Assert
        assertEquals(edxEntityMatchUid, edxEntityMatch.getEdxEntityMatchUid());
        assertEquals(entityUid, edxEntityMatch.getEntityUid());
        assertEquals(matchString, edxEntityMatch.getMatchString());
        assertEquals(typeCd, edxEntityMatch.getTypeCd());
        assertEquals(matchStringHashcode, edxEntityMatch.getMatchStringHashcode());
    }
}
