package gov.cdc.dataprocessing.repository.nbs.odse.model.phc;

import gov.cdc.dataprocessing.model.dto.phc.EntityGroupDto;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Entity_group")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class EntityGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entity_group_uid")
    private Long entityGroupUid;

    @Column(name = "add_reason_cd")
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "cd")
    private String cd;

    @Column(name = "cd_desc_txt")
    private String cdDescTxt;

    @Column(name = "description")
    private String description;

    @Column(name = "duration_amt")
    private String durationAmt;

    @Column(name = "duration_unit_cd")
    private String durationUnitCd;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "group_cnt")
    private Integer groupCnt;

    @Column(name = "last_chg_reason_cd")
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "local_id")
    private String localId;

    @Column(name = "nm")
    private String nm;

    @Column(name = "record_status_cd")
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "to_time")
    private Timestamp toTime;

    @Column(name = "user_affiliation_txt")
    private String userAffiliationTxt;

    @Column(name = "version_ctrl_nbr")
    private Integer versionCtrlNbr;

    // Constructors, getters, and setters
    public EntityGroup() {

    }

    public EntityGroup(EntityGroupDto entityGroupDto) {
        this.entityGroupUid = entityGroupDto.getEntityGroupUid();
        this.addReasonCd = entityGroupDto.getAddReasonCd();
        this.addTime = entityGroupDto.getAddTime();
        this.addUserId = entityGroupDto.getAddUserId();
        this.cd = entityGroupDto.getCd();
        this.cdDescTxt = entityGroupDto.getCdDescTxt();
        this.description = entityGroupDto.getDescription();
        this.durationAmt = entityGroupDto.getDurationAmt();
        this.durationUnitCd = entityGroupDto.getDurationUnitCd();
        this.fromTime = entityGroupDto.getFromTime();
        this.groupCnt = entityGroupDto.getGroupCnt();
        this.lastChgReasonCd = entityGroupDto.getLastChgReasonCd();
        this.lastChgTime = entityGroupDto.getLastChgTime();
        this.lastChgUserId = entityGroupDto.getLastChgUserId();
        this.localId = entityGroupDto.getLocalId();
        this.nm = entityGroupDto.getNm();
        this.recordStatusCd = entityGroupDto.getRecordStatusCd();
        this.recordStatusTime = entityGroupDto.getRecordStatusTime();
        this.statusCd = entityGroupDto.getStatusCd();
        this.statusTime = entityGroupDto.getStatusTime();
        this.toTime = entityGroupDto.getToTime();
        this.userAffiliationTxt = entityGroupDto.getUserAffiliationTxt();
        this.versionCtrlNbr = entityGroupDto.getVersionCtrlNbr();
    }

}