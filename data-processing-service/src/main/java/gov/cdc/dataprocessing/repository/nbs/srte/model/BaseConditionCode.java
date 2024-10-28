package gov.cdc.dataprocessing.repository.nbs.srte.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "Condition_code")
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
public class BaseConditionCode {

    @Id
    @Column(name = "condition_cd")
    private String conditionCd;

    @Column(name = "condition_codeset_nm")
    private String conditionCodesetNm;

    @Column(name = "condition_seq_num")
    private Integer conditionSeqNum;

    @Column(name = "assigning_authority_cd")
    private String assigningAuthorityCd;

    @Column(name = "assigning_authority_desc_txt")
    private String assigningAuthorityDescTxt;

    @Column(name = "code_system_cd")
    private String codeSystemCd;

    @Column(name = "code_system_desc_txt")
    private String codeSystemDescTxt;

    @Column(name = "condition_desc_txt")
    private String conditionDescTxt;

    @Column(name = "condition_short_nm")
    private String conditionShortNm;

    @Column(name = "effective_from_time")
    private Timestamp effectiveFromTime;

    @Column(name = "effective_to_time")
    private Timestamp effectiveToTime;

    @Column(name = "indent_level_nbr")
    private Integer indentLevelNbr;

    @Column(name = "investigation_form_cd")
    private String investigationFormCd;

    @Column(name = "is_modifiable_ind")
    private String isModifiableInd;

    @Column(name = "nbs_uid")
    private Long nbsUid;

    @Column(name = "nnd_ind")
    private String nndInd;

    @Column(name = "parent_is_cd")
    private String parentIsCd;

    @Column(name = "prog_area_cd")
    private String progAreaCd;

    @Column(name = "reportable_morbidity_ind")
    private String reportableMorbidityInd;

    @Column(name = "reportable_summary_ind")
    private String reportableSummaryInd;

    @Column(name = "status_cd")
    private String statusCd;

    @Column(name = "status_time")
    private Timestamp statusTime;

    @Column(name = "nnd_entity_identifier")
    private String nndEntityIdentifier;

    @Column(name = "nnd_summary_entity_identifier")
    private String nndSummaryEntityIdentifier;

    @Column(name = "summary_investigation_form_cd")
    private String summaryInvestigationFormCd;

    @Column(name = "contact_tracing_enable_ind")
    private String contactTracingEnableInd;

    @Column(name = "vaccine_enable_ind")
    private String vaccineEnableInd;

    @Column(name = "treatment_enable_ind")
    private String treatmentEnableInd;

    @Column(name = "lab_report_enable_ind")
    private String labReportEnableInd;

    @Column(name = "morb_report_enable_ind")
    private String morbReportEnableInd;

    @Column(name = "port_req_ind_cd")
    private String portReqIndCd;

    @Column(name = "family_cd")
    private String familyCd;

    @Column(name = "coinfection_grp_cd")
    private String coinfectionGrpCd;

//    @Column(name = "rhap_parse_nbs_ind")
//    private String rhapParseNbsInd;

//    @Column(name = "rhap_action_value")
//    private String rhapActionValue;

    // Constructors, getters, and setters
}