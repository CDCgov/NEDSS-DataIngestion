package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "City_code_value")
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public class CityCodeValue {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "assigning_authority_cd")
    private String assigningAuthorityCd;

    @Column(name = "assigning_authority_desc_txt")
    private String assigningAuthorityDescTxt;

    @Column(name = "code_desc_txt")
    private String codeDescTxt;

    @Column(name = "code_short_desc_txt")
    private String codeShortDescTxt;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "excluded_txt")
    private String excludedTxt;

    @Column(name = "indent_level_nbr")
    private Integer indentLevelNbr;

    @Column(name = "is_modifiable_ind")
    private String isModifiableInd;

    @Column(name = "parent_is_cd")
    private String parentIsCd;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "code_set_nm")
    private String codeSetNm;

    @Column(name = "seq_num")
    private Integer seqNum;

    @Column(name = "nbs_uid")
    private Integer nbsUid;

    @Column(name = "source_concept_id")
    private String sourceConceptId;
}
