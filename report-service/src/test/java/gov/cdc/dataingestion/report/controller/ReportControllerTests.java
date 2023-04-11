//package gov.cdc.dataingestion.report.controller;
//
//import gov.cdc.dataingestion.report.integration.service.ReportService;
//import gov.cdc.dataingestion.report.model.ReportDetails;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
///**
// * Tests for {@link ReportController}.
// */
//public final class ReportControllerTests {
//
//    /**
//     * Report service.
//     */
//    private ReportService reportService;
//
//    @BeforeEach
//    void setUp() {
//        this.reportService = Mockito.mock(ReportService.class);
//        reportController = new ReportController(this.reportService);
//    }
//
//    /**
//     * Report controller.
//     */
//    private  ReportController reportController;
//
//    /***
//     * Tests save report.
//     */
//    @Test
//    void shouldSaveReport() {
//        ReportDetails report  = new ReportDetails();
//        report.setData("new report");
//
//        Assertions.assertNotNull(
//                this.reportController.save(report));
//    }
//}
