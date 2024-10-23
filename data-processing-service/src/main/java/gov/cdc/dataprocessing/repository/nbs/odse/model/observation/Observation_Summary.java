package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import lombok.Data;

import java.sql.Timestamp;

@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class Observation_Summary {
    private Long uid;
    private Timestamp addTime;
    private String addReasonCd;

}
