package gov.cdc.dataprocessing.model.dto.msgoute;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.sql.Timestamp;

@Getter
@Setter
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
