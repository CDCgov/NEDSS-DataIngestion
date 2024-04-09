package gov.cdc.dataprocessing.constant;

public class ComplexQueries {
    public static final String DMB_QUESTION_OID_METADATA_SQL = "SELECT "
            + "NBS_UI_METADATA.nbs_question_uid nbsQuestionUid, "
            + "NBS_UI_METADATA.add_time addTime, "
            + "NBS_UI_METADATA.add_user_id addUserId, "
            + "NBS_UI_METADATA.code_set_group_id codeSetGroupId, "
            + "NBS_UI_METADATA.data_type dataType, "
            + "NBS_UI_METADATA.mask \"mask\", "
            + "NBS_UI_METADATA.investigation_form_cd investigationFormCd, "
            + "NBS_UI_METADATA.last_chg_time lastChgTime, "
            + "NBS_UI_METADATA.last_chg_user_id lastChgUserId, "
            + "NBS_UI_METADATA.question_label AS questionLabel, "
            + "NBS_UI_METADATA.question_tool_tip AS questionToolTip, "
            + "NBS_UI_METADATA.version_ctrl_nbr questionVersionNbr, "
            + "NBS_UI_METADATA.tab_order_id tabId, "
            + "NBS_UI_METADATA.enable_ind enableInd, "
            + "NBS_UI_METADATA.order_nbr orderNbr, "
            + "NBS_UI_METADATA.default_value defaultValue, "
            + "NBS_UI_METADATA.required_ind requiredInd, "
            + "NBS_UI_METADATA.display_ind displayInd, "
            + "NBS_UI_Metadata.coinfection_ind_cd coinfectionIndCd, "
            + "NND_METADATA.nnd_metadata_uid nndMetadataUid, "
            + "NBS_UI_METADATA.question_identifier questionIdentifier, "
            + "NND_METADATA.question_identifier_nnd questionIdentifierNnd,"
            + "NND_METADATA.question_required_nnd questionRequiredNnd,"
            + "NBS_UI_METADATA.question_oid questionOid, "
            + "NBS_UI_METADATA.question_oid_system_txt questionOidSystemTxt, "
            + "CODE_SET.code_set_nm codeSetNm, "
            + "CODE_SET.class_cd codeSetClassCd, "
            + "NBS_UI_METADATA.data_location dataLocation, "
            + "NBS_UI_METADATA.data_cd dataCd, "
            + "NBS_UI_METADATA.data_use_cd dataUseCd, "
            + "NBS_UI_METADATA.field_size fieldSize, "
            + "NBS_UI_METADATA.parent_uid parentUid, "
            + "NBS_UI_METADATA.ldf_page_id ldfPageId, "
            + "NBS_UI_METADATA.nbs_ui_metadata_uid nbsUiMetadataUid, "
            + "NBS_UI_Component.nbs_ui_component_uid nbsUiComponentUid, "
            + "NBS_UI_METADATA.unit_type_cd unitTypeCd, "
            + "NBS_UI_METADATA.unit_value unitValue, "
            + "NBS_UI_METADATA.nbs_table_uid nbsTableUid, "
            + "NBS_UI_METADATA.part_type_cd partTypeCd, "
            + "NBS_UI_METADATA.standard_nnd_ind_cd \"standardNndIndCd\", "
            + "NBS_UI_METADATA.sub_group_nm subGroupNm, "
            + "NND_METADATA.HL7_segment_field \"hl7SegmentField\", "
            + "NBS_UI_METADATA.question_group_seq_nbr \"questionGroupSeqNbr\", "
            + "NBS_UI_METADATA.question_unit_identifier \"questionUnitIdentifier\" "
            + "FROM "
            + "NBS_UI_Metadata INNER JOIN NBS_UI_Component ON NBS_UI_Metadata.nbs_ui_component_uid = NBS_UI_Component.nbs_ui_component_uid "
            + "LEFT OUTER JOIN "
            + "NND_Metadata ON NBS_UI_METADATA.nbs_ui_metadata_uid = NND_Metadata.nbs_ui_metadata_uid LEFT OUTER JOIN "
            + "(SELECT DISTINCT code_set_group_id, code_set_nm, class_cd FROM "
            + "NBS_SRTE..CODESET) CODE_SET "
            + "ON CODE_SET.code_set_group_id = NBS_UI_METADATA.code_set_group_id "
            + "where upper(NBS_UI_Metadata.record_status_cd) = 'ACTIVE' and NBS_UI_Metadata.question_identifier is not null "
            + "order by NBS_UI_METADATA.investigation_form_cd, NBS_UI_METADATA.order_nbr  ";



