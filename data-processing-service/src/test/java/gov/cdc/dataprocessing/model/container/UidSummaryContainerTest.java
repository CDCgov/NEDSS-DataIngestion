package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.UidSummaryContainer;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UidSummaryContainerTest {

    @Test
    void testGettersAndSetters() {
        UidSummaryContainer container = new UidSummaryContainer();

        Long uid = 1L;
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        Long linkingUid = 2L;
        String uniqueMapKey = "uniqueMapKey";
        Timestamp statusTime = new Timestamp(System.currentTimeMillis() + 1000);
        String addReasonCd = "addReasonCd";

        container.setUid(uid);
        container.setAddTime(addTime);
        container.setLinkingUid(linkingUid);
        container.setUniqueMapKey(uniqueMapKey);
        container.setStatusTime(statusTime);
        container.setAddReasonCd(addReasonCd);

        assertEquals(uid, container.getUid());
        assertEquals(addTime, container.getAddTime());
        assertEquals(linkingUid, container.getLinkingUid());
        assertEquals(uniqueMapKey, container.getUniqueMapKey());
        assertEquals(statusTime, container.getStatusTime());
        assertEquals(addReasonCd, container.getAddReasonCd());
    }


    @Test
    void testGetLastChgUserId() {
        UidSummaryContainer container = new UidSummaryContainer();
        Long lastChgUserId = 123L;
        container.setLastChgUserId(lastChgUserId);
        assertEquals(lastChgUserId, container.getLastChgUserId());
    }

    @Test
    void testSetLastChgUserId() {
        UidSummaryContainer container = new UidSummaryContainer();
        Long lastChgUserId = 123L;
        container.setLastChgUserId(lastChgUserId);
        assertEquals(lastChgUserId, container.getLastChgUserId());
    }

    @Test
    void testGetJurisdictionCd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String jurisdictionCd = "jurisdictionCd";
        container.setJurisdictionCd(jurisdictionCd);
        assertEquals(jurisdictionCd, container.getJurisdictionCd());
    }

    @Test
    void testSetJurisdictionCd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String jurisdictionCd = "jurisdictionCd";
        container.setJurisdictionCd(jurisdictionCd);
        assertEquals(jurisdictionCd, container.getJurisdictionCd());
    }

    @Test
    void testGetProgAreaCd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String progAreaCd = "progAreaCd";
        container.setProgAreaCd(progAreaCd);
        assertEquals(progAreaCd, container.getProgAreaCd());
    }

    @Test
    void testSetProgAreaCd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String progAreaCd = "progAreaCd";
        container.setProgAreaCd(progAreaCd);
        assertEquals(progAreaCd, container.getProgAreaCd());
    }

    @Test
    void testGetLastChgTime() {
        UidSummaryContainer container = new UidSummaryContainer();
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        container.setLastChgTime(lastChgTime);
        assertEquals(lastChgTime, container.getLastChgTime());
    }

    @Test
    void testSetLastChgTime() {
        UidSummaryContainer container = new UidSummaryContainer();
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        container.setLastChgTime(lastChgTime);
        assertEquals(lastChgTime, container.getLastChgTime());
    }

    @Test
    void testGetLocalId() {
        UidSummaryContainer container = new UidSummaryContainer();
        String localId = "localId";
        container.setLocalId(localId);
        assertEquals(localId, container.getLocalId());
    }

    @Test
    void testSetLocalId() {
        UidSummaryContainer container = new UidSummaryContainer();
        String localId = "localId";
        container.setLocalId(localId);
        assertEquals(localId, container.getLocalId());
    }

    @Test
    void testGetAddUserId() {
        UidSummaryContainer container = new UidSummaryContainer();
        Long addUserId = 123L;
        container.setAddUserId(addUserId);
        assertEquals(addUserId, container.getAddUserId());
    }

    @Test
    void testSetAddUserId() {
        UidSummaryContainer container = new UidSummaryContainer();
        Long addUserId = 123L;
        container.setAddUserId(addUserId);
        assertEquals(addUserId, container.getAddUserId());
    }

    @Test
    void testGetLastChgReasonCd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String lastChgReasonCd = "lastChgReasonCd";
        container.setLastChgReasonCd(lastChgReasonCd);
        assertEquals(lastChgReasonCd, container.getLastChgReasonCd());
    }

    @Test
    void testSetLastChgReasonCd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String lastChgReasonCd = "lastChgReasonCd";
        container.setLastChgReasonCd(lastChgReasonCd);
        assertEquals(lastChgReasonCd, container.getLastChgReasonCd());
    }

    @Test
    void testGetRecordStatusCd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String recordStatusCd = "recordStatusCd";
        container.setRecordStatusCd(recordStatusCd);
        assertEquals(recordStatusCd, container.getRecordStatusCd());
    }

    @Test
    void testSetRecordStatusCd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String recordStatusCd = "recordStatusCd";
        container.setRecordStatusCd(recordStatusCd);
        assertEquals(recordStatusCd, container.getRecordStatusCd());
    }

    @Test
    void testGetRecordStatusTime() {
        UidSummaryContainer container = new UidSummaryContainer();
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        container.setRecordStatusTime(recordStatusTime);
        assertEquals(recordStatusTime, container.getRecordStatusTime());
    }

    @Test
    void testSetRecordStatusTime() {
        UidSummaryContainer container = new UidSummaryContainer();
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        container.setRecordStatusTime(recordStatusTime);
        assertEquals(recordStatusTime, container.getRecordStatusTime());
    }

    @Test
    void testGetStatusCd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String statusCd = "statusCd";
        container.setStatusCd(statusCd);
        assertEquals(statusCd, container.getStatusCd());
    }

    @Test
    void testSetStatusCd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String statusCd = "statusCd";
        container.setStatusCd(statusCd);
        assertEquals(statusCd, container.getStatusCd());
    }

    @Test
    void testGetStatusTime() {
        UidSummaryContainer container = new UidSummaryContainer();
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        container.setStatusTime(statusTime);
        assertEquals(statusTime, container.getStatusTime());
    }

    @Test
    void testSetStatusTime() {
        UidSummaryContainer container = new UidSummaryContainer();
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        container.setStatusTime(statusTime);
        assertEquals(statusTime, container.getStatusTime());
    }

    @Test
    void testGetSuperclass() {
        UidSummaryContainer container = new UidSummaryContainer();
        assertEquals("gov.cdc.dataprocessing.model.container.base.BaseContainer", container.getSuperclass());
    }

    @Test
    void testGetUid() {
        UidSummaryContainer container = new UidSummaryContainer();
        Long uid = 123L;
        container.setUid(uid);
        assertEquals(uid, container.getUid());
    }

    @Test
    void testSetAddTime() {
        UidSummaryContainer container = new UidSummaryContainer();
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        container.setAddTime(addTime);
        assertEquals(addTime, container.getAddTime());
    }

    @Test
    void testGetAddTime() {
        UidSummaryContainer container = new UidSummaryContainer();
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        container.setAddTime(addTime);
        assertEquals(addTime, container.getAddTime());
    }

    @Test
    void testGetProgramJurisdictionOid() {
        UidSummaryContainer container = new UidSummaryContainer();
        Long programJurisdictionOid = 123L;
        container.setProgramJurisdictionOid(programJurisdictionOid);
        assertEquals(programJurisdictionOid, container.getProgramJurisdictionOid());
    }

    @Test
    void testSetProgramJurisdictionOid() {
        UidSummaryContainer container = new UidSummaryContainer();
        Long programJurisdictionOid = 123L;
        container.setProgramJurisdictionOid(programJurisdictionOid);
        assertEquals(programJurisdictionOid, container.getProgramJurisdictionOid());
    }

    @Test
    void testGetSharedInd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String sharedInd = "sharedInd";
        container.setSharedInd(sharedInd);
        assertEquals(sharedInd, container.getSharedInd());
    }

    @Test
    void testSetSharedInd() {
        UidSummaryContainer container = new UidSummaryContainer();
        String sharedInd = "sharedInd";
        container.setSharedInd(sharedInd);
        assertEquals(sharedInd, container.getSharedInd());
    }

    @Test
    void testGetVersionCtrlNbr() {
        UidSummaryContainer container = new UidSummaryContainer();
        Integer versionCtrlNbr = 1;
        container.setVersionCtrlNbr(versionCtrlNbr);
        assertEquals(versionCtrlNbr, container.getVersionCtrlNbr());
    }

    @Test
    void testSetVersionCtrlNbr() {
        UidSummaryContainer container = new UidSummaryContainer();
        Integer versionCtrlNbr = 1;
        container.setVersionCtrlNbr(versionCtrlNbr);
        assertEquals(versionCtrlNbr, container.getVersionCtrlNbr());
    }
}
