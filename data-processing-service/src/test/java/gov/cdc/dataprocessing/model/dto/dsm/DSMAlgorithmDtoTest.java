package gov.cdc.dataprocessing.model.dto.dsm;


import gov.cdc.dataprocessing.repository.nbs.odse.model.dsm.DsmAlgorithm;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DSMAlgorithmDtoTest {

    @Test
    void testGettersAndSetters() {
        DSMAlgorithmDto dto = new DSMAlgorithmDto();

        Long dsmAlgorithmUid = 1L;
        String algorithmNm = "Algorithm Name";
        String eventType = "Event Type";
        String conditionList = "Condition List";
        String resultedTestList = "Resulted Test List";
        String frequency = "Frequency";
        String applyTo = "Apply To";
        String sendingSystemList = "Sending System List";
        String reportingSystemList = "Reporting System List";
        String eventAction = "Event Action";
        String algorithmPayload = "Algorithm Payload";
        String adminComment = "Admin Comment";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        String statusCd = "Status Cd";
        Long lastChgUserId = 2L;
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());

        dto.setDsmAlgorithmUid(dsmAlgorithmUid);
        dto.setAlgorithmNm(algorithmNm);
        dto.setEventType(eventType);
        dto.setConditionList(conditionList);
        dto.setResultedTestList(resultedTestList);
        dto.setFrequency(frequency);
        dto.setApplyTo(applyTo);
        dto.setSendingSystemList(sendingSystemList);
        dto.setReportingSystemList(reportingSystemList);
        dto.setEventAction(eventAction);
        dto.setAlgorithmPayload(algorithmPayload);
        dto.setAdminComment(adminComment);
        dto.setStatusTime(statusTime);
        dto.setStatusCd(statusCd);
        dto.setLastChgUserId(lastChgUserId);
        dto.setLastChgTime(lastChgTime);

        assertEquals(dsmAlgorithmUid, dto.getDsmAlgorithmUid());
        assertEquals(algorithmNm, dto.getAlgorithmNm());
        assertEquals(eventType, dto.getEventType());
        assertEquals(conditionList, dto.getConditionList());
        assertEquals(resultedTestList, dto.getResultedTestList());
        assertEquals(frequency, dto.getFrequency());
        assertEquals(applyTo, dto.getApplyTo());
        assertEquals(sendingSystemList, dto.getSendingSystemList());
        assertEquals(reportingSystemList, dto.getReportingSystemList());
        assertEquals(eventAction, dto.getEventAction());
        assertEquals(algorithmPayload, dto.getAlgorithmPayload());
        assertEquals(adminComment, dto.getAdminComment());
        assertEquals(statusTime, dto.getStatusTime());
        assertEquals(statusCd, dto.getStatusCd());
        assertEquals(lastChgUserId, dto.getLastChgUserId());
        assertEquals(lastChgTime, dto.getLastChgTime());
    }

    @Test
    void testConstructor() {
        DsmAlgorithm dsmAlgorithm = new DsmAlgorithm();
        dsmAlgorithm.setDsmAlgorithmUid(1L);
        dsmAlgorithm.setAlgorithmNm("Algorithm Name");
        dsmAlgorithm.setEventType("Event Type");
        dsmAlgorithm.setConditionList("Condition List");
        dsmAlgorithm.setResultedTestList("Resulted Test List");
        dsmAlgorithm.setFrequency("Frequency");
        dsmAlgorithm.setApplyTo("Apply To");
        dsmAlgorithm.setSendingSystemList("Sending System List");
        dsmAlgorithm.setReportingSystemList("Reporting System List");
        dsmAlgorithm.setEventAction("Event Action");
        dsmAlgorithm.setAlgorithmPayload("Algorithm Payload");
        dsmAlgorithm.setAdminComment("Admin Comment");
        dsmAlgorithm.setStatusCd("Status Cd");
        dsmAlgorithm.setStatusTime(new Timestamp(System.currentTimeMillis()));
        dsmAlgorithm.setLastChgUserId(2L);
        dsmAlgorithm.setLastChgTime(new Timestamp(System.currentTimeMillis()));

        DSMAlgorithmDto dto = new DSMAlgorithmDto(dsmAlgorithm);

        assertEquals(dsmAlgorithm.getDsmAlgorithmUid(), dto.getDsmAlgorithmUid());
        assertEquals(dsmAlgorithm.getAlgorithmNm(), dto.getAlgorithmNm());
        assertEquals(dsmAlgorithm.getEventType(), dto.getEventType());
        assertEquals(dsmAlgorithm.getConditionList(), dto.getConditionList());
        assertEquals(dsmAlgorithm.getResultedTestList(), dto.getResultedTestList());
        assertEquals(dsmAlgorithm.getFrequency(), dto.getFrequency());
        assertEquals(dsmAlgorithm.getApplyTo(), dto.getApplyTo());
        assertEquals(dsmAlgorithm.getSendingSystemList(), dto.getSendingSystemList());
        assertEquals(dsmAlgorithm.getReportingSystemList(), dto.getReportingSystemList());
        assertEquals(dsmAlgorithm.getEventAction(), dto.getEventAction());
        assertEquals(dsmAlgorithm.getAlgorithmPayload(), dto.getAlgorithmPayload());
        assertEquals(dsmAlgorithm.getAdminComment(), dto.getAdminComment());
        assertEquals(dsmAlgorithm.getStatusCd(), dto.getStatusCd());
        assertEquals(dsmAlgorithm.getStatusTime(), dto.getStatusTime());
        assertEquals(dsmAlgorithm.getLastChgUserId(), dto.getLastChgUserId());
        assertEquals(dsmAlgorithm.getLastChgTime(), dto.getLastChgTime());
    }
}
