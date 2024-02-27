package gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ObsValueNumericDT extends AbstractVO
{
    private String numericValue;

    private Long observationUid;

    private Integer obsValueNumericSeq;

    private String highRange;

    private String lowRange;

    private String comparatorCd1;

    private BigDecimal numericValue1;

    private BigDecimal numericValue2;

    private String numericUnitCd;

    private String separatorCd;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;

    private Integer numericScale1;

    private Integer numericScale2;

    private boolean itDirty = false;

    private boolean itNew = true;

    private boolean itDelete = false;
}
