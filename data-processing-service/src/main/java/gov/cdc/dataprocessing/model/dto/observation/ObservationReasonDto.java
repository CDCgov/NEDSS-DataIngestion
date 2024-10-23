package gov.cdc.dataprocessing.model.dto.observation;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObservationReason;
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
