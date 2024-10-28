package gov.cdc.dataprocessing.model.dto.act;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
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
public class ActivityLocatorParticipationDto extends BaseContainer {
    private static final long serialVersionUID = 1L;
    private Long actUid;
    private Long locatorUid;
    private Long entityUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String durationAmt;
    private String durationUnitCd;
    private Timestamp fromTime;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Timestamp toTime;
    private String statusCd;
    private Timestamp statusTime;
    private String typeCd;
    private String typeDescTrxt;
    private String userAffiliationTxt;

    public ActivityLocatorParticipationDto() {

    }

    public ActivityLocatorParticipationDto(ActLocatorParticipation domain) {
        this.actUid = domain.getActUid();
        this.locatorUid = domain.getLocatorUid();
        this.entityUid = domain.getEntityUid();
        this.addReasonCd = domain.getAddReasonCd();
        this.addTime = domain.getAddTime();
        this.addUserId = domain.getAddUserId();
        this.durationAmt = domain.getDurationAmount();
        this.durationUnitCd = domain.getDurationUnitCd();
        this.fromTime = domain.getFromTime();
        this.lastChgReasonCd = domain.getLastChangeReasonCd();
        this.lastChgTime = domain.getLastChangeTime();
        this.lastChgUserId = domain.getLastChangeUserId();
        this.recordStatusCd = domain.getRecordStatusCd();
        this.recordStatusTime = domain.getRecordStatusTime();
        this.toTime = domain.getToTime();
        this.statusCd = domain.getStatusCd();
        this.statusTime = domain.getStatusTime();
        this.typeCd = domain.getTypeCd();
        this.typeDescTrxt = domain.getTypeDescTxt(); // Check this field name
        this.userAffiliationTxt = domain.getUserAffiliationTxt();
    }

}
