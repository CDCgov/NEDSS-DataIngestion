package gov.cdc.dataingestion.report.model;

import gov.cdc.dataingestion.report.model.Schema;
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
         * report sender name.
         */
        private String clientName;

        /**
         * report data.
         */
        private String data;

        /**
         * schema.
         */
        private Schema schema;

}
