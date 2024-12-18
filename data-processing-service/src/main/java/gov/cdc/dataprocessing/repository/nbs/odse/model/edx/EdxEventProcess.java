package gov.cdc.dataprocessing.repository.nbs.odse.model.edx;

import gov.cdc.dataprocessing.model.dto.edx.EDXEventProcessDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "EDX_event_process")
@Data
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
