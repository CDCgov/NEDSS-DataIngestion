package gov.cdc.dataprocessing.constant;

public class ComplexQueries {
    public static final String DMB_QUESTION_OID_METADATA_SQL = "SELECT "
            + "NBS_UI_METADATA.nbs_question_uid, "
            + "NBS_UI_METADATA.add_time, "
            + "NBS_UI_METADATA.add_user_id, "
            + "NBS_UI_METADATA.code_set_group_id, "
            + "NBS_UI_METADATA.data_type, "
            + "NBS_UI_METADATA.mask, "
            + "NBS_UI_METADATA.investigation_form_cd, "
            + "NBS_UI_METADATA.last_chg_time, "
            + "NBS_UI_METADATA.last_chg_user_id, "
            + "NBS_UI_METADATA.question_label, "
            + "NBS_UI_METADATA.question_tool_tip, "
            + "NBS_UI_METADATA.version_ctrl_nbr, "
            + "NBS_UI_METADATA.tab_order_id, "
            + "NBS_UI_METADATA.enable_ind, "
            + "NBS_UI_METADATA.order_nbr, "
            + "NBS_UI_METADATA.default_value, "
            + "NBS_UI_METADATA.required_ind, "
            + "NBS_UI_METADATA.display_ind, "
            + "NBS_UI_Metadata.coinfection_ind_cd, "
            + "NND_METADATA.nnd_metadata_uid nndMetadataUid, "
            + "NBS_UI_METADATA.question_identifier, "
            + "NND_METADATA.question_identifier_nnd questionIdentifierNnd,"
            + "NND_METADATA.question_required_nnd questionRequiredNnd,"
            + "NBS_UI_METADATA.question_oid, "
            + "NBS_UI_METADATA.question_oid_system_txt, "
            + "CODE_SET.code_set_nm codeSetNm, "
            + "CODE_SET.class_cd codeSetClassCd, "
            + "NBS_UI_METADATA.data_location, "
            + "NBS_UI_METADATA.data_cd, "
            + "NBS_UI_METADATA.data_use_cd, "
            + "NBS_UI_METADATA.field_size, "
            + "NBS_UI_METADATA.parent_uid, "
            + "NBS_UI_METADATA.ldf_page_id, "
            + "NBS_UI_METADATA.nbs_ui_metadata_uid, "
            + "NBS_UI_Component.nbs_ui_component_uid nbsUiComponentUid, "
            + "NBS_UI_METADATA.unit_type_cd, "
            + "NBS_UI_METADATA.unit_value, "
            + "NBS_UI_METADATA.nbs_table_uid, "
            + "NBS_UI_METADATA.part_type_cd, "
            + "NBS_UI_METADATA.standard_nnd_ind_cd, "
            + "NBS_UI_METADATA.sub_group_nm, "
            + "NND_METADATA.HL7_segment_field \"hl7SegmentField\", "
            + "NBS_UI_METADATA.question_group_seq_nbr, "
            + "NBS_UI_METADATA.question_unit_identifier "
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
            "wa_question.wa_question_uid, " +
            "wa_question.question_unit_identifier, " +
            "wa_question.add_time, " +
            "wa_question.add_user_id, " +
            "wa_question.code_set_group_id, " +
            "wa_question.data_type, " +
            "wa_question.mask, " +
            "'CORE_INV_FORM' \"investigationFormCd\", "+
            "wa_question.last_chg_time, " +
            "wa_question.last_chg_user_id, " +
            "wa_question.question_nm, " +
            "wa_question.question_tool_tip, " +
            "wa_question.version_ctrl_nbr, " +
            "wa_question.default_value, " +
            "wa_question.question_identifier, " +
            "wa_question.question_oid, " +
            "wa_question.question_oid_system_txt, " +
            "CODESET.code_set_nm \"codeSetNm\", " +
            "CODESET.class_cd \"codeSetClassCd\", " +
            "wa_question.data_location, " +
            "wa_question.data_cd, " +
            "wa_question.data_use_cd, " +
            "wa_question.field_size, " +
            "NBS_UI_Component.nbs_ui_component_uid \"nbsUiComponentUid\", " +
            "wa_question.unit_type_cd, " +
            "wa_question.unit_value, " +
            "wa_question.coinfection_ind_cd, " +
            "wa_question.standard_nnd_ind_cd,     " +
            "wa_question.question_group_seq_nbr  " +
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


    public static final String RETRIEVE_OBSERVATION_QUESTION_SQL =
            " select obs.*," +
//                    "obs.observation_uid observationUid, obs.cd cd, " +
//                    " obs.ctrl_cd_display_form ctrlCdDisplayForm , " +
//                    " obs.version_ctrl_nbr versionCtrlNbr, obs.shared_ind sharedInd, " +
//                    " obs.local_id localId, " +
//                    " obs.cd_desc_txt \"cdDescTxt\" , " +
//                    " obs.cd_system_desc_txt \"cdSystemDescTxt\"  , " +
//                    " obs.cd_system_cd \"cdSystemCd\" ," +
//                    " obs.cd_version \"cdVersion\" , " +
                    " obscode.observation_uid obsCodeUid ," +
                    "obscode.code code, obscode.original_txt originalTxt, obscode.code_system_desc_txt codeSystemDescTxt," +
                    " obsdate.observation_uid obsDateUid , " +
                    " obsdate.from_time fromTime, obsdate.to_time toTime, obsdate.duration_amt durationAmt, obsdate.duration_unit_cd durationUnitCd," +
                    " obsDate.obs_value_date_seq obsValueDateSeq, " +
                    " obsnumeric.observation_uid obsNumericUid , " +
                    " obsnumeric.numeric_value_1 numericValue1, obsnumeric.numeric_value_2 numericValue2, " +
                    " obsnumeric.numeric_scale_1 numericScale1, obsnumeric.numeric_scale_2 numericScale2, " +
                    " obsnumeric.numeric_unit_cd numericUnitCd, obsnumeric.obs_value_numeric_seq obsValueNumericSeq, " +
                    " obstxt.observation_uid obsTxtUid , " +
                    " obstxt.value_txt valueTxt, obstxt.obs_value_txt_seq obsValueTxtSeq, " +
                    " ar2.source_act_uid sourceActUid, ar2.target_act_uid targetActUid, " +
                    " ar2.type_cd typeCd" +
                    " from Observation obs " +
                    " left outer join  obs_value_coded obscode " +
                    " on obs.observation_uid   = obscode.observation_uid " +
                    " left outer join  obs_value_date obsdate " +
                    " on obs.observation_uid = obsdate.observation_uid  " +
                    " left outer join  obs_value_numeric obsnumeric " +
                    " on obs.observation_uid = obsnumeric.observation_uid " +
                    " left outer join  obs_value_txt obstxt " +
                    " on obs.observation_uid = obstxt.observation_uid  " +
                    " inner join  act_relationship ar  " +
                    " on  ar.source_act_uid = obs.observation_uid  " +
                    " left outer join act_relationship ar2 " +
                    " on ar2.target_act_uid  = ar.source_act_uid " +
                    " where   " +
                    "  ar.target_act_uid  = ?1  " +
                    " order by obs.observation_uid, ar2.source_act_uid  ";
}
