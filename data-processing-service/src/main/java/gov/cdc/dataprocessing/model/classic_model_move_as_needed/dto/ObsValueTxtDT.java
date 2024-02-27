package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObsValueTxtDT extends AbstractVO
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
}
