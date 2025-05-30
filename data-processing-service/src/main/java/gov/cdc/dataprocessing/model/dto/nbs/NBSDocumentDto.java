package gov.cdc.dataprocessing.model.dto.nbs;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.dto.RootDtoInterface;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

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
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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
    private Map<Object, Object> eventIdMap = new HashMap<>();
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
