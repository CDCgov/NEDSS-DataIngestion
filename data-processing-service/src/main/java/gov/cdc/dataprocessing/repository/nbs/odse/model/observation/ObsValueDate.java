package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.dto.observation.ObsValueDateDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ObsValueDateId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "Obs_value_date")
@Data
@IdClass(ObsValueDateId.class)
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
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

    public ObsValueDate(ObsValueDateDto obsValueDateDto) {
        this.observationUid = obsValueDateDto.getObservationUid();
        this.obsValueDateSeq = obsValueDateDto.getObsValueDateSeq();
        this.durationAmt = obsValueDateDto.getDurationAmt();
        this.durationUnitCd = obsValueDateDto.getDurationUnitCd();
        this.fromTime = obsValueDateDto.getFromTime();
        this.toTime = obsValueDateDto.getToTime();
    }
}