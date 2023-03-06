package gov.cdc.dataingestion.report.integration.service;

import gov.cdc.dataingestion.report.integration.service.convert.IConvertCsvToHl7Service;
import gov.cdc.dataingestion.report.integration.service.convert.IConvertToFhirService;
import gov.cdc.dataingestion.report.integration.service.schema.LoadSchemasService;
import gov.cdc.dataingestion.report.model.Report;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
     * Load schema service.
     */
    private final LoadSchemasService loadSchemasService;

    /**
     * Convert csv to Hl7 service.
     */
    private final IConvertCsvToHl7Service convertCsvToHl7Service;

    /**
     * Designated constructor.
     * @param convertToFhirService   Convert to Fhir Service.
     * @param loadSchemasService     schema service
     * @param convertCsvToHl7Service Convert csv to Hl7 service.
     */
    public ReportService(
            final IConvertToFhirService convertToFhirService,
            final LoadSchemasService loadSchemasService,
            final IConvertCsvToHl7Service convertCsvToHl7Service) {
        this.convertToFhirService = convertToFhirService;
        this.loadSchemasService = loadSchemasService;
        this.convertCsvToHl7Service = convertCsvToHl7Service;
    }

    /**
     * Saves report.
     * @param input Report
     * @return report id.
     */
    @Override
    public String execute(final Report input) {

        // Get schema for the requested client
         var schemas = this.loadSchemasService.execute("");
         input.setSchema(
                 schemas.get(this.getSenderSchemaName(input.getClientName())));


         // Map , convert to Hl7 message
       var message =  this.convertCsvToHl7Service.execute(input);

        // Convert to Fhir.
         return this.convertToFhirService
                    .execute(message);
    }

    @NotNull
    private String getSenderSchemaName(@NotNull final String senderName) {
        //TODO Integrate with sender details.
        return "pdi-covid-19";
    }
}
