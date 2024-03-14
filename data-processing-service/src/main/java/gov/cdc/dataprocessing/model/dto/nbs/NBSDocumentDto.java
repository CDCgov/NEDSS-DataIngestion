package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class NBSDocumentDto extends BaseContainer {

    private static final long serialVersionUID = 1L;
    private Long nbsquestionuid;
    private String invFormCode;
    private String questionIdentifier;
    private String questionLabel;
    private String codeSetName;
    private String dataType;
    private Long nbsDocumentUid;
    private Blob docPayload;
    private Blob phdcDocDerived;
    private String payloadViewIndCd;
    private String docTypeCd;
    private Long nbsDocumentMetadataUid;
    private String localId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Long addUserId;
    private Timestamp addTime;
    private String progAreaCd;
    private String jurisdictionCd;
    private String txt;
    private Long programJurisdictionOid;
    private String sharedInd;
    private Integer versionCtrlNbr;
    private String cd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String docPurposeCd;
    private String docStatusCd;
    private String payLoadTxt;
    private String phdcDocDerivedTxt;
    private String cdDescTxt;
    private String sendingFacilityNm;
    private String sendingFacilityOID;
    private Long nbsInterfaceUid;
    private String sendingAppPatientId;
    private String sendingAppEventId;
    private boolean itDirty = false;
    private boolean itNew = true;
    private boolean itDelete = false;
    private String superclass;
    private String xmldocPayload;
    private Integer externalVersionCtrlNbr;
    private Map<Object, Object> eventIdMap = new HashMap<Object, Object>();
    private Object documentObject;
    private String docEventTypeCd;
    private String processingDecisionCd;
    private String processingDecisiontxt;
    private Timestamp effectiveTime;
}
