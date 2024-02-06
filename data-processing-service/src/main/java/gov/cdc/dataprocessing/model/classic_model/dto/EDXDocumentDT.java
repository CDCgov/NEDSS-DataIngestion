package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
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
}
