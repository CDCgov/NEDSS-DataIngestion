package gov.cdc.dataprocessing.model.classic_model.dto;

import gov.cdc.dataprocessing.model.classic_model.vo.AbstractVO;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class ObsValueCodedDT extends AbstractVO
{

    private Long observationUid;

    private String altCd;

    private String altCdDescTxt;

    private String altCdSystemCd;

    private String altCdSystemDescTxt;

    private String code;

    private String codeDerivedInd;

    private String codeSystemCd;

    private String codeSystemDescTxt;

    private String codeVersion;

    private String displayName;

    private String originalTxt;

    private String progAreaCd = null;

    private String jurisdictionCd = null;

    private Long programJurisdictionOid = null;

    private String sharedInd = null;

    private boolean itDirty = false;

    private boolean itNew = false;

    private boolean itDelete = false;

    private Collection<Object> theObsValueCodedModDTCollection;

    private String searchResultRT;

    private String cdSystemCdRT;

    private String hiddenCd;
}
