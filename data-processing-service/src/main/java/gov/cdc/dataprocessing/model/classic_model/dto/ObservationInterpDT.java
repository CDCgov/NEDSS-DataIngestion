package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObservationInterpDT extends AbstractVO {

    private Long observationUid;
    private String interpretationCd;
    private String interpretationDescTxt;
    boolean _bDirty = false;
    boolean _bNew = false;

}
