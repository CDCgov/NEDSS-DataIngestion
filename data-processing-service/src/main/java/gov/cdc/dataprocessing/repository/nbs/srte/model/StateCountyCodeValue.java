package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "State_county_code_value")
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
public class StateCountyCodeValue {

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
    private Date effectiveFromTime;

    @Column(name = "effective_to_time")
    private Date effectiveToTime;

    @Column(name = "excluded_txt")
    private String excludedTxt;

    @Column(name = "Indent_level_nbr")
    private Short indentLevelNbr;

    @Column(name = "is_modifiable_ind")
    private Character isModifiableInd;

    @Column(name = "parent_is_cd")
    private String parentIsCd;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "code_set_nm")
    private String codeSetNm;

    @Column(name = "seq_num")
    private Short seqNum;

    @Column(name = "nbs_uid")
    private Integer nbsUid;

    @Column(name = "source_concept_id")
    private String sourceConceptId;

    @Column(name = "code_system_cd")
    private String codeSystemCd;

    @Column(name = "code_system_desc_txt")
    private String codeSystemDescTxt;

    // Getters and setters
}
