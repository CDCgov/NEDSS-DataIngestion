package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.ResultedTestSummaryContainer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResultedTestSummaryContainerTest {

    @Test
    void testGettersAndSetters() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();

        Long sourceActUid = 1L;
        String localId = "localId";
        Long observationUid = 2L;
        String ctrlCdUserDefined1 = "ctrlCdUserDefined1";
        String resultedTest = "resultedTest";
        String codedResultValue = "codedResultValue";
        String organismName = "organismName";
        String numericResultCompare = "numericResultCompare";
        BigDecimal numericResultValue1 = new BigDecimal("123.45");
        String numericResultSeperator = "numericResultSeperator";
        BigDecimal numericResultValue2 = new BigDecimal("678.90");
        String numericResultUnits = "numericResultUnits";
        String textResultValue = "textResultValue";
        String type = "type";
        String status = "status";
        String resultedTestStatusCd = "resultedTestStatusCd";
        String resultedTestStatus = "resultedTestStatus";
        String drugName = "drugName";
        String orderedTest = "orderedTest";
        Collection<Object> theSusTestSummaryVOColl = new ArrayList<>();
        String cdSystemCd = "cdSystemCd";
        String resultedTestCd = "resultedTestCd";
        String organismCodeSystemCd = "organismCodeSystemCd";
        String recordStatusCode = "recordStatusCode";
        String highRange = "highRange";
        Integer numericScale1 = 1;
        String lowRange = "lowRange";
        String uniqueMapKey = "uniqueMapKey";
        Integer numericScale2 = 2;

        container.setSourceActUid(sourceActUid);
        container.setLocalId(localId);
        container.setObservationUid(observationUid);
        container.setCtrlCdUserDefined1(ctrlCdUserDefined1);
        container.setResultedTest(resultedTest);
        container.setCodedResultValue(codedResultValue);
        container.setOrganismName(organismName);
        container.setNumericResultCompare(numericResultCompare);
        container.setNumericResultValue1(numericResultValue1);
        container.setNumericResultSeperator(numericResultSeperator);
        container.setNumericResultValue2(numericResultValue2);
        container.setNumericResultUnits(numericResultUnits);
        container.setTextResultValue(textResultValue);
        container.setType(type);
        container.setStatus(status);
        container.setResultedTestStatusCd(resultedTestStatusCd);
        container.setResultedTestStatus(resultedTestStatus);
        container.setDrugName(drugName);
        container.setOrderedTest(orderedTest);
        container.setTheSusTestSummaryVOColl(theSusTestSummaryVOColl);
        container.setCdSystemCd(cdSystemCd);
        container.setResultedTestCd(resultedTestCd);
        container.setOrganismCodeSystemCd(organismCodeSystemCd);
        container.setRecordStatusCode(recordStatusCode);
        container.setHighRange(highRange);
        container.setNumericScale1(numericScale1);
        container.setLowRange(lowRange);
        container.setUniqueMapKey(uniqueMapKey);
        container.setNumericScale2(numericScale2);

        assertEquals(sourceActUid, container.getSourceActUid());
        assertEquals(localId, container.getLocalId());
        assertEquals(observationUid, container.getObservationUid());
        assertEquals(ctrlCdUserDefined1, container.getCtrlCdUserDefined1());
        assertEquals(resultedTest, container.getResultedTest());
        assertEquals(codedResultValue, container.getCodedResultValue());
        assertEquals(organismName, container.getOrganismName());
        assertEquals(numericResultCompare, container.getNumericResultCompare());
        assertEquals(numericResultValue1, container.getNumericResultValue1());
        assertEquals(numericResultSeperator, container.getNumericResultSeperator());
        assertEquals(numericResultValue2, container.getNumericResultValue2());
        assertEquals(numericResultUnits, container.getNumericResultUnits());
        assertEquals(textResultValue, container.getTextResultValue());
        assertEquals(type, container.getType());
        assertEquals(status, container.getStatus());
        assertEquals(resultedTestStatusCd, container.getResultedTestStatusCd());
        assertEquals(resultedTestStatus, container.getResultedTestStatus());
        assertEquals(drugName, container.getDrugName());
        assertEquals(orderedTest, container.getOrderedTest());
        assertEquals(theSusTestSummaryVOColl, container.getTheSusTestSummaryVOColl());
        assertEquals(cdSystemCd, container.getCdSystemCd());
        assertEquals(resultedTestCd, container.getResultedTestCd());
        assertEquals(organismCodeSystemCd, container.getOrganismCodeSystemCd());
        assertEquals(recordStatusCode, container.getRecordStatusCode());
        assertEquals(highRange, container.getHighRange());
        assertEquals(numericScale1, container.getNumericScale1());
        assertEquals(lowRange, container.getLowRange());
        assertEquals(uniqueMapKey, container.getUniqueMapKey());
        assertEquals(numericScale2, container.getNumericScale2());
    }

    @Test
    void testGetLastChgUserId() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Long lastChgUserId = 123L;
        container.setLastChgUserId(lastChgUserId);
        assertEquals(lastChgUserId, container.getLastChgUserId());
    }

    @Test
    void testSetLastChgUserId() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Long lastChgUserId = 123L;
        container.setLastChgUserId(lastChgUserId);
        assertEquals(lastChgUserId, container.getLastChgUserId());
    }

    @Test
    void testGetJurisdictionCd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String jurisdictionCd = "jurisdictionCd";
        container.setJurisdictionCd(jurisdictionCd);
        assertEquals(jurisdictionCd, container.getJurisdictionCd());
    }

    @Test
    void testSetJurisdictionCd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String jurisdictionCd = "jurisdictionCd";
        container.setJurisdictionCd(jurisdictionCd);
        assertEquals(jurisdictionCd, container.getJurisdictionCd());
    }

    @Test
    void testGetProgAreaCd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String progAreaCd = "progAreaCd";
        container.setProgAreaCd(progAreaCd);
        assertEquals(progAreaCd, container.getProgAreaCd());
    }

    @Test
    void testSetProgAreaCd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String progAreaCd = "progAreaCd";
        container.setProgAreaCd(progAreaCd);
        assertEquals(progAreaCd, container.getProgAreaCd());
    }

    @Test
    void testGetLastChgTime() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        container.setLastChgTime(lastChgTime);
        assertEquals(lastChgTime, container.getLastChgTime());
    }

    @Test
    void testSetLastChgTime() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Timestamp lastChgTime = new Timestamp(System.currentTimeMillis());
        container.setLastChgTime(lastChgTime);
        assertEquals(lastChgTime, container.getLastChgTime());
    }

    @Test
    void testGetLocalId() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String localId = "localId";
        container.setLocalId(localId);
        assertEquals(localId, container.getLocalId());
    }

    @Test
    void testSetLocalId() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String localId = "localId";
        container.setLocalId(localId);
        assertEquals(localId, container.getLocalId());
    }

    @Test
    void testGetAddUserId() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Long addUserId = 123L;
        container.setAddUserId(addUserId);
        assertEquals(addUserId, container.getAddUserId());
    }

    @Test
    void testSetAddUserId() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Long addUserId = 123L;
        container.setAddUserId(addUserId);
        assertEquals(addUserId, container.getAddUserId());
    }

    @Test
    void testGetLastChgReasonCd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String lastChgReasonCd = "lastChgReasonCd";
        container.setLastChgReasonCd(lastChgReasonCd);
        assertEquals(lastChgReasonCd, container.getLastChgReasonCd());
    }

    @Test
    void testSetLastChgReasonCd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String lastChgReasonCd = "lastChgReasonCd";
        container.setLastChgReasonCd(lastChgReasonCd);
        assertEquals(lastChgReasonCd, container.getLastChgReasonCd());
    }

    @Test
    void testGetRecordStatusCd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String recordStatusCd = "recordStatusCd";
        container.setRecordStatusCd(recordStatusCd);
        assertEquals(recordStatusCd, container.getRecordStatusCd());
    }

    @Test
    void testSetRecordStatusCd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String recordStatusCd = "recordStatusCd";
        container.setRecordStatusCd(recordStatusCd);
        assertEquals(recordStatusCd, container.getRecordStatusCd());
    }

    @Test
    void testGetRecordStatusTime() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        container.setRecordStatusTime(recordStatusTime);
        assertEquals(recordStatusTime, container.getRecordStatusTime());
    }

    @Test
    void testSetRecordStatusTime() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Timestamp recordStatusTime = new Timestamp(System.currentTimeMillis());
        container.setRecordStatusTime(recordStatusTime);
        assertEquals(recordStatusTime, container.getRecordStatusTime());
    }

    @Test
    void testGetStatusCd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String statusCd = "statusCd";
        container.setStatusCd(statusCd);
        assertEquals(statusCd, container.getStatusCd());
    }

    @Test
    void testSetStatusCd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String statusCd = "statusCd";
        container.setStatusCd(statusCd);
        assertEquals(statusCd, container.getStatusCd());
    }

    @Test
    void testGetStatusTime() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        container.setStatusTime(statusTime);
        assertEquals(statusTime, container.getStatusTime());
    }

    @Test
    void testSetStatusTime() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Timestamp statusTime = new Timestamp(System.currentTimeMillis());
        container.setStatusTime(statusTime);
        assertEquals(statusTime, container.getStatusTime());
    }

    @Test
    void testGetSuperclass() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        assertEquals("gov.cdc.dataprocessing.model.container.base.BaseContainer", container.getSuperclass());
    }

    @Test
    void testGetUid() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Long uid = 123L;
        container.setObservationUid(uid);
        assertEquals(uid, container.getUid());
    }

    @Test
    void testSetAddTime() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Timestamp addTime = new Timestamp(System.currentTimeMillis());
        container.setAddTime(addTime);
        assertNull(container.getAddTime());
    }

    @Test
    void testGetAddTime() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        assertNull(container.getAddTime());
    }

    @Test
    void testGetProgramJurisdictionOid() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Long programJurisdictionOid = 123L;
        container.setProgramJurisdictionOid(programJurisdictionOid);
        assertEquals(programJurisdictionOid, container.getProgramJurisdictionOid());
    }

    @Test
    void testSetProgramJurisdictionOid() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Long programJurisdictionOid = 123L;
        container.setProgramJurisdictionOid(programJurisdictionOid);
        assertEquals(programJurisdictionOid, container.getProgramJurisdictionOid());
    }

    @Test
    void testGetSharedInd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String sharedInd = "sharedInd";
        container.setSharedInd(sharedInd);
        assertEquals(sharedInd, container.getSharedInd());
    }

    @Test
    void testSetSharedInd() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        String sharedInd = "sharedInd";
        container.setSharedInd(sharedInd);
        assertEquals(sharedInd, container.getSharedInd());
    }

    @Test
    void testGetVersionCtrlNbr() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Integer versionCtrlNbr = 1;
        container.setVersionCtrlNbr(versionCtrlNbr);
        assertEquals(versionCtrlNbr, container.getVersionCtrlNbr());
    }

    @Test
    void testSetVersionCtrlNbr() {
        ResultedTestSummaryContainer container = new ResultedTestSummaryContainer();
        Integer versionCtrlNbr = 1;
        container.setVersionCtrlNbr(versionCtrlNbr);
        assertEquals(versionCtrlNbr, container.getVersionCtrlNbr());
    }
}
