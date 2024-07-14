package gov.cdc.dataprocessing.repository.nbs.srte.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "Snomed_condition")
@Getter
@Setter
public class SnomedCondition {

    @Id
    @Column(name = "snomed_cd", length = 20)
    private String snomedCd;

    @Column(name = "condition_cd", length = 20)
    private String conditionCd;

    @Column(name = "disease_nm", length = 200)
    private String diseaseNm;

    @Column(name = "organism_set_nm", length = 100)
    private String organismSetNm;

    @Column(name = "status_cd", length = 1)
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;
}
