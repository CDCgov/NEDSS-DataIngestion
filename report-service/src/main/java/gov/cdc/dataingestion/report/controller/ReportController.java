package gov.cdc.dataingestion.report.controller;

import gov.cdc.dataingestion.report.integration.service.ReportService;
import gov.cdc.dataingestion.report.model.ReportDetails;
import gov.cdc.dataingestion.report.repository.model.Report;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

/**
 * Contains Report API.
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    /**
     * Report service.
     */
    private final ReportService reportService;

    /**
     * Designated constructor.
     * @param reportService report service.
     */
    public ReportController(final ReportService reportService) {
          this.reportService = reportService;
     }

    /**
     * Saves Report.
     * @param reportDetails reportDetails.
     * @return report id.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(
            @RequestBody final ReportDetails reportDetails) {


        Report report = new Report();
        report.setId(UUID.randomUUID().toString());
        report.setData(reportDetails.getData());

        String id = this.reportService.save(report);
        return ResponseEntity.ok(id);
    }

    /**
     * Gets report status.
     * @return report status.
     */
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> reports() {
        return ResponseEntity.ok(this.reportService.findAll());
    }
}

