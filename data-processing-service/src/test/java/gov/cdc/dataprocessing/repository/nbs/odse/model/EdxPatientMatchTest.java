package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.matching.EdxPatientMatchDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxPatientMatch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EdxPatientMatchTest {

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        EdxPatientMatch edxPatientMatch = new EdxPatientMatch();

        // Assert
        assertNull(edxPatientMatch.getEdxPatientMatchUid());
        assertNull(edxPatientMatch.getPatientUid());
        assertNull(edxPatientMatch.getMatchString());
        assertNull(edxPatientMatch.getTypeCd());
        assertNull(edxPatientMatch.getMatchStringHashcode());
    }

    @Test
    void testDtoConstructor() {
        // Arrange
        Long patientUid = 2L;
        String matchString = "testMatchString";
        String typeCd = "testTypeCd";
        Long matchStringHashcode = 123456789L;

        EdxPatientMatchDto dto = new EdxPatientMatchDto();
        dto.setPatientUid(patientUid);
        dto.setMatchString(matchString);
        dto.setTypeCd(typeCd);
        dto.setMatchStringHashCode(matchStringHashcode);

        // Act
        EdxPatientMatch edxPatientMatch = new EdxPatientMatch(dto);

        // Assert
        assertEquals(patientUid, edxPatientMatch.getPatientUid());
        assertEquals(matchString, edxPatientMatch.getMatchString());
        assertEquals(typeCd, edxPatientMatch.getTypeCd());
        assertEquals(matchStringHashcode, edxPatientMatch.getMatchStringHashcode());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        EdxPatientMatch edxPatientMatch = new EdxPatientMatch();

        Long edxPatientMatchUid = 1L;
        Long patientUid = 2L;
        String matchString = "testMatchString";
        String typeCd = "testTypeCd";
        Long matchStringHashcode = 123456789L;

        // Act
        edxPatientMatch.setEdxPatientMatchUid(edxPatientMatchUid);
        edxPatientMatch.setPatientUid(patientUid);
        edxPatientMatch.setMatchString(matchString);
        edxPatientMatch.setTypeCd(typeCd);
        edxPatientMatch.setMatchStringHashcode(matchStringHashcode);

        // Assert
        assertEquals(edxPatientMatchUid, edxPatientMatch.getEdxPatientMatchUid());
        assertEquals(patientUid, edxPatientMatch.getPatientUid());
        assertEquals(matchString, edxPatientMatch.getMatchString());
        assertEquals(typeCd, edxPatientMatch.getTypeCd());
        assertEquals(matchStringHashcode, edxPatientMatch.getMatchStringHashcode());
    }
}
