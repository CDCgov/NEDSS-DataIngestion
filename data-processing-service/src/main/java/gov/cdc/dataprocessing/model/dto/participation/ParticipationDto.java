package gov.cdc.dataprocessing.model.dto.participation;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter

public class ParticipationDto extends BaseContainer {
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String awarenessCd;
    private String awarenessDescTxt;
    private String durationAmt;
    private String durationUnitCd;
    private Timestamp fromTime;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private String statusCd;
    private Timestamp statusTime;
    private String typeCd;
    private Timestamp toTime;
    private String typeDescTxt;
    private String userAffiliationTxt;
    private String subjectEntityClassCd;
    private Long subjectEntityUid;
    private Long roleSeq;
    private String cd;
    private String actClassCd;
    private String subjectClassCd;
    private Long actUid;

    public ParticipationDto() {

    }

    public ParticipationDto(Participation participation) {
        this.subjectEntityUid = participation.getSubjectEntityUid();
        this.actUid = participation.getActUid();
        this.typeCd = participation.getTypeCode();
        this.actClassCd = participation.getActClassCode();
        this.addReasonCd = participation.getAddReasonCode();
        this.addTime = participation.getAddTime();
        this.addUserId = participation.getAddUserId();
        this.awarenessCd = participation.getAwarenessCode();
        this.awarenessDescTxt = participation.getAwarenessDescription();
        this.durationAmt = participation.getDurationAmount();
        this.durationUnitCd = participation.getDurationUnitCode();
        this.fromTime = participation.getFromTime();
        this.lastChgReasonCd = participation.getLastChangeReasonCode();
        this.lastChgTime = participation.getLastChangeTime();
        this.lastChgUserId = participation.getLastChangeUserId();
        this.recordStatusCd = participation.getRecordStatusCode();
        this.recordStatusTime = participation.getRecordStatusTime();
        this.subjectEntityClassCd = participation.getSubjectClassCode();
        this.cd = participation.getCode();
        this.roleSeq = participation.getRoleSeq();
        this.statusCd = participation.getStatusCode();
        this.statusTime = participation.getStatusTime();
        this.subjectClassCd = participation.getSubjectClassCode();
        this.toTime = participation.getToTime();
        this.typeDescTxt = participation.getTypeDescription();
        this.userAffiliationTxt = participation.getUserAffiliationText();
    }
}
