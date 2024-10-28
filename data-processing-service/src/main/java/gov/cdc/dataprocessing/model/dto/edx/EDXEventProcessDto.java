package gov.cdc.dataprocessing.model.dto.edx;

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
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class EDXEventProcessDto extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private Long eDXEventProcessUid;

    private Long nbsDocumentUid;
    private String sourceEventId;
    private Long nbsEventUid;
    private String docEventTypeCd;
    private String docEventSource;
    private Long addUserId;
    private Timestamp addTime;
    private String jurisdictionCd;
    private String progAreaCd;
    private Long programJurisdictionOid;
    private String localId;
    private String parsedInd;
    private Long edxDocumentUid;

}
