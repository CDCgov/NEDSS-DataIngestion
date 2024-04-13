package gov.cdc.dataprocessing.model.dto.observation;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueDate;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ObsValueDateDto extends BaseContainer
{
    private static final long serialVersionUID = 1L;

    private Long observationUid;

    private Integer obsValueDateSeq;

    private String durationAmt;

    private String durationUnitCd;

    private Timestamp fromTime;

    private Timestamp toTime;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;



    public ObsValueDateDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public ObsValueDateDto(ObsValueDate obsValueDate) {
        this.observationUid = obsValueDate.getObservationUid();
        this.obsValueDateSeq = obsValueDate.getObsValueDateSeq();
        this.durationAmt = obsValueDate.getDurationAmt();
        this.durationUnitCd = obsValueDate.getDurationUnitCd();
        this.fromTime = obsValueDate.getFromTime();
        this.toTime = obsValueDate.getToTime();
    }

}
