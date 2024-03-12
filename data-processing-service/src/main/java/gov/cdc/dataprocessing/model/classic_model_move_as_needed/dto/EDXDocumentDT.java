package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxDocument;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class EDXDocumentDT extends AbstractVO {
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
    private boolean itDirty = false;
    private boolean itNew = false;
    private boolean itDelete = false;
    private String versionNbr;
    private String viewLink;

    public EDXDocumentDT() {

    }

    public EDXDocumentDT(EdxDocument domain) {
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
