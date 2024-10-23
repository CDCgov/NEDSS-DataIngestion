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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
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
