package gov.cdc.dataprocessing.repository.nbs.odse.model;

import gov.cdc.dataprocessing.model.classic_model.dto.EntityLocatorParticipationDT;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.EntityLocatorParticipationId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.PersonEthnicGroupId;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;

import static gov.cdc.dataprocessing.utilities.time.TimeStampUtil.getCurrentTimeStamp;

@Entity
@Table(name = "Entity_locator_participation", schema = "dbo")
@IdClass(EntityLocatorParticipationId.class) // Specify the IdClass
@Data
public class EntityLocatorParticipation {

    @Id
    @Column(name = "entity_uid", nullable = false)
    private Long entityUid;

    @Id
    @Column(name = "locator_uid", nullable = false)
    private Long locatorUid;

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

    @Column(name = "version_ctrl_nbr", nullable = false)
    private Integer versionCtrlNbr;

    @Column(name = "as_of_date")
    private Timestamp asOfDate;

    // Add getters and setters as needed
    public EntityLocatorParticipation(EntityLocatorParticipationDT entityLocatorParticipationDT) {
        var timestamp = getCurrentTimeStamp();
        this.entityUid = entityLocatorParticipationDT.getEntityUid();
        this.locatorUid = entityLocatorParticipationDT.getLocatorUid();
        this.addReasonCd = entityLocatorParticipationDT.getAddReasonCd();
        this.addTime = entityLocatorParticipationDT.getAddTime();
        this.addUserId = entityLocatorParticipationDT.getAddUserId();
        this.cd = entityLocatorParticipationDT.getCd();
        this.cdDescTxt = entityLocatorParticipationDT.getCdDescTxt();
        this.classCd = entityLocatorParticipationDT.getClassCd();
        this.durationAmt = entityLocatorParticipationDT.getDurationAmt();
        this.durationUnitCd = entityLocatorParticipationDT.getDurationUnitCd();
        this.fromTime = entityLocatorParticipationDT.getFromTime();
        this.lastChgReasonCd = entityLocatorParticipationDT.getLastChgReasonCd();
        this.lastChgTime = entityLocatorParticipationDT.getLastChgTime();
        this.lastChgUserId = entityLocatorParticipationDT.getLastChgUserId();
        this.locatorDescTxt = entityLocatorParticipationDT.getLocatorDescTxt();
        this.recordStatusCd = entityLocatorParticipationDT.getRecordStatusCd();
        this.recordStatusTime = entityLocatorParticipationDT.getRecordStatusTime();
        this.statusCd = entityLocatorParticipationDT.getStatusCd();
        this.statusTime = entityLocatorParticipationDT.getStatusTime();
        this.toTime = entityLocatorParticipationDT.getToTime();
        this.useCd = entityLocatorParticipationDT.getUseCd();
        this.userAffiliationTxt = entityLocatorParticipationDT.getUserAffiliationTxt();
        this.validTimeTxt = entityLocatorParticipationDT.getValidTimeTxt();
        this.versionCtrlNbr = entityLocatorParticipationDT.getVersionCtrlNbr();
        if (entityLocatorParticipationDT.getAsOfDate() == null) {
            this.asOfDate = timestamp;
        } else {
            this.asOfDate = entityLocatorParticipationDT.getAsOfDate();
        }
    }

    public EntityLocatorParticipation() {

    }
}
