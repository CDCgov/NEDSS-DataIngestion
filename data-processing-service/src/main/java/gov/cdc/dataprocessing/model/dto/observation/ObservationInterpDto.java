package gov.cdc.dataprocessing.model.dto.observation;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObservationInterp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class ObservationInterpDto extends BaseContainer {

    private Long observationUid;
    private String interpretationCd;
    private String interpretationDescTxt;
    boolean _bDirty = false;
    boolean _bNew = false;

    public ObservationInterpDto() {

    }

    public ObservationInterpDto(ObservationInterp observationInterp) {
        this.observationUid = observationInterp.getObservationUid();
        this.interpretationCd = observationInterp.getInterpretationCd();
        this.interpretationDescTxt = observationInterp.getInterpretationDescTxt();
    }


}
