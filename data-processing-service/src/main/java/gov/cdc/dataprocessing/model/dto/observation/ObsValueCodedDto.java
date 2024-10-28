package gov.cdc.dataprocessing.model.dto.observation;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueCoded;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class ObsValueCodedDto extends BaseContainer
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


    private Collection<Object> theObsValueCodedModDTCollection;

    private String searchResultRT;

    private String cdSystemCdRT;

    private String hiddenCd;

    public ObsValueCodedDto() {

    }

    public ObsValueCodedDto(ObsValueCoded obsValueCoded) {
        this.observationUid = obsValueCoded.getObservationUid();
        this.altCd = obsValueCoded.getAltCd();
        this.altCdDescTxt = obsValueCoded.getAltCdDescTxt();
        this.altCdSystemCd = obsValueCoded.getAltCdSystemCd();
        this.altCdSystemDescTxt = obsValueCoded.getAltCdSystemDescTxt();
        this.code = obsValueCoded.getCode();
        this.codeDerivedInd = obsValueCoded.getCodeDerivedInd();
        this.codeSystemCd = obsValueCoded.getCodeSystemCd();
        this.codeSystemDescTxt = obsValueCoded.getCodeSystemDescTxt();
        this.codeVersion = obsValueCoded.getCodeVersion();
        this.displayName = obsValueCoded.getDisplayName();
        this.originalTxt = obsValueCoded.getOriginalTxt();
    }
}

