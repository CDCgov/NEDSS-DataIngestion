package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.dto.observation.ObservationReasonDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ObservationReasonId;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Observation_reason")
@IdClass(ObservationReasonId.class)
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class ObservationReason {

    @Id
    @Column(name = "observation_uid")
    private Long observationUid;

    @Id
    @Column(name = "reason_cd")
    private String reasonCd;

    @Column(name = "reason_desc_txt")
    private String reasonDescTxt;


    public ObservationReason() {

    }

    public ObservationReason(ObservationReasonDto observationReasonDto) {
        this.observationUid = observationReasonDto.getObservationUid();
        this.reasonCd = observationReasonDto.getReasonCd();
        this.reasonDescTxt = observationReasonDto.getReasonDescTxt();
    }
}
