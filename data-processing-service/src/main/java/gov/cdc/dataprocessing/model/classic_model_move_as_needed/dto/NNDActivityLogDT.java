package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.log.NNDActivityLog;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class NNDActivityLogDT extends AbstractVO
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

    public NNDActivityLogDT() {

    }
    public NNDActivityLogDT(NNDActivityLog activityLog) {
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
