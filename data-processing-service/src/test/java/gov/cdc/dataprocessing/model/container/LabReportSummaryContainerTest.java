package gov.cdc.dataprocessing.model.container;

import gov.cdc.dataprocessing.model.container.model.LabReportSummaryContainer;
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
}
