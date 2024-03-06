package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueDate;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ObsValueDateDT  extends AbstractVO
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

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;

    public ObsValueDateDT() {

    }

    public ObsValueDateDT(ObsValueDate obsValueDate) {
        this.observationUid = obsValueDate.getObservationUid();
        this.obsValueDateSeq = obsValueDate.getObsValueDateSeq();
        this.durationAmt = obsValueDate.getDurationAmt();
        this.durationUnitCd = obsValueDate.getDurationUnitCd();
        this.fromTime = obsValueDate.getFromTime();
        this.toTime = obsValueDate.getToTime();
    }

}
