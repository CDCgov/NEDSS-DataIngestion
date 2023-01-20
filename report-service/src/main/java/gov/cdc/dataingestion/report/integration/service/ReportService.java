package gov.cdc.dataingestion.report.integration.service;

import gov.cdc.dataingestion.report.repository.IReportRepository;
import gov.cdc.dataingestion.report.repository.model.Report;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Report service.
 */
@Service
public class ReportService {

    /**
     * Report repository.
     */
    private final IReportRepository reportRepository;

    /**
     * Designated constructor.
     * @param reportRepository report repository.
     */
    public ReportService(final IReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Saves report.
     *
     * @param report
     * @return report id.
     */
    public String save(final Report report) {
        return this.reportRepository.save(report).getId();
    }

    /**
     * Retrieves all reports.
     * @return list of reports.
     */
    public List<Report> findAll() {
        return this.reportRepository.findAll();
    }
}
