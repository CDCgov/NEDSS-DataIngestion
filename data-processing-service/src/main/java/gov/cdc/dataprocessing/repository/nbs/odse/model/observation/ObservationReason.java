package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationReasonDT;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Observation_reason")
public class ObservationReason {

    @Id
    @Column(name = "observation_uid")
    private Long observationUid;

    @Id
    @Column(name = "reason_cd")
    private String reasonCd;

    @Column(name = "reason_desc_txt")
    private String reasonDescTxt;

    // Constructors, getters, and setters (Lombok-generated)
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "observation_uid", referencedColumnName = "observation_uid", insertable = false, updatable = false)
//    private Observation observation;

    // Other relationships or methods if needed

    public ObservationReason() {

    }

    public ObservationReason(ObservationReasonDT observationReasonDT) {
        this.observationUid = observationReasonDT.getObservationUid();
        this.reasonCd = observationReasonDT.getReasonCd();
        this.reasonDescTxt = observationReasonDT.getReasonDescTxt();
    }
}
