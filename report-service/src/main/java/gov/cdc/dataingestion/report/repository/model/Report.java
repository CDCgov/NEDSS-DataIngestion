package gov.cdc.dataingestion.report.repository.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Report entity.
 */

@Document(collection = "reports")
@Data
@SuppressWarnings("checkstyle:VisibilityModifier")
public class Report {
        /**
         * Id.
         */
        @Id
        private String id;

        /**
         * report data.
         */
        private String data;

}
