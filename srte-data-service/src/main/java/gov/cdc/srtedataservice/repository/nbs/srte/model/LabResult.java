package gov.cdc.srtedataservice.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "Lab_result")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class LabResult {

    @Id
    @Column(name = "lab_result_cd", length = 20)
    private String labResultCd;

    @Column(name = "laboratory_id", length = 20)
    private String laboratoryId;

    @Column(name = "lab_result_desc_txt", length = 50)
    private String labResultDescTxt;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "nbs_uid")
    private Long nbsUid;

    @Column(name = "default_prog_area_cd", length = 20)
    private String defaultProgAreaCd;

    @Column(name = "organism_name_ind", length = 1)
    private String organismNameInd;

    @Column(name = "default_condition_cd", length = 20)
    private String defaultConditionCd;

    @Column(name = "pa_derivation_exclude_cd", length = 1)
    private String paDerivationExcludeCd;

    @Column(name = "code_system_cd", length = 300)
    private String codeSystemCd;

    @Column(name = "code_set_nm", length = 256)
    private String codeSetNm;

}
