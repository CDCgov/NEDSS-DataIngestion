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
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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

