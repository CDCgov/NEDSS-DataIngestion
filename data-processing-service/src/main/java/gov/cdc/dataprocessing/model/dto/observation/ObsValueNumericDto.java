package gov.cdc.dataprocessing.model.dto.observation;

import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueNumeric;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

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
public class ObsValueNumericDto extends BaseContainer
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




    public ObsValueNumericDto() {
        itDirty = false;
        itNew = true;
        itDelete = false;
    }
    public ObsValueNumericDto(ObsValueNumeric obsValueNumeric) {
        this.observationUid = obsValueNumeric.getObservationUid();
        this.obsValueNumericSeq = obsValueNumeric.getObsValueNumericSeq();
        this.highRange = obsValueNumeric.getHighRange();
        this.lowRange = obsValueNumeric.getLowRange();
        this.comparatorCd1 = obsValueNumeric.getComparatorCd1();
        this.numericValue1 = obsValueNumeric.getNumericValue1();
        this.numericValue2 = obsValueNumeric.getNumericValue2();
        this.numericUnitCd = obsValueNumeric.getNumericUnitCd();
        this.separatorCd = obsValueNumeric.getSeparatorCd();
        this.numericScale1 = obsValueNumeric.getNumericScale1();
        this.numericScale2 = obsValueNumeric.getNumericScale2();
    }
}
