package gov.cdc.dataprocessing.repository.nbs.odse.model.nbs;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.NbsActEntityDT;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "NBS_act_entity")
@Data
public class NbsActEntity {

    @Id
    @Column(name = "nbs_act_entity_uid")
    private Long nbsActEntityUid;

    @Column(name = "act_uid")
    private Long actUid;

    @Column(name = "add_time", nullable = false)
    private Timestamp addTime;

    @Column(name = "add_user_id", nullable = false)
    private Long addUserId;

    @Column(name = "entity_uid", nullable = false)
    private Long entityUid;

    @Column(name = "entity_version_ctrl_nbr", nullable = false)
    private Integer entityVersionCtrlNbr;

    @Column(name = "last_chg_time", nullable = false)
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id", nullable = false)
    private Long lastChgUserId;

    @Column(name = "record_status_cd", nullable = false, length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time", nullable = false)
    private Timestamp recordStatusTime;

    @Column(name = "type_cd", length = 50)
    private String typeCd;

    // Constructors, getters, and setters if needed

    public NbsActEntity() {

    }
    public NbsActEntity(NbsActEntityDT nbsActEntityDT) {
        this.nbsActEntityUid = nbsActEntityDT.getNbsActEntityUid();
        this.addTime = nbsActEntityDT.getAddTime();
        this.addUserId = nbsActEntityDT.getAddUserId();
        this.entityUid = nbsActEntityDT.getEntityUid();
        this.entityVersionCtrlNbr = nbsActEntityDT.getEntityVersionCtrlNbr();
        this.lastChgTime = nbsActEntityDT.getLastChgTime();
        this.lastChgUserId = nbsActEntityDT.getLastChgUserId();
        this.recordStatusCd = nbsActEntityDT.getRecordStatusCd();
        this.recordStatusTime = nbsActEntityDT.getRecordStatusTime();
        this.typeCd = nbsActEntityDT.getTypeCd();
        this.actUid = nbsActEntityDT.getActUid();
    }

}