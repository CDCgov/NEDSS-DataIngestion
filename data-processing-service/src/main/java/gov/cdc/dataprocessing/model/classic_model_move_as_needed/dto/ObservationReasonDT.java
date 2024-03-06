package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObservationReason;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObservationReasonDT extends AbstractVO
{

    private Long observationUid;

    private String reasonCd;

    private String reasonDescTxt;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;

    public ObservationReasonDT() {

    }

    public ObservationReasonDT(ObservationReason observationReason) {
        this.observationUid = observationReason.getObservationUid();
        this.reasonCd = observationReason.getReasonCd();
        this.reasonDescTxt = observationReason.getReasonDescTxt();
    }

}
