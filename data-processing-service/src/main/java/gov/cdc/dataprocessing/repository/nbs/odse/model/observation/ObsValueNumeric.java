package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.dto.observation.ObsValueNumericDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ObsValueNumericId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "Obs_value_numeric")
@IdClass(ObsValueNumericId.class)
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
