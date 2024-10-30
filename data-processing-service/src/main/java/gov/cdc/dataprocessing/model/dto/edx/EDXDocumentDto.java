package gov.cdc.dataprocessing.model.dto.edx;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxDocument;
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
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class EDXDocumentDto extends BaseContainer {
    private Long eDXDocumentUid;
    private Long actUid;
    private String payload;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Timestamp addTime;
    private String docTypeCd;
    private Long nbsDocumentMetadataUid;
    private String originalPayload;
    private String originalDocTypeCd;
    private Long edxDocumentParentUid;


    // Not in DB
    private String documentViewXsl;
    private String xmlSchemaLocation;
    private String progAreaCd = null;
    private String jurisdictionCd = null;
    private Long programJurisdictionOid = null;
    private String sharedInd = null;

    private String versionNbr;
    private String viewLink;

    public EDXDocumentDto() {
           itDirty = false;
           itNew = false;
           itDelete = false;
    }

    public EDXDocumentDto(EdxDocument domain) {
        this.eDXDocumentUid = domain.getId();
        this.actUid = domain.getActUid();
        this.payload = domain.getPayload();
        this.recordStatusCd = domain.getRecordStatusCd();
        this.recordStatusTime = domain.getRecordStatusTime();
        this.addTime = domain.getAddTime();
        this.docTypeCd = domain.getDocTypeCd();
        this.nbsDocumentMetadataUid = domain.getNbsDocumentMetadataUid();
        this.originalPayload = domain.getOriginalPayload();
        this.originalDocTypeCd = domain.getOriginalDocTypeCd();
        this.edxDocumentParentUid = domain.getEdxDocumentParentUid();
        // Set other fields as needed
    }

}
