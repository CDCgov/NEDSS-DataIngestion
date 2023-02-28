package gov.cdc.dataingestion.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Report details.
 */
@Data
@AllArgsConstructor
public final class ReportInfo {
    /**
     * request payload
     */
    private String payload;
}