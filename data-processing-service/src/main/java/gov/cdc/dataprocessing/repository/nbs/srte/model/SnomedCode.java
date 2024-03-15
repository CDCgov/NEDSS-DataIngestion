package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Snomed_code")
@Data
public class SnomedCode {

    @Id
    @Column(name = "snomed_cd")
    private String snomedCd;

    @Column(name = "snomed_desc_txt")
    private String snomedDescTxt;

    @Column(name = "source_concept_id")
    private String sourceConceptId;

    @Column(name = "source_version_id")
    private String sourceVersionId;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "nbs_uid")
    private Integer nbsUid;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "pa_derivation_exclude_cd")
    private String paDerivationExcludeCd;

    // Constructors, getters, and setters
}