package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.EDXEventProcessDT;
import jakarta.persistence.*;
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

    public EdxEventProcess(EDXEventProcessDT edxEventProcessDT) {
        this.nbsDocumentUid = edxEventProcessDT.getNbsDocumentUid();
        this.nbsEventUid = edxEventProcessDT.getNbsEventUid();
        this.sourceEventId = edxEventProcessDT.getSourceEventId();
        this.docEventTypeCd = edxEventProcessDT.getDocEventTypeCd();
        this.addUserId = edxEventProcessDT.getAddUserId();
        this.addTime = edxEventProcessDT.getAddTime();
        this.parsedInd = edxEventProcessDT.getParsedInd();
        this.edxDocumentUid = edxEventProcessDT.getEdxDocumentUid();
    }
}
