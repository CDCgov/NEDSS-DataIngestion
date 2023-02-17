package gov.cdc.dataingestion.report.integration.service;

import gov.cdc.dataingestion.report.integration.service.convert.IConvertToFhirService;
import gov.cdc.dataingestion.report.model.ReportInfo;
import gov.cdc.dataingestion.report.repository.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Report service.
 */
@Service
@Slf4j
public class ReportService implements IReportService {

    /**
     * Convert to Fhir Service.
     */
    private final IConvertToFhirService convertToFhirService;

    /**
     * Designated constructor.
     *
     * @param convertToFhirService Convert to Fhir Service.
     */
    public ReportService(final IConvertToFhirService convertToFhirService) {
        this.convertToFhirService = convertToFhirService;
    }

    /**
     * Saves report.
     *
     * @param input Report
     * @return report id.
     */
    @Override
    public String execute(final Report input) {
            return this.convertToFhirService
                    .execute(new ReportInfo(input.getData()));
    }
}
