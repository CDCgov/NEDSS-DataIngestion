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
