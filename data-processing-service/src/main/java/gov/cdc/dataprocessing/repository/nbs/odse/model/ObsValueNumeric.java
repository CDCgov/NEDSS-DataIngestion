package gov.cdc.dataprocessing.repository.nbs.odse.model;

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
    private Short obsValueNumericSeq;

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
    private Short numericScale1;

    @Column(name = "numeric_scale_2")
    private Short numericScale2;

    // Relationships if needed
}
