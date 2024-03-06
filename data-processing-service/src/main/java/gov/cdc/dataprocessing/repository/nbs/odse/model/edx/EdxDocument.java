package gov.cdc.dataprocessing.repository.nbs.odse.model.edx;


import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXDocumentDT;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "edx_document")
public class EdxDocument  {

    @Id
    @Column(name = "edx_document_uid")
    private Long eDXDocumentUid;

    @Column(name = "act_uid")
    private Long actUid;

    @Column(name = "payload")
    private String payload;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "doc_type_cd")
    private String docTypeCd;

    @Column(name = "nbs_document_metadata_uid")
    private Long nbsDocumentMetadataUid;

    @Column(name = "original_payload")
    private String originalPayload;

    @Column(name = "original_doc_type_cd")
    private String originalDocTypeCd;

    @Column(name = "edx_document_parent_uid")
    private Long edxDocumentParentUid;


    public EdxDocument() {

    }

    public EdxDocument(EDXDocumentDT dto) {
        this.eDXDocumentUid = dto.getEDXDocumentUid();
        this.actUid = dto.getActUid();
        this.payload = dto.getPayload();
        this.recordStatusCd = dto.getRecordStatusCd();
        this.recordStatusTime = dto.getRecordStatusTime();
        this.addTime = dto.getAddTime();
        this.docTypeCd = dto.getDocTypeCd();
        this.nbsDocumentMetadataUid = dto.getNbsDocumentMetadataUid();
        this.originalPayload = dto.getOriginalPayload();
        this.originalDocTypeCd = dto.getOriginalDocTypeCd();
        this.edxDocumentParentUid = dto.getEdxDocumentParentUid();
        // Set other fields as needed
    }


}