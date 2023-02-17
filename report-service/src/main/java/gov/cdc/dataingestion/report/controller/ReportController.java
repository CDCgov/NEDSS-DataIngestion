package gov.cdc.dataingestion.report.controller;

import gov.cdc.dataingestion.report.integration.service.ReportService;
import gov.cdc.dataingestion.report.repository.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

/**
 * Contains Report API.
 */
@RestController
@RequestMapping("/api/report")
@Slf4j
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
     * @param input reportDetails.
     * @return report id.
     */
    @PostMapping(path = "/save", consumes = "text/csv")
    public ResponseEntity<?> save(
            @RequestBody final String input) {

        Report report = new Report();
        report.setData(input);
        report.setId(UUID.randomUUID().toString());

        var fhirBundle = this.reportService.execute(report);
        log.info("id:{}", fhirBundle);

        return ResponseEntity.ok(fhirBundle);
    }
}

