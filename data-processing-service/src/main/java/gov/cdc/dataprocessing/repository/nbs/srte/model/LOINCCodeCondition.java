package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Loinc_condition")
@Data

public class LOINCCodeCondition {
    @Id
    @Column(name = "loinc_cd", length = 20)
    private String loincCd;

    @Column(name = "condition_cd", length = 20)
    private String conditionCd;

    @Column(name = "disease_nm", length = 200)
    private String diseaseNm;

    @Column(name = "reported_value", length = 20)
    private String reportedValue;

    @Column(name = "reported_numeric_value", length = 20)
    private String reportedNumericValue;

    @Column(name = "status_cd", length = 1)
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;
}
