package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.dto.observation.ObsValueNumericDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ObsValueNumericId;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Obs_value_numeric")
@IdClass(ObsValueNumericId.class)
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public class ObsValueNumeric implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "observation_uid")
    private Long observationUid;

    @Id
    @Column(name = "obs_value_numeric_seq")
    private Integer obsValueNumericSeq;

    @Column(name = "high_range")
    private String highRange;

    @Column(name = "low_range")
    private String lowRange;

    @Column(name = "comparator_cd_1")
    private String comparatorCd1;

    @Column(name = "numeric_value_1")
    private BigDecimal numericValue1;

    @Column(name = "numeric_value_2")
    private BigDecimal numericValue2;

    @Column(name = "numeric_unit_cd")
    private String numericUnitCd;

    @Column(name = "separator_cd")
    private String separatorCd;

    @Column(name = "numeric_scale_1")
    private Integer numericScale1;

    @Column(name = "numeric_scale_2")
    private Integer numericScale2;

    // Relationships if needed
    public ObsValueNumeric() {
        
    }
    public ObsValueNumeric(ObsValueNumericDto obsValueNumericDto) {
        this.observationUid = obsValueNumericDto.getObservationUid();
        this.obsValueNumericSeq = obsValueNumericDto.getObsValueNumericSeq();
        this.highRange = obsValueNumericDto.getHighRange();
        this.lowRange = obsValueNumericDto.getLowRange();
        this.comparatorCd1 = obsValueNumericDto.getComparatorCd1();
        this.numericValue1 = obsValueNumericDto.getNumericValue1();
        this.numericValue2 = obsValueNumericDto.getNumericValue2();
        this.numericUnitCd = obsValueNumericDto.getNumericUnitCd();
        this.separatorCd = obsValueNumericDto.getSeparatorCd();
        this.numericScale1 = obsValueNumericDto.getNumericScale1();
        this.numericScale2 = obsValueNumericDto.getNumericScale2();
    }

}
