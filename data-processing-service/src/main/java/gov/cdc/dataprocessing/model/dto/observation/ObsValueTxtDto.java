package gov.cdc.dataprocessing.model.dto.observation;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueTxt;
import lombok.Getter;
import lombok.Setter;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public class ObsValueTxtDto extends BaseContainer
{

    private Long observationUid;

    private Integer obsValueTxtSeq;

    private String dataSubtypeCd;

    private String encodingTypeCd;

    private String txtTypeCd;

    private byte[] valueImageTxt;

    private String valueTxt;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;



    public ObsValueTxtDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }

    public ObsValueTxtDto(ObsValueTxt obsValueTxt) {
        this.observationUid = obsValueTxt.getObservationUid();
        this.obsValueTxtSeq = obsValueTxt.getObsValueTxtSeq();
        this.dataSubtypeCd = obsValueTxt.getDataSubtypeCd();
        this.encodingTypeCd = obsValueTxt.getEncodingTypeCd();
        this.txtTypeCd = obsValueTxt.getTxtTypeCd();
        this.valueImageTxt = obsValueTxt.getValueImageTxt();
        this.valueTxt = obsValueTxt.getValueTxt();
    }
}
