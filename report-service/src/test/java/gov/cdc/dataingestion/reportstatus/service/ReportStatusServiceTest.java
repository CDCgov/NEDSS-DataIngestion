package gov.cdc.dataingestion.reportstatus.service;

import gov.cdc.dataingestion.nbs.repository.NbsInterfaceRepository;
import gov.cdc.dataingestion.nbs.repository.model.NbsInterfaceModel;
import gov.cdc.dataingestion.reportstatus.model.ReportStatusIdData;
import gov.cdc.dataingestion.reportstatus.repository.IReportStatusRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ReportStatusServiceTest {
    @Mock
    private IReportStatusRepository iReportStatusRepositoryMock;
    @Mock
    private NbsInterfaceRepository nbsInterfaceRepositoryMock;
    @InjectMocks
    private ReportStatusService reportStatusServiceMock;
    private ReportStatusIdData reportStatusIdData;
    private NbsInterfaceModel nbsInterfaceModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reportStatusIdData = new ReportStatusIdData();
        nbsInterfaceModel = new NbsInterfaceModel();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(iReportStatusRepositoryMock);
        Mockito.reset(nbsInterfaceRepositoryMock);
    }

    @Test
    void testGetStatusForReportSuccessForValidData() {
        String id = "test_uuid_from_user";
        reportStatusIdData.setNbsInterfaceUid(1234);
        nbsInterfaceModel.setRecordStatusCd("Success");

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.of(reportStatusIdData));
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(1234)).thenReturn(Optional.of(nbsInterfaceModel));

        String status = reportStatusServiceMock.getStatusForReport(id);
        assertEquals("Success", status);
    }

    @Test
    void testGetStatusForReportEmptyReportIdData() {
        String id = "test_uuid_from_user_does_not_exist";

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.empty());

        String status = reportStatusServiceMock.getStatusForReport(id);
        assertEquals("Provided UUID is not present in the database.", status);
    }

    @Test
    void testGetStatusForReportEmptyNbsInterfaceData() {
        String id = "test_uuid_from_user";

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.of(reportStatusIdData));
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(1234)).thenReturn(Optional.empty());

        String status = reportStatusServiceMock.getStatusForReport(id);
        assertEquals("Couldn't find status for the requested ID.", status);
    }

    @Test
    void testDummyGetStatusForReportSuccessModelCoverage() {
        String id = "test_uuid_from_user";
        reportStatusIdData.setNbsInterfaceUid(1234);
        nbsInterfaceModel.setRecordStatusCd("Success");

        // These setters are added to increase the line coverage for model class
        reportStatusIdData.setId("test_uuid");
        reportStatusIdData.setRawMessageId(id);
        reportStatusIdData.setCreatedBy("junit_test");
        reportStatusIdData.setUpdatedBy("junit_test");

        when(iReportStatusRepositoryMock.findByRawMessageId(id)).thenReturn(Optional.of(reportStatusIdData));
        when(nbsInterfaceRepositoryMock.findByNbsInterfaceUid(1234)).thenReturn(Optional.of(nbsInterfaceModel));

        String status = reportStatusServiceMock.getStatusForReport(id);
        assertEquals("Success", status);

        // The following asserts are added to increase the line coverage for model class
        assertEquals("test_uuid", reportStatusIdData.getId());
        assertEquals(id, reportStatusIdData.getRawMessageId());
        assertEquals("junit_test", reportStatusIdData.getCreatedBy());
        assertEquals("junit_test", reportStatusIdData.getUpdatedBy());
    }
}