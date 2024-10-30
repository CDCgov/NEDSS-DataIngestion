package gov.cdc.dataprocessing.repository.nbs.odse.model.log;

import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "EDX_activity_detail_log")
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
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