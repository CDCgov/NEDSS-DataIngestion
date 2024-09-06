package gov.cdc.nbsDedup.model.dto.observation;


import gov.cdc.nbsDedup.model.container.base.BaseContainer;
import gov.cdc.nbsDedup.nbs.odse.model.observation.ObservationReason;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObservationReasonDto extends BaseContainer
{

    private Long observationUid;

    private String reasonCd;

    private String reasonDescTxt;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;



    public ObservationReasonDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public ObservationReasonDto(ObservationReason observationReason) {
        this.observationUid = observationReason.getObservationUid();
        this.reasonCd = observationReason.getReasonCd();
        this.reasonDescTxt = observationReason.getReasonDescTxt();
    }

}
