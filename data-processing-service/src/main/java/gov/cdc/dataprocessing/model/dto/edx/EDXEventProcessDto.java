package gov.cdc.dataprocessing.model.dto.edx;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@SuppressWarnings("all")
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
