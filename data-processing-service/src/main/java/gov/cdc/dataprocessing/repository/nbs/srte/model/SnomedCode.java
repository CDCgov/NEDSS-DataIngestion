package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Snomed_code")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
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