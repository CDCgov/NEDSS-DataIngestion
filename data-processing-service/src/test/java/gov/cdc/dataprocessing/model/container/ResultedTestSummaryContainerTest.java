package gov.cdc.dataprocessing.model.container;


import gov.cdc.dataprocessing.model.container.model.ResultedTestSummaryContainer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
