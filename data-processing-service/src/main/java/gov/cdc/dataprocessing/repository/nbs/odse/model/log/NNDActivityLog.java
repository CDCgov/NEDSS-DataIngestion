package gov.cdc.dataprocessing.repository.nbs.odse.model.log;

import gov.cdc.dataprocessing.model.dto.log.NNDActivityLogDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.NNDActivityLogId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "NND_Activity_log")
@Data
@IdClass(NNDActivityLogId.class)
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
public class NNDActivityLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "nnd_activity_log_uid")
    private Long nndActivityLogUid;

    @Id
    @Column(name = "nnd_activity_log_seq")
    private Integer nndActivityLogSeq;

    @Column(name = "error_message_txt", length = 2000, nullable = false)
    private String errorMessageTxt;

    @Column(name = "local_id", length = 50, nullable = false)
    private String localId;

    @Column(name = "record_status_cd", length = 20, nullable = false)
    private String recordStatusCd;

    @Column(name = "record_status_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp recordStatusTime;

    @Column(name = "status_cd", length = 1, nullable = false)
    private String  statusCd;

    @Column(name = "status_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp statusTime;

    @Column(name = "service", length = 300)
    private String service;

    public NNDActivityLog() {

    }
    public NNDActivityLog(NNDActivityLogDto activityLogDT) {
        this.nndActivityLogUid = activityLogDT.getNndActivityLogUid();
        this.nndActivityLogSeq = activityLogDT.getNndActivityLogSeq();
        this.errorMessageTxt = activityLogDT.getErrorMessageTxt();
        this.localId = activityLogDT.getLocalId();
        this.recordStatusCd = activityLogDT.getRecordStatusCd();
        this.recordStatusTime = activityLogDT.getRecordStatusTime();
        this.statusCd = activityLogDT.getStatusCd();
        this.statusTime = activityLogDT.getStatusTime();
        this.service = activityLogDT.getService();
    }

}