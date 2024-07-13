package gov.cdc.dataprocessing.model.dto.dsm;


import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DSMUpdateAlgorithmDtoTest {

    @Test
    void testGettersAndSetters() {
        DSMUpdateAlgorithmDto dto = new DSMUpdateAlgorithmDto();

        Long dsmUpdateAlgorithmUid = 1L;
        String conditionCd = "Condition Cd";
        String sendingSystemNm = "Sending System Nm";
        String updateIndCd = "Update Ind Cd";
        String updateClosedBehaviour = "Update Closed Behaviour";
        String updateMultiClosedBehaviour = "Update Multi Closed Behaviour";
        String updateMultiOpenBehaviour = "Update Multi Open Behaviour";
        String updateIgnoreList = "Update Ignore List";
        String updateTimeframe = "Update Timeframe";
        String adminComment = "Admin Comment";
        String statusCd = "Status Cd";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        Long addUserId = 2L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long lastChgUserId = 3L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        String dsmUpdateAlgorithmMapKey = "DSM Update Algorithm Map Key";

        dto.setDsmUpdateAlgorithmUid(dsmUpdateAlgorithmUid);
        dto.setConditionCd(conditionCd);
        dto.setSendingSystemNm(sendingSystemNm);
        dto.setUpdateIndCd(updateIndCd);
        dto.setUpdateClosedBehaviour(updateClosedBehaviour);
        dto.setUpdateMultiClosedBehaviour(updateMultiClosedBehaviour);
        dto.setUpdateMultiOpenBehaviour(updateMultiOpenBehaviour);
        dto.setUpdateIgnoreList(updateIgnoreList);
        dto.setUpdateTimeframe(updateTimeframe);
        dto.setAdminComment(adminComment);
        dto.setStatusCd(statusCd);
        dto.setStatusTime(statusTime);
        dto.setAddUserId(addUserId);
        dto.setAddTime(addTime);
        dto.setLastChgUserId(lastChgUserId);
        dto.setLastChgTime(lastChgTime);
        dto.setDsmUpdateAlgorithmMapKey(dsmUpdateAlgorithmMapKey);

        assertEquals(dsmUpdateAlgorithmUid, dto.getDsmUpdateAlgorithmUid());
        assertEquals(conditionCd, dto.getConditionCd());
        assertEquals(sendingSystemNm, dto.getSendingSystemNm());
        assertEquals(updateIndCd, dto.getUpdateIndCd());
        assertEquals(updateClosedBehaviour, dto.getUpdateClosedBehaviour());
        assertEquals(updateMultiClosedBehaviour, dto.getUpdateMultiClosedBehaviour());
        assertEquals(updateMultiOpenBehaviour, dto.getUpdateMultiOpenBehaviour());
        assertEquals(updateIgnoreList, dto.getUpdateIgnoreList());
        assertEquals(updateTimeframe, dto.getUpdateTimeframe());
        assertEquals(adminComment, dto.getAdminComment());
        assertEquals(statusCd, dto.getStatusCd());
        assertEquals(statusTime, dto.getStatusTime());
        assertEquals(addUserId, dto.getAddUserId());
        assertEquals(addTime, dto.getAddTime());
        assertEquals(lastChgUserId, dto.getLastChgUserId());
        assertEquals(lastChgTime, dto.getLastChgTime());
        assertEquals(dsmUpdateAlgorithmMapKey, dto.getDsmUpdateAlgorithmMapKey());
    }
}
