package gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed;


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

}