package gov.cdc.dataprocessing.model.dto.matching;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
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
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
public class EdxEntityMatchDto extends BaseContainer {
    private Long edxEntityMatchUid;
    private Long entityUid;
    private String matchString;
    private String typeCd;
    private Long matchStringHashCode;

    private Long addUserId;
    private Long lastChgUserId;
    private Timestamp addTime;
    private Timestamp lastChgTime;

}
