package gov.cdc.dataprocessing.model.dto.observation;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueDate;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
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
