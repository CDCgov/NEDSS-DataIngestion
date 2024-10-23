package gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class PrepareEntity {
    private String localId = null;
    private Long addUserId = null;
    private Timestamp addUserTime = null;
    private String recordStatusState = null;
    private String objectStatusState = null;
}