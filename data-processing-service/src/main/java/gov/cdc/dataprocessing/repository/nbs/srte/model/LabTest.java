package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Lab_test")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class LabTest {
    @Id
    @Column(name = "lab_test_cd")
    private String labTestCd;

    @Column(name = "laboratory_id")
    private String laboratoryId;

    @Column(name = "lab_test_desc_txt")
    private String labResultDescTxt;

    @Column(name = "test_type_cd")
    private String testTypeCd;

    @Column(name = "nbs_uid")
    private Long nbsUid;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "default_prog_area_cd")
    private String defaultProgAreaCd;

    @Column(name = "default_condition_cd")
    private String defaultConditionCd;

    @Column(name = "drug_test_ind")
    private String drugTestInd;

    @Column(name = "organism_result_test_ind")
    private String organismResultTestInd;

    @Column(name = "indent_level_nbr")
    private Integer indentLevelNbr;

    @Column(name = "pa_derivation_exclude_cd")
    private String paDerivationExcludeCd;
}
