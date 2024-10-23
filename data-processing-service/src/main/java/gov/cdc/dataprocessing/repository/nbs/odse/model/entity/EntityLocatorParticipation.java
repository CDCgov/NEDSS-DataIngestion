package gov.cdc.dataprocessing.repository.nbs.odse.model.entity;

import gov.cdc.dataprocessing.model.dto.entity.EntityLocatorParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.EntityLocatorParticipationId;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Entity
@Table(name = "Entity_locator_participation", schema = "dbo")
@IdClass(EntityLocatorParticipationId.class) // Specify the IdClass
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class EntityLocatorParticipation {

    @Id
    @Column(name = "entity_uid", nullable = false)
    private Long entityUid;

    @Id
    @Column(name = "locator_uid", nullable = false)
    private Long locatorUid;

 //   @Version
    @Column(name = "version_ctrl_nbr", nullable = false)
    private Integer versionCtrlNbr;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd", length = 50)
    private String cd;

    @Column(name = "cd_desc_txt", length = 100)
    private String cdDescTxt;

    @Column(name = "class_cd", length = 10)
    private String classCd;

    @Column(name = "duration_amt", length = 20)
    private String durationAmt;

    @Column(name = "duration_unit_cd", length = 20)
    private String durationUnitCd;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "locator_desc_txt", length = 2000)
    private String locatorDescTxt;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "status_cd", length = 1)
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "use_cd", length = 20)
    private String useCd;

    @Column(name = "user_affiliation_txt", length = 20)
    private String userAffiliationTxt;

    @Column(name = "valid_time_txt", length = 100)
    private String validTimeTxt;

    @Column(name = "as_of_date")
    private Timestamp asOfDate;

    // Add getters and setters as needed
    public EntityLocatorParticipation(EntityLocatorParticipationDto entityLocatorParticipationDto) {
        var timestamp = getCurrentTimeStamp();
        this.entityUid = entityLocatorParticipationDto.getEntityUid();
        this.locatorUid = entityLocatorParticipationDto.getLocatorUid();

        if (entityLocatorParticipationDto.getAddReasonCd() == null) {
            this.addReasonCd = "Add";
        } else {
            this.addReasonCd = entityLocatorParticipationDto.getAddReasonCd();
        }

        if (entityLocatorParticipationDto.getAddUserId() == null) {
            this.addUserId = AuthUtil.authUser.getAddUserId();
            this.addTime = timestamp;
        } else {
            this.addUserId = entityLocatorParticipationDto.getAddUserId();
            this.addTime = entityLocatorParticipationDto.getAddTime();
        }
        
        this.cd = entityLocatorParticipationDto.getCd();
        this.cdDescTxt = entityLocatorParticipationDto.getCdDescTxt();
        this.classCd = entityLocatorParticipationDto.getClassCd();
        this.durationAmt = entityLocatorParticipationDto.getDurationAmt();
        this.durationUnitCd = entityLocatorParticipationDto.getDurationUnitCd();
        this.fromTime = entityLocatorParticipationDto.getFromTime();
        this.lastChgReasonCd = entityLocatorParticipationDto.getLastChgReasonCd();
        this.lastChgTime = entityLocatorParticipationDto.getLastChgTime();
        this.lastChgUserId = entityLocatorParticipationDto.getLastChgUserId();
        this.locatorDescTxt = entityLocatorParticipationDto.getLocatorDescTxt();
        this.recordStatusCd = entityLocatorParticipationDto.getRecordStatusCd();
        this.recordStatusTime = entityLocatorParticipationDto.getRecordStatusTime();
        this.statusCd = entityLocatorParticipationDto.getStatusCd();
        this.statusTime = entityLocatorParticipationDto.getStatusTime();
        this.toTime = entityLocatorParticipationDto.getToTime();
        this.useCd = entityLocatorParticipationDto.getUseCd();
        this.userAffiliationTxt = entityLocatorParticipationDto.getUserAffiliationTxt();
        this.validTimeTxt = entityLocatorParticipationDto.getValidTimeTxt();
        this.versionCtrlNbr = entityLocatorParticipationDto.getVersionCtrlNbr();

        if (entityLocatorParticipationDto.getAsOfDate() == null) {
            this.asOfDate = timestamp;
        } else {
            this.asOfDate = entityLocatorParticipationDto.getAsOfDate();
        }
    }

    public EntityLocatorParticipation() {

    }
}
