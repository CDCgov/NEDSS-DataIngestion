package gov.cdc.dataprocessing.model.dto.msgoute;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class NbsInterfaceDto extends BaseContainer {
    private Long nbsInterfaceUid;
    private Blob payload;
    private String impExpIndCd;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String sendingSystemNm;
    private Timestamp addTime;
    private String receivingSystemNm;
    private Long notificationUid;
    private Long nbsDocumentUid;
    private String xmlPayLoadContent;
    private String systemNm;
    private String docTypeCd;
    private String cdaPayload;
    private Long observationUid;
    private String originalPayload;
    private String originalDocTypeCd;
}
