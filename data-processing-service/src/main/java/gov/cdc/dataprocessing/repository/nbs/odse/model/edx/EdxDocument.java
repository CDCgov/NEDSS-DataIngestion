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