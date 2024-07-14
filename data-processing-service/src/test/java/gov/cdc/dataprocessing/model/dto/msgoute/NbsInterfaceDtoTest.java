package gov.cdc.dataprocessing.model.dto.msgoute;

import org.junit.jupiter.api.Test;

import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NbsInterfaceDtoTest {

    @Test
    public void testGettersAndSetters() throws SQLException {
        // Create an instance of NbsInterfaceDto
        NbsInterfaceDto dto = new NbsInterfaceDto();

        // Set values using setters
        dto.setNbsInterfaceUid(1L);
        Blob payload = new javax.sql.rowset.serial.SerialBlob(new byte[]{});
        dto.setPayload(payload);
        dto.setImpExpIndCd("Import");
        dto.setRecordStatusCd("Active");
        Timestamp recordStatusTime = Timestamp.valueOf("2023-01-01 12:00:00");
        dto.setRecordStatusTime(recordStatusTime);
        dto.setSendingSystemNm("System A");
        Timestamp addTime = Timestamp.valueOf("2023-01-02 12:00:00");
        dto.setAddTime(addTime);
        dto.setReceivingSystemNm("System B");
        dto.setNotificationUid(2L);
        dto.setNbsDocumentUid(3L);
        dto.setXmlPayLoadContent("<xml>content</xml>");
        dto.setSystemNm("System C");
        dto.setDocTypeCd("Type A");
        dto.setCdaPayload("<CDA>payload</CDA>");
        dto.setObservationUid(4L);
        dto.setOriginalPayload("Original Payload");
        dto.setOriginalDocTypeCd("Original Type");

        // Assert values using getters
        assertEquals(1L, dto.getNbsInterfaceUid());
        assertEquals(payload, dto.getPayload());
        assertEquals("Import", dto.getImpExpIndCd());
        assertEquals("Active", dto.getRecordStatusCd());
        assertEquals(recordStatusTime, dto.getRecordStatusTime());
        assertEquals("System A", dto.getSendingSystemNm());
        assertEquals(addTime, dto.getAddTime());
        assertEquals("System B", dto.getReceivingSystemNm());
        assertEquals(2L, dto.getNotificationUid());
        assertEquals(3L, dto.getNbsDocumentUid());
        assertEquals("<xml>content</xml>", dto.getXmlPayLoadContent());
        assertEquals("System C", dto.getSystemNm());
        assertEquals("Type A", dto.getDocTypeCd());
        assertEquals("<CDA>payload</CDA>", dto.getCdaPayload());
        assertEquals(4L, dto.getObservationUid());
        assertEquals("Original Payload", dto.getOriginalPayload());
        assertEquals("Original Type", dto.getOriginalDocTypeCd());
    }
}
