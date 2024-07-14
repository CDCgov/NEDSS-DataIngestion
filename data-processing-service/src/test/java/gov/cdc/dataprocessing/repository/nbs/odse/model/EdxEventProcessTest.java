package gov.cdc.dataprocessing.repository.nbs.odse.model.edx;

import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EdxEventProcessTest {

    private EdxEventProcess edxEventProcess;
    private Timestamp timestamp;

    @BeforeEach
    void setUp() {
        edxEventProcess = new EdxEventProcess();
        timestamp = new Timestamp(System.currentTimeMillis());
    }

    @Test
    void testGettersAndSetters() {
        // Set values
        edxEventProcess.setEdxEventProcessUid(1L);
        edxEventProcess.setNbsDocumentUid(2L);
        edxEventProcess.setNbsEventUid(3L);
        edxEventProcess.setSourceEventId("sourceEventId");
        edxEventProcess.setDocEventTypeCd("docEventTypeCd");
        edxEventProcess.setAddUserId(4L);
        edxEventProcess.setAddTime(timestamp);
        edxEventProcess.setParsedInd("Y");
        edxEventProcess.setEdxDocumentUid(5L);

        // Verify values
        assertEquals(1L, edxEventProcess.getEdxEventProcessUid());
        assertEquals(2L, edxEventProcess.getNbsDocumentUid());
        assertEquals(3L, edxEventProcess.getNbsEventUid());
        assertEquals("sourceEventId", edxEventProcess.getSourceEventId());
        assertEquals("docEventTypeCd", edxEventProcess.getDocEventTypeCd());
        assertEquals(4L, edxEventProcess.getAddUserId());
        assertEquals(timestamp, edxEventProcess.getAddTime());
        assertEquals("Y", edxEventProcess.getParsedInd());
        assertEquals(5L, edxEventProcess.getEdxDocumentUid());
    }

    @Test
    void testDefaultValues() {
        // Check default values
        assertNull(edxEventProcess.getEdxEventProcessUid());
        assertNull(edxEventProcess.getNbsDocumentUid());
        assertNull(edxEventProcess.getNbsEventUid());
        assertNull(edxEventProcess.getSourceEventId());
        assertNull(edxEventProcess.getDocEventTypeCd());
        assertNull(edxEventProcess.getAddUserId());
        assertNull(edxEventProcess.getAddTime());
        assertNull(edxEventProcess.getParsedInd());
        assertNull(edxEventProcess.getEdxDocumentUid());
    }

    @Test
    void testConstructorWithDto() {
        EDXEventProcessDto dto = new EDXEventProcessDto();
        dto.setNbsDocumentUid(2L);
        dto.setNbsEventUid(3L);
        dto.setSourceEventId("sourceEventId");
        dto.setDocEventTypeCd("docEventTypeCd");
        dto.setAddUserId(4L);
        dto.setAddTime(timestamp);
        dto.setParsedInd("Y");
        dto.setEdxDocumentUid(5L);

        EdxEventProcess edxEventProcessFromDto = new EdxEventProcess(dto);

        assertNull(edxEventProcessFromDto.getEdxEventProcessUid()); // Assuming it's auto-generated
        assertEquals(2L, edxEventProcessFromDto.getNbsDocumentUid());
        assertEquals(3L, edxEventProcessFromDto.getNbsEventUid());
        assertEquals("sourceEventId", edxEventProcessFromDto.getSourceEventId());
        assertEquals("docEventTypeCd", edxEventProcessFromDto.getDocEventTypeCd());
        assertEquals(4L, edxEventProcessFromDto.getAddUserId());
        assertEquals(timestamp, edxEventProcessFromDto.getAddTime());
        assertEquals("Y", edxEventProcessFromDto.getParsedInd());
        assertEquals(5L, edxEventProcessFromDto.getEdxDocumentUid());
    }
}
