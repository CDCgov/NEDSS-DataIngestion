package gov.cdc.dataprocessing.model.dto.participation;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
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
