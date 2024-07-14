package gov.cdc.dataprocessing.repository.nbs.odse.model;


import gov.cdc.dataprocessing.model.dto.log.EDXActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.EdxActivityLog;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EdxActivityLogTest {

    @Test
    void testEDXActivityLogDtoConstructor() {
        // Arrange
        EDXActivityLogDto dto = new EDXActivityLogDto();
        dto.setSourceUid(1L);
        dto.setTargetUid(2L);
        dto.setDocType("docType");
        dto.setRecordStatusCd("recordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setExceptionTxt("exceptionTxt");
        dto.setImpExpIndCd("impExpIndCd");
        dto.setSourceTypeCd("sourceTypeCd");
        dto.setTargetTypeCd("targetTypeCd");
        dto.setBusinessObjLocalId("businessObjLocalid");
        dto.setDocName("docNm");
        dto.setSrcName("sourceNm");
        dto.setAlgorithmAction("algorithmAction");
        dto.setAlgorithmName("algorithmName");
        dto.setMessageId("messageId");
        dto.setEntityNm("entityNm");
        dto.setAccessionNbr("accessionNbr");

        // Act
        EdxActivityLog log = new EdxActivityLog(dto);

        // Assert
        assertEquals(dto.getSourceUid(), log.getSourceUid());
        assertEquals(dto.getTargetUid(), log.getTargetUid());
        assertEquals(dto.getDocType(), log.getDocType());
        assertEquals(dto.getRecordStatusCd(), log.getRecordStatusCd());
        assertEquals(dto.getRecordStatusTime(), log.getRecordStatusTime());
        assertEquals(dto.getExceptionTxt(), log.getExceptionTxt());
        assertEquals(dto.getImpExpIndCd(), log.getImpExpIndCd());
        assertEquals(dto.getSourceTypeCd(), log.getSourceTypeCd());
        assertEquals(dto.getTargetTypeCd(), log.getTargetTypeCd());
        assertEquals(dto.getBusinessObjLocalId(), log.getBusinessObjLocalid());
        assertEquals(dto.getDocName(), log.getDocNm());
        assertEquals(dto.getSrcName(), log.getSourceNm());
        assertEquals(dto.getAlgorithmAction(), log.getAlgorithmAction());
        assertEquals(dto.getAlgorithmName(), log.getAlgorithmName());
        assertEquals(dto.getMessageId(), log.getMessageId());
        assertEquals(dto.getEntityNm(), log.getEntityNm());
        assertEquals(dto.getAccessionNbr(), log.getAccessionNbr());
    }

    @Test
    void testDefaultConstructor() {
        // Arrange & Act
        EdxActivityLog log = new EdxActivityLog();

        // Assert
        assertNull(log.getId());
        assertNull(log.getSourceUid());
        assertNull(log.getTargetUid());
        assertNull(log.getDocType());
        assertNull(log.getRecordStatusCd());
        assertNull(log.getRecordStatusTime());
        assertNull(log.getExceptionTxt());
        assertNull(log.getImpExpIndCd());
        assertNull(log.getSourceTypeCd());
        assertNull(log.getTargetTypeCd());
        assertNull(log.getBusinessObjLocalid());
        assertNull(log.getDocNm());
        assertNull(log.getSourceNm());
        assertNull(log.getAlgorithmAction());
        assertNull(log.getAlgorithmName());
        assertNull(log.getMessageId());
        assertNull(log.getEntityNm());
        assertNull(log.getAccessionNbr());
    }
}
