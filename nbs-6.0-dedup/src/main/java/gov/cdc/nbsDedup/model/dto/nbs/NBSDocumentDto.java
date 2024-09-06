package gov.cdc.nbsDedup.model.dto.nbs;


import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import gov.cdc.nbsDedup.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class NBSDocumentDto extends BaseContainer implements RootDtoInterface {

    private static final long serialVersionUID = 1L;
    private Long nbsquestionuid;
    private String invFormCode;
    private String questionIdentifier;
    private String questionLabel;
    private String codeSetName;
    private String dataType;
    private Long nbsDocumentUid;
    private String docPayload;
    private String phdcDocDerived;
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

    private String superclass;
    private String xmldocPayload;
    private Integer externalVersionCtrlNbr;
    private Map<Object, Object> eventIdMap = new HashMap<Object, Object>();
    private Object documentObject;
    private String docEventTypeCd;
    private String processingDecisionCd;
    private String processingDecisiontxt;
    private Timestamp effectiveTime;

    public NBSDocumentDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    @Override
    public String getLastChgReasonCd() {
        return null;
    }

    @Override
    public void setLastChgReasonCd(String aLastChgReasonCd) {

    }

    @Override
    public String getStatusCd() {
        return null;
    }

    @Override
    public void setStatusCd(String aStatusCd) {

    }

    @Override
    public Timestamp getStatusTime() {
        return null;
    }

    @Override
    public void setStatusTime(Timestamp aStatusTime) {

    }

    @Override
    public Long getUid() {
        return null;
    }
}
