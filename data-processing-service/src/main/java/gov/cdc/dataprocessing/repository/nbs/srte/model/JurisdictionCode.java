package gov.cdc.dataprocessing.repository.nbs.srte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;


@Entity
@Table(name = "Jurisdiction_code")
@Data
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 6809 - TEST
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S6809"})
public class JurisdictionCode {

    @Id
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    @Column(name = "type_cd", nullable = false, length = 20)
    private String typeCd;

    @Column(name = "assigning_authority_cd", length = 199)
    private String assigningAuthorityCd;

    @Column(name = "assigning_authority_desc_txt", length = 100)
    private String assigningAuthorityDescTxt;

    @Column(name = "code_desc_txt", length = 255)
    private String codeDescTxt;

    @Column(name = "code_short_desc_txt", length = 50)
    private String codeShortDescTxt;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "indent_level_nbr")
    private Integer indentLevelNbr;

    @Column(name = "is_modifiable_ind", length = 1)
    private String isModifiableInd;

    @Column(name = "parent_is_cd", length = 20)
    private String parentIsCd;

    @Column(name = "state_domain_cd", length = 20)
    private String stateDomainCd;

    @Column(name = "status_cd", length = 1)
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "code_set_nm", length = 256)
    private String codeSetNm;

    @Column(name = "code_seq_num")
    private Integer codeSeqNum;

    @Column(name = "nbs_uid")
    private Integer nbsUid;

    @Column(name = "source_concept_id", length = 20)
    private String sourceConceptId;

    @Column(name = "code_system_cd", length = 300)
    private String codeSystemCd;

    @Column(name = "code_system_desc_txt", length = 100)
    private String codeSystemDescTxt;

    @Column(name = "export_ind", length = 1)
    private String exportInd;

    // Add getters and setters as needed
}
