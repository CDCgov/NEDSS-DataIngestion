package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "Code_value_general", schema = "dbo")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class CodeValueGeneral {

    @Id
    @Column(name = "code_set_nm", nullable = false, length = 256)
    private String codeSetNm;

    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "code_desc_txt", length = 300)
    private String codeDescTxt;

    @Column(name = "code_short_desc_txt", length = 100)
    private String codeShortDescTxt;

    @Column(name = "code_system_cd", length = 300)
    private String codeSystemCd;

    @Column(name = "code_system_desc_txt", length = 100)
    private String codeSystemDescTxt;

    @Column(name = "effective_from_time")
    private Date effectiveFromTime;

    @Column(name = "effective_to_time")
    private Date effectiveToTime;

    @Column(name = "indent_level_nbr")
    private Short indentLevelNbr;

    @Column(name = "is_modifiable_ind", length = 1)
    private Character isModifiableInd = 'Y';

    @Column(name = "nbs_uid")
    private Integer nbsUid;

    @Column(name = "parent_is_cd", length = 20)
    private String parentIsCd;

    @Column(name = "source_concept_id", length = 20)
    private String sourceConceptId;

    @Column(name = "super_code_set_nm", length = 256)
    private String superCodeSetNm;

    @Column(name = "super_code", length = 20)
    private String superCode;

    @Column(name = "status_cd", length = 1)
    private Character statusCd;

    @Column(name = "status_time")
    private Date statusTime;

    @Column(name = "concept_type_cd", length = 20)
    private String conceptTypeCd;

    @Column(name = "concept_code", length = 256)
    private String conceptCode;

    @Column(name = "concept_nm", length = 256)
    private String conceptNm;

    @Column(name = "concept_preferred_nm", length = 256)
    private String conceptPreferredNm;

    @Column(name = "concept_status_cd", length = 256)
    private String conceptStatusCd;

    @Column(name = "concept_status_time")
    private Date conceptStatusTime;

    @Column(name = "code_system_version_nbr", length = 256)
    private String codeSystemVersionNbr;

    @Column(name = "concept_order_nbr")
    private Integer conceptOrderNbr;

    @Column(name = "admin_comments", length = 2000)
    private String adminComments;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "add_user_id")
    private Long addUserId;
}
