package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObservationInterpDT;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "Observation_interp")
public class ObservationInterp {

    @Id
    @Column(name = "observation_uid")
    private Long observationUid;

    @Id
    @Column(name = "interpretation_cd")
    private String interpretationCd;

    @Column(name = "interpretation_desc_txt")
    private String interpretationDescTxt;

    // Relationships if needed
    public ObservationInterp() {

    }

    public ObservationInterp(ObservationInterpDT observationInterpDT) {
        this.observationUid = observationInterpDT.getObservationUid();
        this.interpretationCd = observationInterpDT.getInterpretationCd();
        this.interpretationDescTxt = observationInterpDT.getInterpretationDescTxt();
    }
}
