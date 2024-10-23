package gov.cdc.dataprocessing.repository.nbs.odse.model.nbs;

import gov.cdc.dataprocessing.model.dto.nbs.NbsNoteDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "NBS_note")
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class NbsNote {
    @Id
    @Column(name = "nbs_note_uid")
    private Long nbsNoteUid;

    @Column(name = "note_parent_uid", nullable = false)
    private Long noteParentUid;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time", nullable = false)
    private Timestamp recordStatusTime;

    @Column(name = "add_time", nullable = false)
    private Timestamp addTime;

    @Column(name = "add_user_id", nullable = false)
    private Long addUserId;

    @Column(name = "last_chg_time", nullable = false)
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id", nullable = false)
    private Long lastChgUserId;

    @Column(name = "note", nullable = false)
    private String note;

    @Column(name = "private_ind_cd", nullable = false)
    private String privateIndCd;

    @Column(name = "type_cd", nullable = false)
    private String typeCd;

    public NbsNote() {

    }

    public NbsNote(NbsNoteDto nbsNoteDto) {
        this.nbsNoteUid = nbsNoteDto.getNbsNoteUid();
        this.noteParentUid = nbsNoteDto.getNoteParentUid();
        this.recordStatusCd = nbsNoteDto.getRecordStatusCd();
        this.recordStatusTime = nbsNoteDto.getRecordStatusTime();
        this.addTime = nbsNoteDto.getAddTime();
        this.addUserId = nbsNoteDto.getAddUserId();
        this.lastChgTime = nbsNoteDto.getLastChgTime();
        this.lastChgUserId = nbsNoteDto.getLastChgUserId();
        this.note = nbsNoteDto.getNote();
        this.privateIndCd = nbsNoteDto.getPrivateIndCd();
        this.typeCd = nbsNoteDto.getTypeCd();
    }
}
