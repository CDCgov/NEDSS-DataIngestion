package gov.cdc.dataprocessing.repository.nbs.odse.model.observation;

import gov.cdc.dataprocessing.model.classic_model_move_as_needed.dto.ObsValueNumericDT;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Obs_value_numeric")
public class ObsValueNumeric {

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
    public ObsValueNumeric(ObsValueNumericDT obsValueNumericDT) {
        this.observationUid = obsValueNumericDT.getObservationUid();
        this.obsValueNumericSeq = obsValueNumericDT.getObsValueNumericSeq();
        this.highRange = obsValueNumericDT.getHighRange();
        this.lowRange = obsValueNumericDT.getLowRange();
        this.comparatorCd1 = obsValueNumericDT.getComparatorCd1();
        this.numericValue1 = obsValueNumericDT.getNumericValue1();
        this.numericValue2 = obsValueNumericDT.getNumericValue2();
        this.numericUnitCd = obsValueNumericDT.getNumericUnitCd();
        this.separatorCd = obsValueNumericDT.getSeparatorCd();
        this.numericScale1 = obsValueNumericDT.getNumericScale1();
        this.numericScale2 = obsValueNumericDT.getNumericScale2();
    }

}
