package gov.cdc.dataingestion.report.repository.model;

import lombok.Data;

/**
 * Report entity.
 */

@Data
@SuppressWarnings("checkstyle:VisibilityModifier")
public class Report {
        /**
         * Id.
         */
        private String id;

        /**
         * report data.
         */
        private String data;

}
