package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.LabReportSummaryContainer;
import gov.cdc.dataprocessing.model.container.model.ProviderDataForPrintContainer;
import gov.cdc.dataprocessing.model.container.model.ResultedTestSummaryContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Lab_Summary_ForWorkUp_New;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LabReportSummaryContainerTest {

    @Test
    void testDefaultConstructor() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        assertNotNull(container);
    }

    @Test
    void testConstructorWithObservationLabSummary() {
        Observation_Lab_Summary_ForWorkUp_New summary = new Observation_Lab_Summary_ForWorkUp_New();
        summary.setUid(123L);
        summary.setLocalId("local123");
        summary.setJurisdictionCd("jurisdiction");
        summary.setStatusCd("status");
        summary.setRecordStatusCd("recordStatus");
        summary.setCdDescTxt("orderedTest");
        summary.setObservationUid(456L);
        summary.setProgAreaCd("programArea");
        summary.setRptToStateTime(new Timestamp(System.currentTimeMillis()));
        summary.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        summary.setCdSystemCd("cdSystem");
        summary.setEffectiveFromTime(new Timestamp(System.currentTimeMillis()));
        summary.setProcessingDecisionCd("processingDecision");
        summary.setElectronicInd("Y");

        LabReportSummaryContainer container = new LabReportSummaryContainer(summary);

        assertEquals(123L, container.getUid());
        assertEquals("local123", container.getLocalId());
        assertEquals("jurisdiction", container.getJurisdictionCd());
        assertEquals("status", container.getStatus());
        assertEquals("recordStatus", container.getRecordStatusCd());
        assertEquals("orderedTest", container.getOrderedTest());
        assertEquals(456L, container.getObservationUid());
        assertEquals("programArea", container.getProgramArea());
        assertNotNull(container.getDateReceived());
        assertNotNull(container.getActivityFromTime());
        assertEquals("cdSystem", container.getCdSystemCd());
        assertNotNull(container.getDateCollected());
        assertEquals("processingDecision", container.getProcessingDecisionCd());
        assertEquals("Y", container.getElectronicInd());
    }

    @Test
    void testSetAndGetValues() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        container.setUid(789L);
        container.setLocalId("local789");
        container.setJurisdictionCd("newJurisdiction");
        container.setStatus("newStatus");
        container.setRecordStatusCd("newRecordStatus");
        container.setOrderedTest("newOrderedTest");
        container.setObservationUid(101112L);
        container.setProgramArea("newProgramArea");
        container.setDateReceived(new Timestamp(System.currentTimeMillis()));
        container.setActivityFromTime(new Timestamp(System.currentTimeMillis()));
        container.setCdSystemCd("newCdSystem");
        container.setDateCollected(new Timestamp(System.currentTimeMillis()));
        container.setProcessingDecisionCd("newProcessingDecision");
        container.setElectronicInd("N");

        assertEquals(789L, container.getUid());
        assertEquals("local789", container.getLocalId());
        assertEquals("newJurisdiction", container.getJurisdictionCd());
        assertEquals("newStatus", container.getStatus());
        assertEquals("newRecordStatus", container.getRecordStatusCd());
        assertEquals("newOrderedTest", container.getOrderedTest());
        assertEquals(101112L, container.getObservationUid());
        assertEquals("newProgramArea", container.getProgramArea());
        assertNotNull(container.getDateReceived());
        assertNotNull(container.getActivityFromTime());
        assertEquals("newCdSystem", container.getCdSystemCd());
        assertNotNull(container.getDateCollected());
        assertEquals("newProcessingDecision", container.getProcessingDecisionCd());
        assertEquals("N", container.getElectronicInd());
    }

    @Test
    void testSetAndGetCollections() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();

        Collection<ResultedTestSummaryContainer> resultedTestSummaryVOCollection = new ArrayList<>();
        resultedTestSummaryVOCollection.add(new ResultedTestSummaryContainer());
        container.setTheResultedTestSummaryVOCollection(resultedTestSummaryVOCollection);
        assertEquals(resultedTestSummaryVOCollection, container.getTheResultedTestSummaryVOCollection());

        Collection<Object> invSummaryVOs = new ArrayList<>();
        invSummaryVOs.add("invSummary");
        container.setInvSummaryVOs(invSummaryVOs);
        assertEquals(invSummaryVOs, container.getInvSummaryVOs());
    }

    @Test
    void testSetAndGetProviderFields() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        container.setProviderFirstName("John");
        container.setProviderLastName("Doe");
        container.setProviderSuffix("Jr.");
        container.setProviderPrefix("Dr.");
        container.setProviderDegree("MD");
        container.setProviderUid("UID123");

        assertEquals("John", container.getProviderFirstName());
        assertEquals("Doe", container.getProviderLastName());
        assertEquals("Jr.", container.getProviderSuffix());
        assertEquals("Dr.", container.getProviderPrefix());
        assertEquals("MD", container.getProviderDegree());
        assertEquals("UID123", container.getProviderUid());
    }

    @Test
    void testSetAndGetBooleanFields() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        container.setLabFromMorb(true);
        container.setReactor(true);
        container.setLabFromDoc(true);
        container.setTouched(true);
        container.setAssociated(true);

        assertTrue(container.isLabFromMorb());
        assertTrue(container.isReactor());
        assertTrue(container.isLabFromDoc());
        assertTrue(container.getIsTouched());
        assertTrue(container.getIsAssociated());
    }

    @Test
    void testSetAndGetMiscFields() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        container.setPersonUid(123456L);
        container.setLastNm("Smith");
        container.setFirstNm("Jane");
        container.setPersonParentUid(654321L);
        container.setCurrSexCd("F");
        container.setOrderingFacility("Facility");

        assertEquals(123456L, container.getPersonUid());
        assertEquals("Smith", container.getLastNm());
        assertEquals("Jane", container.getFirstNm());
        assertEquals(654321L, container.getPersonParentUid());
        assertEquals("F", container.getCurrSexCd());
        assertEquals("Facility", container.getOrderingFacility());
    }

    @Test
    void testGettersAndSetters() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();

        container.setTouched(true);
        assertTrue(container.getIsTouched());

        container.setAssociated(true);
        assertTrue(container.getIsAssociated());

        Timestamp dateReceived = new Timestamp(System.currentTimeMillis());
        container.setDateReceived(dateReceived);
        assertEquals(dateReceived, container.getDateReceived());

        String dateReceivedS = "2024-07-13";
        container.setDateReceivedS(dateReceivedS);
        assertEquals(dateReceivedS, container.getDateReceivedS());

        int versionCtrlNbr = 1;
        container.setVersionCtrlNbr(versionCtrlNbr);
        assertEquals(versionCtrlNbr, container.getVersionCtrlNbr());

        Timestamp dateCollected = new Timestamp(System.currentTimeMillis());
        container.setDateCollected(dateCollected);
        assertEquals(dateCollected, container.getDateCollected());

        Timestamp activityFromTime = new Timestamp(System.currentTimeMillis());
        container.setActivityFromTime(activityFromTime);
        assertEquals(activityFromTime, container.getActivityFromTime());

        String type = "TestType";
        container.setType(type);
        assertEquals(type, container.getType());

        String programArea = "TestProgramArea";
        container.setProgramArea(programArea);
        assertEquals(programArea, container.getProgramArea());

        String jurisdiction = "TestJurisdiction";
        container.setJurisdiction(jurisdiction);
        assertEquals(jurisdiction, container.getJurisdiction());

        String jurisdictionCd = "TestJurisdictionCd";
        container.setJurisdictionCd(jurisdictionCd);
        assertEquals(jurisdictionCd, container.getJurisdictionCd());

        String status = "TestStatus";
        container.setStatus(status);
        assertEquals(status, container.getStatus());

        String recordStatusCd = "TestRecordStatusCd";
        container.setRecordStatusCd(recordStatusCd);
        assertEquals(recordStatusCd, container.getRecordStatusCd());

        long observationUid = 1L;
        container.setObservationUid(observationUid);
        assertEquals(observationUid, container.getObservationUid());

        String patientFirstName = "John";
        container.setPatientFirstName(patientFirstName);
        assertEquals(patientFirstName, container.getPatientFirstName());

        String patientLastName = "Doe";
        container.setPatientLastName(patientLastName);
        assertEquals(patientLastName, container.getPatientLastName());

        String personLocalId = "TestPersonLocalId";
        container.setPersonLocalId(personLocalId);
        assertEquals(personLocalId, container.getPersonLocalId());

        Collection<ResultedTestSummaryContainer> theResultedTestSummaryVOCollection = new ArrayList<>();
        container.setTheResultedTestSummaryVOCollection(theResultedTestSummaryVOCollection);
        assertEquals(theResultedTestSummaryVOCollection, container.getTheResultedTestSummaryVOCollection());

        Collection<Object> invSummaryVOs = new ArrayList<>();
        container.setInvSummaryVOs(invSummaryVOs);
        assertEquals(invSummaryVOs, container.getInvSummaryVOs());

        String orderedTest = "TestOrdered";
        container.setOrderedTest(orderedTest);
        assertEquals(orderedTest, container.getOrderedTest());

        long mprUid = 2L;
        container.setMPRUid(mprUid);
        assertEquals(mprUid, container.getMPRUid());

        String cdSystemCd = "TestCdSystemCd";
        container.setCdSystemCd(cdSystemCd);
        assertEquals(cdSystemCd, container.getCdSystemCd());

        String actionLink = "TestActionLink";
        container.setActionLink(actionLink);
        assertEquals(actionLink, container.getActionLink());

        String resultedTestString = "TestResulted";
        container.setResultedTestString(resultedTestString);
        assertEquals(resultedTestString, container.getResultedTestString());

        String reportingFacility = "TestFacility";
        container.setReportingFacility(reportingFacility);
        assertEquals(reportingFacility, container.getReportingFacility());

        String specimenSource = "TestSpecimenSource";
        container.setSpecimenSource(specimenSource);
        assertEquals(specimenSource, container.getSpecimenSource());

        String[] selectedcheckboxIds = new String[]{"Test1", "Test2"};
        container.setSelectedcheckboxIds(selectedcheckboxIds);
        assertArrayEquals(selectedcheckboxIds, container.getSelectedcheckboxIds());

        String checkBoxId = "TestCheckBoxId";
        container.setCheckBoxId(checkBoxId);
        assertEquals(checkBoxId, container.getCheckBoxId());

        String providerFirstName = "Jane";
        container.setProviderFirstName(providerFirstName);
        assertEquals(providerFirstName, container.getProviderFirstName());

        String providerLastName = "Smith";
        container.setProviderLastName(providerLastName);
        assertEquals(providerLastName, container.getProviderLastName());

        String providerSuffix = "Jr.";
        container.setProviderSuffix(providerSuffix);
        assertEquals(providerSuffix, container.getProviderSuffix());

        String providerPrefix = "Dr.";
        container.setProviderPrefix(providerPrefix);
        assertEquals(providerPrefix, container.getProviderPrefix());

        String providerDegree = "MD";
        container.setProviderDegree(providerDegree);
        assertEquals(providerDegree, container.getProviderDegree());

        String providerUid = "ProviderUid123";
        container.setProviderUid(providerUid);
        assertEquals(providerUid, container.getProviderUid());

        String degree = "PhD";
        container.setDegree(degree);
        assertEquals(degree, container.getDegree());

        String accessionNumber = "AccessionNumber123";
        container.setAccessionNumber(accessionNumber);
        assertEquals(accessionNumber, container.getAccessionNumber());

        container.setLabFromMorb(true);
        assertTrue(container.isLabFromMorb());

        container.setReactor(true);
        assertTrue(container.isReactor());

        String electronicInd = "TestElectronicInd";
        container.setElectronicInd(electronicInd);
        assertEquals(electronicInd, container.getElectronicInd());

        Map<Object, Object> associationsMap = Map.of("Key", "Value");
        container.setAssociationsMap(associationsMap);
        assertEquals(associationsMap, container.getAssociationsMap());

        String processingDecisionCd = "TestProcessingDecisionCd";
        container.setProcessingDecisionCd(processingDecisionCd);
        assertEquals(processingDecisionCd, container.getProcessingDecisionCd());

        String disabled = "true";
        container.setDisabled(disabled);
        assertEquals(disabled, container.getDisabled());

        ProviderDataForPrintContainer providerDataForPrintVO = new ProviderDataForPrintContainer();
        container.setProviderDataForPrintVO(providerDataForPrintVO);
        assertEquals(providerDataForPrintVO, container.getProviderDataForPrintVO());

        container.setLabFromDoc(true);
        assertTrue(container.isLabFromDoc());

        Long uid = 3L;
        container.setUid(uid);
        assertEquals(uid, container.getUid());

        String sharedInd = "TestSharedInd";
        container.setSharedInd(sharedInd);
        assertEquals(sharedInd, container.getSharedInd());

        String progAreaCd = "TestProgAreaCd";
        container.setProgAreaCd(progAreaCd);
        assertEquals(progAreaCd, container.getProgAreaCd());

        String localId = "TestLocalId";
        container.setLocalId(localId);
        assertEquals(localId, container.getLocalId());

        Long personUid = 4L;
        container.setPersonUid(personUid);
        assertEquals(personUid, container.getPersonUid());

        String lastNm = "LastName";
        container.setLastNm(lastNm);
        assertEquals(lastNm, container.getLastNm());

        String firstNm = "FirstName";
        container.setFirstNm(firstNm);
        assertEquals(firstNm, container.getFirstNm());

        Long personParentUid = 5L;
        container.setPersonParentUid(personParentUid);
        assertEquals(personParentUid, container.getPersonParentUid());

        String currSexCd = "Male";
        container.setCurrSexCd(currSexCd);
        assertEquals(currSexCd, container.getCurrSexCd());

        String orderingFacility = "TestOrderingFacility";
        container.setOrderingFacility(orderingFacility);
        assertEquals(orderingFacility, container.getOrderingFacility());
    }

    @Test
    void testGetLastChgUserId() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Long personUid = 123L;
        container.setPersonUid(personUid);
        assertEquals(personUid, container.getLastChgUserId());
    }

    @Test
    void testSetLastChgUserId() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Long personUid = 123L;
        container.setLastChgUserId(personUid);
        assertEquals(personUid, container.getPersonUid());
    }

    @Test
    void testGetLastChgTime() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Timestamp dateReceived = new Timestamp(System.currentTimeMillis());
        container.setDateReceived(dateReceived);
        assertEquals(dateReceived, container.getLastChgTime());
    }

    @Test
    void testSetLastChgTime() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Timestamp dateReceived = new Timestamp(System.currentTimeMillis());
        container.setLastChgTime(dateReceived);
        assertEquals(dateReceived, container.getDateReceived());
    }

    @Test
    void testGetAddUserId() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Long personUid = 123L;
        container.setPersonUid(personUid);
        assertEquals(personUid, container.getAddUserId());
    }

    @Test
    void testSetAddUserId() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Long personUid = 123L;
        container.setAddUserId(personUid);
        assertEquals(personUid, container.getPersonUid());
    }

    @Test
    void testGetRecordStatusTime() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Timestamp dateReceived = new Timestamp(System.currentTimeMillis());
        container.setDateReceived(dateReceived);
        assertEquals(dateReceived, container.getRecordStatusTime());
    }

    @Test
    void testSetRecordStatusTime() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Timestamp dateReceived = new Timestamp(System.currentTimeMillis());
        container.setRecordStatusTime(dateReceived);
        assertEquals(dateReceived, container.getDateReceived());
    }

    @Test
    void testGetStatusCd() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        String status = "status";
        container.setStatus(status);
        assertEquals(status, container.getStatusCd());
    }

    @Test
    void testSetStatusCd() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        String status = "status";
        container.setStatusCd(status);
        assertEquals(status, container.getStatus());
    }

    @Test
    void testGetStatusTime() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Timestamp dateReceived = new Timestamp(System.currentTimeMillis());
        container.setDateReceived(dateReceived);
        assertEquals(dateReceived, container.getStatusTime());
    }

    @Test
    void testSetStatusTime() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Timestamp dateReceived = new Timestamp(System.currentTimeMillis());
        container.setStatusTime(dateReceived);
        assertEquals(dateReceived, container.getDateReceived());
    }

    @Test
    void testGetSuperclass() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        assertEquals("gov.cdc.dataprocessing.model.container.base.BaseContainer", container.getSuperclass());
    }

    @Test
    void testSetAddTime() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Timestamp dateReceived = new Timestamp(System.currentTimeMillis());
        container.setAddTime(dateReceived);
        assertEquals(dateReceived, container.getDateReceived());
    }

    @Test
    void testGetAddTime() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Timestamp dateReceived = new Timestamp(System.currentTimeMillis());
        container.setDateReceived(dateReceived);
        assertEquals(dateReceived, container.getAddTime());
    }

    @Test
    void testGetProgramJurisdictionOid() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Long MPRUid = 123L;
        container.setMPRUid(MPRUid);
        assertEquals(MPRUid, container.getProgramJurisdictionOid());
    }

    @Test
    void testSetProgramJurisdictionOid() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        Long MPRUid = 123L;
        container.setProgramJurisdictionOid(MPRUid);
        assertEquals(MPRUid, container.getMPRUid());
    }

    @Test
    void testCompareTo() {
        LabReportSummaryContainer container1 = new LabReportSummaryContainer();
        LabReportSummaryContainer container2 = new LabReportSummaryContainer();
        container1.setUid(1L);
        container2.setUid(2L);
        assertTrue(container1.compareTo(container2) < 0);
        assertTrue(container2.compareTo(container1) > 0);
    }

    @Test
    void testGetIsTouched() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        container.setItTouched(true);
        assertTrue(container.getIsTouched());
    }



    @Test
    void testGetIsAssociated() {
        LabReportSummaryContainer container = new LabReportSummaryContainer();
        container.setItAssociated(true);
        assertTrue(container.getIsAssociated());
    }


}
