package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Labtest_loinc")
@Data
public class LabTestLoinc {
    @Id
    @Column(name = "lab_test_cd")
    private String labTestCd;

    @Column(name = "laboratory_id")
    private String laboratoryId;

    @Column(name = "loinc_cd")
    private String loincCd;


    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "status_cd")
    private Character statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;
}
