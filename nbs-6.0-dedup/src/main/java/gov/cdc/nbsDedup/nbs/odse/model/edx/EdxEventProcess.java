package gov.cdc.nbsDedup.nbs.odse.model.edx;

import gov.cdc.nbsDedup.model.dto.edx.EDXEventProcessDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "EDX_event_process")
@Data
public class EdxEventProcess {

    @Id
    @Column(name = "edx_event_process_uid")
    private Long edxEventProcessUid;

    @Column(name = "nbs_document_uid")
    private Long nbsDocumentUid;

    @Column(name = "nbs_event_uid", nullable = false)
    private Long nbsEventUid;

    @Column(name = "source_event_id")
    private String sourceEventId;

    @Column(name = "doc_event_type_cd", nullable = false)
    private String docEventTypeCd;

    @Column(name = "add_user_id", nullable = false)
    private Long addUserId;

    @Column(name = "add_time", nullable = false)
    private Timestamp addTime;

    @Column(name = "parsed_ind")
    private String parsedInd;

    @Column(name = "edx_document_uid")
    private Long edxDocumentUid;

    public EdxEventProcess() {

    }

    public EdxEventProcess(EDXEventProcessDto edxEventProcessDto) {
        this.nbsDocumentUid = edxEventProcessDto.getNbsDocumentUid();
        this.nbsEventUid = edxEventProcessDto.getNbsEventUid();
        this.sourceEventId = edxEventProcessDto.getSourceEventId();
        this.docEventTypeCd = edxEventProcessDto.getDocEventTypeCd();
        this.addUserId = edxEventProcessDto.getAddUserId();
        this.addTime = edxEventProcessDto.getAddTime();
        this.parsedInd = edxEventProcessDto.getParsedInd();
        this.edxDocumentUid = edxEventProcessDto.getEdxDocumentUid();
    }
}
