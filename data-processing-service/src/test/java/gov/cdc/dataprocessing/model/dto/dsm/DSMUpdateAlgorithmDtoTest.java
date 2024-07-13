package gov.cdc.dataprocessing.model.dto.dsm;

import org.junit.jupiter.api.Test;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class DSMUpdateAlgorithmDtoTest {

    @Test
    void testGettersAndSetters() {
        DSMUpdateAlgorithmDto dto = new DSMUpdateAlgorithmDto();

        // Set values
        dto.setDsmUpdateAlgorithmUid(1L);
        dto.setConditionCd("ConditionCd");
        dto.setSendingSystemNm("SendingSystemNm");
        dto.setUpdateIndCd("UpdateIndCd");
        dto.setUpdateClosedBehaviour("UpdateClosedBehaviour");
        dto.setUpdateMultiClosedBehaviour("UpdateMultiClosedBehaviour");
        dto.setUpdateMultiOpenBehaviour("UpdateMultiOpenBehaviour");
        dto.setUpdateIgnoreList("UpdateIgnoreList");
        dto.setUpdateTimeframe("UpdateTimeframe");
        dto.setAdminComment("AdminComment");
        dto.setStatusCd("StatusCd");
        dto.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setAddUserId(2L);
        dto.setAddTime(new Timestamp(System.currentTimeMillis()));
        dto.setLastChgUserId(3L);
        dto.setLastChgTime(new Timestamp(System.currentTimeMillis()));
        dto.setDsmUpdateAlgorithmMapKey("DsmUpdateAlgorithmMapKey");
        dto.setJurisdictionCd("JurisdictionCd");
        dto.setProgAreaCd("ProgAreaCd");
        dto.setLocalId("LocalId");
        dto.setLastChgReasonCd("LastChgReasonCd");
        dto.setRecordStatusCd("RecordStatusCd");
        dto.setRecordStatusTime(new Timestamp(System.currentTimeMillis()));
        dto.setProgramJurisdictionOid(4L);
        dto.setSharedInd("SharedInd");
        dto.setVersionCtrlNbr(1);

        // Assert values
        assertEquals(1L, dto.getDsmUpdateAlgorithmUid());
        assertEquals("ConditionCd", dto.getConditionCd());
        assertEquals("SendingSystemNm", dto.getSendingSystemNm());
        assertEquals("UpdateIndCd", dto.getUpdateIndCd());
        assertEquals("UpdateClosedBehaviour", dto.getUpdateClosedBehaviour());
        assertEquals("UpdateMultiClosedBehaviour", dto.getUpdateMultiClosedBehaviour());
        assertEquals("UpdateMultiOpenBehaviour", dto.getUpdateMultiOpenBehaviour());
        assertEquals("UpdateIgnoreList", dto.getUpdateIgnoreList());
        assertEquals("UpdateTimeframe", dto.getUpdateTimeframe());
        assertEquals("AdminComment", dto.getAdminComment());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals(2L, dto.getAddUserId());
        assertNotNull(dto.getAddTime());
        assertEquals(3L, dto.getLastChgUserId());
        assertNotNull(dto.getLastChgTime());
        assertEquals("DsmUpdateAlgorithmMapKey", dto.getDsmUpdateAlgorithmMapKey());
        assertEquals("JurisdictionCd", dto.getJurisdictionCd());
        assertEquals("ProgAreaCd", dto.getProgAreaCd());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertNotNull(dto.getRecordStatusTime());
        assertEquals(4L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(1, dto.getVersionCtrlNbr());

        // Test overridden methods
        assertEquals("JurisdictionCd", dto.getJurisdictionCd());
        assertEquals("ProgAreaCd", dto.getProgAreaCd());
        assertEquals("LocalId", dto.getLocalId());
        assertEquals("LastChgReasonCd", dto.getLastChgReasonCd());
        assertEquals("RecordStatusCd", dto.getRecordStatusCd());
        assertEquals("StatusCd", dto.getStatusCd());
        assertNotNull(dto.getStatusTime());
        assertEquals("ConditionCd", dto.getConditionCd());
        assertNotNull(dto.getAddTime());
        assertEquals(4L, dto.getProgramJurisdictionOid());
        assertEquals("SharedInd", dto.getSharedInd());
        assertEquals(1, dto.getVersionCtrlNbr());
        assertEquals(1L, dto.getDsmUpdateAlgorithmUid());
        assertEquals(3L, dto.getLastChgUserId());
        assertNotNull(dto.getLastChgTime());
    }
}
