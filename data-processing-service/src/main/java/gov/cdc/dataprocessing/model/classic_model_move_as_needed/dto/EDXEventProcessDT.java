package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class EDXEventProcessDT extends BaseContainer {
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
