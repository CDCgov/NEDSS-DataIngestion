package gov.cdc.dataprocessing.repository.nbs.odse.model.nbs;


import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "NBS_act_entity_hist")
@Data
public class NbsActEntityHist {

    @Id
    @Column(name = "nbs_act_entity_hist_uid")
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

    public NbsActEntityHist() {

    }

    public NbsActEntityHist(NbsActEntityDto nbsActEntityDto) {
        this.nbsActEntityUid = nbsActEntityDto.getNbsActEntityUid();
        this.addTime = nbsActEntityDto.getAddTime();
        this.addUserId = nbsActEntityDto.getAddUserId();
        this.entityUid = nbsActEntityDto.getEntityUid();
        this.entityVersionCtrlNbr = nbsActEntityDto.getEntityVersionCtrlNbr();
        this.lastChgTime = nbsActEntityDto.getLastChgTime();
        this.lastChgUserId = nbsActEntityDto.getLastChgUserId();
        this.recordStatusCd = nbsActEntityDto.getRecordStatusCd();
        this.recordStatusTime = nbsActEntityDto.getRecordStatusTime();
        this.typeCd = nbsActEntityDto.getTypeCd();
        this.actUid = nbsActEntityDto.getActUid();
    }

}