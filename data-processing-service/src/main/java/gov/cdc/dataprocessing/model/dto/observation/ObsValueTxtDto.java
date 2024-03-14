package gov.cdc.dataprocessing.model.dto.observation;

import gov.cdc.dataprocessing.model.container.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueTxt;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;

    public ObsValueTxtDto() {

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
