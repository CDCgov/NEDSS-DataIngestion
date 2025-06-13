package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.dto.observation.ObservationInterpDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ObservationInterpId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;


@Data
@Entity
@Table(name = "Observation_interp")
@IdClass(ObservationInterpId.class)

public class ObservationInterp implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public ObservationInterp(ObservationInterpDto observationInterpDto) {
        this.observationUid = observationInterpDto.getObservationUid();
        this.interpretationCd = observationInterpDto.getInterpretationCd();
        this.interpretationDescTxt = observationInterpDto.getInterpretationDescTxt();
    }
}
