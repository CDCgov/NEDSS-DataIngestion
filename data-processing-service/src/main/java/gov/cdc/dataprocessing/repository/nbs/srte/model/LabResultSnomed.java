package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Entity
@Table(name = "Lab_result_Snomed")
@Getter
@Setter
public class LabResultSnomed {

    @Id
    @Column(name = "lab_result_cd")
    private String labResultCd;

    @Column(name = "laboratory_id")
    private String laboratoryId;

    @Column(name = "snomed_cd")
    private String snomedCd;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "status_cd")
    private Character statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;
}