    public static final String GENERIC_QUESTION_OID_METADATA_SQL ="SELECT " +
            "wa_question.question_unit_identifier \"questionUnitIdentifier\", " +
            "wa_question.add_time \"addTime\", " +
            "wa_question.add_user_id \"addUserId\", " +
            "wa_question.code_set_group_id \"codeSetGroupId\", " +
            "wa_question.data_type \"dataType\", " +
            "wa_question.mask \"mask\", " +
            "'CORE_INV_FORM' \"investigationFormCd\", "+
            "wa_question.last_chg_time \"lastChgTime\", " +
            "wa_question.last_chg_user_id \"lastChgUserId\", " +
            "wa_question.question_nm \"questionLabel\", " +
            "wa_question.question_tool_tip \"questionToolTip\", " +
            "wa_question.version_ctrl_nbr \"questionVersionNbr\", " +
            "wa_question.default_value \"defaultValue\", " +
            "wa_question.question_identifier \"questionIdentifier\", " +
            "wa_question.question_oid \"questionOid\", " +
            "wa_question.question_oid_system_txt \"questionOidSystemTxt\", " +
            "CODESET.code_set_nm \"codeSetNm\", " +
            "CODESET.class_cd \"codeSetClassCd\", " +
            "wa_question.data_location \"dataLocation\", " +
            "wa_question.data_cd \"dataCd\", " +
            "wa_question.data_use_cd \"dataUseCd\", " +
            "wa_question.field_size \"fieldSize\", " +
            "NBS_UI_Component.nbs_ui_component_uid \"nbsUiComponentUid\", " +
            "wa_question.unit_type_cd \"unitTypeCd\", " +
            "wa_question.unit_value \"unitValue\", " +
            "wa_question.coinfection_ind_cd \"coinfectionIndCd\", " +
            "wa_question.standard_nnd_ind_cd \"standardNndIndCd\",     " +
            "wa_question.question_group_seq_nbr \"questionGroupSeqNbr\"    " +
            "FROM " +
            "wa_question "+
            "INNER JOIN NBS_UI_Component ON wa_question.nbs_ui_component_uid = NBS_UI_Component.nbs_ui_component_uid "+
            "LEFT OUTER JOIN "+
            "NBS_SRTE..CODESET "+
            "ON CODESET.code_set_group_id = wa_question.code_set_group_id "+
            " WHERE(UPPER( wa_question.data_location ) LIKE UPPER( 'public_health_case%' ) "+
            " OR UPPER( wa_question.data_location ) LIKE UPPER( 'confirmation_method%' )) " +
            " AND (wa_question.code_set_group_id IS NULL " +
            "   OR wa_question.code_set_group_id IN "+
            "   ( "+
            "     SELECT code_set_group_id " +
            "      FROM nbs_srte..codeset "+
            "      WHERE UPPER( class_cd )= UPPER('code_value_general') "+
            "    ))" ;


    public static final String PAM_QUESTION_OID_METADATA_SQL = "SELECT "
            + "NBS_UI_METADATA.nbs_question_uid nbsQuestionUid, "
            + "NBS_UI_METADATA.add_time addTime, "
            + "NBS_UI_METADATA.add_user_id addUserId, "
            + "NBS_UI_METADATA.code_set_group_id codeSetGroupId, "
            + "NBS_UI_METADATA.data_type dataType, "
            + "NBS_UI_METADATA.investigation_form_cd investigationFormCd, "
            + "NBS_UI_METADATA.last_chg_time lastChgTime, "
            + "NBS_UI_METADATA.last_chg_user_id lastChgUserId, "
            + "NBS_UI_METADATA.question_label AS questionLabel, "
            + "NBS_UI_METADATA.question_tool_tip AS questionToolTip, "
            + "NBS_UI_METADATA.version_ctrl_nbr questionVersionNbr, "
            + "NBS_UI_METADATA.tab_order_id tabId, "
            + "NBS_UI_METADATA.enable_ind enableInd, "
            + "NBS_UI_METADATA.order_nbr orderNbr, "
            + "NBS_UI_METADATA.default_value defaultValue, "
            + "NBS_UI_METADATA.required_ind requiredInd, "
            + "NBS_UI_METADATA.display_ind displayInd, "
            + "NND_METADATA.nnd_metadata_uid nndMetadataUid, "
            + "NBS_UI_METADATA.question_identifier questionIdentifier, "
            + "NND_METADATA.question_identifier_nnd questionIdentifierNnd,"
            + "NND_METADATA.question_required_nnd questionRequiredNnd,"
            + "NBS_UI_METADATA.question_oid questionOid, "
            + "NBS_UI_METADATA.question_oid_system_txt questionOidSystemTxt, "
            + "NBS_UI_Metadata.coinfection_ind_cd coinfectionIndCd, "
            + "CODE_SET.code_set_nm codeSetNm, "
            + "CODE_SET.class_cd codeSetClassCd, "
            + "NBS_UI_METADATA.data_location dataLocation, "
            + "NBS_UI_METADATA.data_cd dataCd, "
            + "NBS_UI_METADATA.data_use_cd dataUseCd, "
            + "NBS_UI_METADATA.field_size fieldSize, "
            + "NBS_UI_METADATA.parent_uid parentUid, "
            + "NBS_UI_METADATA.ldf_page_id ldfPageId, "
            + "NBS_UI_METADATA.nbs_ui_metadata_uid nbsUiMetadataUid, "
            + "NBS_UI_Component.nbs_ui_component_uid nbsUiComponentUid, "
            + "NBS_UI_METADATA.nbs_table_uid nbsTableUid, "
            + "NBS_UI_METADATA.part_type_cd partTypeCd, "
            + "NBS_UI_METADATA.standard_nnd_ind_cd \"standardNndIndCd\", "
            + "NND_METADATA.HL7_segment_field \"hl7SegmentField\", "
            + "NBS_UI_METADATA.question_group_seq_nbr \"questionGroupSeqNbr\" "
            + "FROM "
            + "NBS_UI_Metadata INNER JOIN NBS_UI_Component ON NBS_UI_Metadata.nbs_ui_component_uid = NBS_UI_Component.nbs_ui_component_uid "
            + "LEFT OUTER JOIN "
            + "NND_Metadata ON NBS_UI_METADATA.nbs_ui_metadata_uid = NND_Metadata.nbs_ui_metadata_uid LEFT OUTER JOIN "
            + "(SELECT DISTINCT code_set_group_id, code_set_nm, class_cd FROM "
            + "NBS_SRTE..CODESET) CODE_SET "
            + "ON CODE_SET.code_set_group_id = NBS_UI_METADATA.code_set_group_id "
            + "where NBS_UI_Metadata.record_status_cd = 'Active' "
            + "order by NBS_UI_METADATA.investigation_form_cd, NBS_UI_METADATA.order_nbr  ";
}
