package gov.cdc.dataprocessing.constant.query;

public class CodeValueQuery {
    public static final String SELECT_CODE_VALUE_GENERAL_BY_CODE_SET_NM = """
SELECT
    code_set_nm AS codeSetNm,
    code AS code,
    code_desc_txt AS codeDescTxt,
    code_short_desc_txt AS codeShortDescTxt,
    code_system_cd AS codeSystemCd,
    code_system_desc_txt AS codeSystemDescTxt,
    effective_from_time AS effectiveFromTime,
    effective_to_time AS effectiveToTime,
    indent_level_nbr AS indentLevelNbr,
    is_modifiable_ind AS isModifiableInd,
    nbs_uid AS nbsUid,
    parent_is_cd AS parentIsCd,
    source_concept_id AS sourceConceptId,
    super_code_set_nm AS superCodeSetNm,
    super_code AS superCode,
    status_cd AS statusCd,
    status_time AS statusTime,
    concept_type_cd AS conceptTypeCd,
    concept_code AS conceptCode,
    concept_nm AS conceptNm,
    concept_preferred_nm AS conceptPreferredNm,
    concept_status_cd AS conceptStatusCd,
    concept_status_time AS conceptStatusTime,
    code_system_version_nbr AS codeSystemVersionNbr,
    concept_order_nbr AS conceptOrderNbr,
    admin_comments AS adminComments,
    add_time AS addTime,
    add_user_id AS addUserId
FROM dbo.Code_value_general
WHERE UPPER(code_set_nm) = :codeSetNm
""";

    public static final String SELECT_CODE_VALUE_GENERAL_BY_CODE_SET_NM_ORDERED = """
SELECT
    code_set_nm AS codeSetNm,
    code AS code,
    code_desc_txt AS codeDescTxt,
    code_short_desc_txt AS codeShortDescTxt,
    code_system_cd AS codeSystemCd,
    code_system_desc_txt AS codeSystemDescTxt,
    effective_from_time AS effectiveFromTime,
    effective_to_time AS effectiveToTime,
    indent_level_nbr AS indentLevelNbr,
    is_modifiable_ind AS isModifiableInd,
    nbs_uid AS nbsUid,
    parent_is_cd AS parentIsCd,
    source_concept_id AS sourceConceptId,
    super_code_set_nm AS superCodeSetNm,
    super_code AS superCode,
    status_cd AS statusCd,
    status_time AS statusTime,
    concept_type_cd AS conceptTypeCd,
    concept_code AS conceptCode,
    concept_nm AS conceptNm,
    concept_preferred_nm AS conceptPreferredNm,
    concept_status_cd AS conceptStatusCd,
    concept_status_time AS conceptStatusTime,
    code_system_version_nbr AS codeSystemVersionNbr,
    concept_order_nbr AS conceptOrderNbr,
    admin_comments AS adminComments,
    add_time AS addTime,
    add_user_id AS addUserId
FROM dbo.Code_value_general
WHERE UPPER(code_set_nm) = :codeSetNm
ORDER BY concept_order_nbr, code_short_desc_txt
""";

    public static final String SELECT_CODE_VALUE_GENERAL_BY_CODE_SET_AND_CODE = """
SELECT
    code_set_nm AS codeSetNm,
    code AS code,
    code_desc_txt AS codeDescTxt,
    code_short_desc_txt AS codeShortDescTxt,
    code_system_cd AS codeSystemCd,
    code_system_desc_txt AS codeSystemDescTxt,
    effective_from_time AS effectiveFromTime,
    effective_to_time AS effectiveToTime,
    indent_level_nbr AS indentLevelNbr,
    is_modifiable_ind AS isModifiableInd,
    nbs_uid AS nbsUid,
    parent_is_cd AS parentIsCd,
    source_concept_id AS sourceConceptId,
    super_code_set_nm AS superCodeSetNm,
    super_code AS superCode,
    status_cd AS statusCd,
    status_time AS statusTime,
    concept_type_cd AS conceptTypeCd,
    concept_code AS conceptCode,
    concept_nm AS conceptNm,
    concept_preferred_nm AS conceptPreferredNm,
    concept_status_cd AS conceptStatusCd,
    concept_status_time AS conceptStatusTime,
    code_system_version_nbr AS codeSystemVersionNbr,
    concept_order_nbr AS conceptOrderNbr,
    admin_comments AS adminComments,
    add_time AS addTime,
    add_user_id AS addUserId
FROM dbo.Code_value_general
WHERE code_set_nm = :codeSetNm
AND code = :code
""";

}
