package gov.cdc.dataprocessing.model.dto.log;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.NNDActivityLog;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class NNDActivityLogDto extends BaseContainer
{
    private Long nndActivityLogUid;
    private Integer nndActivityLogSeq;
    private String errorMessageTxt;
    private String localId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String statusCd;
    private Timestamp statusTime;
    private String subjectNm;
    private String service;

    public NNDActivityLogDto() {

    }
    public NNDActivityLogDto(NNDActivityLog activityLog) {
        this.nndActivityLogUid = activityLog.getNndActivityLogUid();
        this.nndActivityLogSeq = activityLog.getNndActivityLogSeq();
        this.errorMessageTxt = activityLog.getErrorMessageTxt();
        this.localId = activityLog.getLocalId();
        this.recordStatusCd = activityLog.getRecordStatusCd();
        this.recordStatusTime = activityLog.getRecordStatusTime();
        this.statusCd = activityLog.getStatusCd();
        this.statusTime = activityLog.getStatusTime();
        this.service = activityLog.getService();
    }
}
