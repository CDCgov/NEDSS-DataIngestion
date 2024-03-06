package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObsValueDateDT;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "Obs_value_date")
@Data
public class ObsValueDate implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "observation_uid")
    private Long observationUid;

    @Id
    @Column(name = "obs_value_date_seq")
    private Integer obsValueDateSeq;

    @Column(name = "duration_amt")
    private String durationAmt;

    @Column(name = "duration_unit_cd")
    private String durationUnitCd;

    @Column(name = "from_time")
    private Timestamp fromTime;

    @Column(name = "to_time")
    private Timestamp toTime;

//    @ManyToOne
//    @JoinColumn(name = "observation_uid", referencedColumnName = "observation_uid", insertable = false, updatable = false)
//    private Observation observation;

    // Constructors, getters, and setters

    public ObsValueDate() {

    }

    public ObsValueDate(ObsValueDateDT obsValueDateDT) {
        this.observationUid = obsValueDateDT.getObservationUid();
        this.obsValueDateSeq = obsValueDateDT.getObsValueDateSeq();
        this.durationAmt = obsValueDateDT.getDurationAmt();
        this.durationUnitCd = obsValueDateDT.getDurationUnitCd();
        this.fromTime = obsValueDateDT.getFromTime();
        this.toTime = obsValueDateDT.getToTime();
    }
}