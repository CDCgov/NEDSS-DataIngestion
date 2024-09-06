package gov.cdc.nbsDedup.nbs.odse.model.observation;


import gov.cdc.nbsDedup.model.dto.observation.ObservationReasonDto;
import gov.cdc.nbsDedup.nbs.odse.model.id_class.ObservationReasonId;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Observation_reason")
@IdClass(ObservationReasonId.class)
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
