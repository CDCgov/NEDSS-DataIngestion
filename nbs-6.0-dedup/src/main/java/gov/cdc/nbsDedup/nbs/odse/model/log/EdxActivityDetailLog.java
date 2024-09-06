package gov.cdc.nbsDedup.nbs.odse.model.log;

import gov.cdc.nbsDedup.model.dto.log.EDXActivityDetailLogDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EDX_activity_detail_log")
public class EdxActivityDetailLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edx_activity_detail_log_uid", nullable = false)
    private Long id;

//    @NotNull
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "edx_activity_log_uid", nullable = false)
//    private EdxActivityLog edxActivityLogUid;

    @Column(name = "edx_activity_log_uid", nullable = false)
    private Long edxActivityLogUid;

    @Size(max = 256)
    @Column(name = "record_id", length = 256)
    private String recordId;

    @Size(max = 50)
    @Column(name = "record_type", length = 50)
    private String recordType;

    @Size(max = 250)
    @Column(name = "record_nm", length = 250)
    private String recordNm;

    @Size(max = 50)
    @Column(name = "log_type", length = 50)
    private String logType;

    @Size(max = 2000)
    @Column(name = "log_comment", length = 2000)
    private String logComment;

    public EdxActivityDetailLog() {
    }
    public EdxActivityDetailLog(EDXActivityDetailLogDto eDXActivityDtlLogDto) {
        //this.id=eDXActivityDtlLogDto.getEdxActivityDetailLogUid();
        this.edxActivityLogUid = eDXActivityDtlLogDto.getEdxActivityLogUid();
        this.recordId = eDXActivityDtlLogDto.getRecordId();
        this.recordType = eDXActivityDtlLogDto.getRecordType();
        this.recordNm = eDXActivityDtlLogDto.getRecordName();
        this.logType = eDXActivityDtlLogDto.getLogType();
        this.logComment = eDXActivityDtlLogDto.getComment();
    }
}
