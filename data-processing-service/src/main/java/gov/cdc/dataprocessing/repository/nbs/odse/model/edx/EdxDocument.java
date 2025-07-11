package gov.cdc.dataprocessing.repository.nbs.odse.model.edx;


import gov.cdc.dataprocessing.model.dto.edx.EDXDocumentDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "EDX_Document")

public class EdxDocument  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EDX_Document_uid")
    private Long id;

    @Column(name = "act_uid")
    private Long actUid;

    @Column(name = "payload", columnDefinition = "xml", nullable = false)
    private String payload;

    @Column(name = "record_status_cd", nullable = false, length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time", nullable = false)
    private Timestamp recordStatusTime;

    @Column(name = "add_time", nullable = false)
    private Timestamp addTime;

    @Column(name = "doc_type_cd", nullable = false, length = 20)
    private String docTypeCd;

    @Column(name = "nbs_document_metadata_uid", nullable = false)
    private Long nbsDocumentMetadataUid;

    @Column(name = "original_payload", columnDefinition = "varchar(max)")
    private String originalPayload;

    @Column(name = "original_doc_type_cd", length = 20)
    private String originalDocTypeCd;

    @Column(name = "edx_document_parent_uid")
    private Long edxDocumentParentUid;

    public EdxDocument() {

    }

    public EdxDocument(EDXDocumentDto dto) {
        this.id = dto.getEDXDocumentUid();
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