package gov.cdc.nbsDedup.model.dto.observation;


import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import gov.cdc.nbsDedup.nbs.odse.model.observation.ObservationInterp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
