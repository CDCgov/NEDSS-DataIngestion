package gov.cdc.dataprocessing.constant;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;

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
            + "NBS_UI_METADATA.data_location dataLocation, " // + "NBS_UI_METADATA.data_location dataLocation, "
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


    public static final String SELECT_PHCPAT_NAMED_BY_PATIENT_COLLECTION1="select subject.cd \"subjectPhcCd\", "
            +"  ct_contact.subject_entity_phc_uid \"subjectEntityPhcUid\", subject.local_id \"subjectPhcLocalId\","
            +"  ct_contact.named_On_Date \"namedOnDate\", ct_contact.CT_Contact_uid \"ctContactUid\",ct_contact.local_Id \"localId\", "
            +" 	ct_contact.subject_Entity_Uid \"subjectEntityUid\", ct_contact.contact_Entity_Uid \"contactEntityUid\", CT_CONTACT.priority_cd \"priorityCd\", ct_contact.disposition_cd \"dispositionCd\", "
            +"  ct_contact.prog_area_cd \"progAreaCd\", ct_contact.named_during_interview_uid \"namedDuringInterviewUid\", cm.fld_foll_up_dispo \"invDispositionCd\", ct_contact.contact_referral_basis_cd \"contactReferralBasisCd\", "
            +"  ct_contact.third_party_entity_uid \"thirdPartyEntityUid\", ct_contact.third_party_entity_phc_uid \"thirdPartyEntityPhcUid\", ct_contact.processing_decision_cd \"contactProcessingDecisionCd\", "
            +"  subjectCM.FLD_FOLL_UP_DISPO \"sourceDispositionCd\", subject.cd \"sourceConditionCd\", subjectperson.curr_sex_cd \"sourceCurrentSexCd\", subjectCM.PAT_INTV_STATUS_CD \"sourceInterviewStatusCd\","
            +" 	 contact.public_health_case_uid \"contactEntityPhcUid\", contact.local_id \"contactPhcLocalId\", person.person_parent_uid \"subjectMprUid\" ,  ixs.interview_date \"interviewDate\", ct_contact.add_time \"createDate\", personcontact.person_parent_uid \"contactMprUid\" "
            +"   from ct_contact with (nolock) "
            +"  left outer join public_health_case subject with (nolock) on (ct_contact.SUBJECT_ENTITY_PHC_UID=subject.public_health_case_uid) "
            +"  left outer join case_management cm with (nolock) on (ct_contact.CONTACT_ENTITY_PHC_UID=cm.public_health_case_uid)"
            +"  left outer join case_management subjectCM with (nolock) on (ct_contact.SUBJECT_ENTITY_PHC_UID=subjectCM.public_health_case_uid) "
            +"  left outer join interview ixs with (nolock) on ct_contact.named_during_interview_uid=ixs.interview_uid "
            +" 	left outer join public_health_case contact with (nolock) on (ct_contact.CONTACT_ENTITY_PHC_UID=contact.PUBLIC_HEALTH_CASE_UID ";
    public static final String SELECT_PHCPAT_NAMED_BY_PATIENT_COLLECTION3= " ) inner join person with (nolock)  on ct_contact.SUBJECT_ENTITY_UID=person.person_uid  "
            +" inner join person subjectperson with (nolock)  on ct_contact.SUBJECT_ENTITY_UID=subjectperson.person_uid "
            +" inner join person personcontact with (nolock) on ct_contact.CONTACT_ENTITY_UID=personcontact.person_uid "
            +" where  ct_contact.record_status_cd='ACTIVE' and Subject_Entity_Phc_Uid =";

    public static final String SELECT_PHCPAT_NAMED_BY_CONTACT_COLLECTION=
            "select ct_contact.named_On_Date \"namedOnDate\", " +
            "ct_contact.CT_Contact_uid \"ctContactUid\", " +
            "ct_contact.local_Id \"localId\", "
            +" ct_contact.subject_Entity_Uid \"subjectEntityUid\", " +
            "ct_contact.contact_Entity_Uid \"contactEntityUid\", " +
            "CT_CONTACT.priority_cd \"priorityCd\", " +
            "ct_contact.disposition_cd \"dispositionCd\", "
            +" ct_contact.prog_area_cd \"progAreaCd\", " +
            "ct_contact.named_during_interview_uid \"namedDuringInterviewUid\", " +
            "ct_contact.contact_referral_basis_cd \"contactReferralBasisCd\", "
            +" ct_contact.third_party_entity_uid \"thirdPartyEntityUid\"," +
            " ct_contact.third_party_entity_phc_uid \"thirdPartyEntityPhcUid\", " +
            "ct_contact.processing_decision_cd \"contactProcessingDecisionCd\", "
            +" subjectCM.FLD_FOLL_UP_DISPO \"sourceDispositionCd\", " +
            "subject.cd \"sourceConditionCd\", " +
            "subjectperson.curr_sex_cd \"sourceCurrentSexCd\", " +
            "subjectCM.PAT_INTV_STATUS_CD \"sourceInterviewStatusCd\","
            +" ct_contact.SUBJECT_ENTITY_PHC_UID \"subjectEntityPhcUid\"," +
            " ixs.interview_date \"interviewDate\", " +
            " ct_contact.add_time \"createDate\", " +
            "subject.local_id \"subjectPhcLocalId\", " +
            "person.person_parent_uid \"contactMprUid\" ," +
            " subject.cd \"subjectPhcCd\", " +
            "subjectperson.person_parent_uid  \"subjectMprUid\" 	"
            +" from ct_contact with (nolock) "
            +"  left outer join public_health_case subject with (nolock) on (ct_contact.SUBJECT_ENTITY_PHC_UID=subject.public_health_case_uid) "
            +"  left outer join case_management cm with (nolock) on (ct_contact.CONTACT_ENTITY_PHC_UID=cm.public_health_case_uid) "
            +"  left outer join case_management subjectCM with (nolock) on (ct_contact.SUBJECT_ENTITY_PHC_UID=subjectCM.public_health_case_uid) "
            +"  left outer join interview ixs with (nolock) on ct_contact.named_during_interview_uid=ixs.interview_uid "
            +" 	inner join person with (nolock) on ct_contact.CONTACT_ENTITY_UID=person.person_uid "
            +" 	inner join person subjectperson with (nolock) on ct_contact.SUBJECT_ENTITY_UID=subjectperson.person_uid "
            +" 	where  ct_contact.record_status_cd='ACTIVE' and CONTACT_ENTITY_PHC_UID =";

    public static final String SELECT_PHCPAT_OTHER_NAMED_BY_CONTACT_COLLECTION="select" +
            " ct_contact.named_On_Date \"namedOnDate\"," +
            " ct_contact.CT_Contact_uid \"ctContactUid\", " +
            "ct_contact.local_Id \"localId\", "
            +" ct_contact.subject_Entity_Uid \"subjectEntityUid\"," +
            " ct_contact.contact_Entity_Uid \"contactEntityUid\"," +
            " CT_CONTACT.priority_cd \"priorityCd\", " +
            "ct_contact.disposition_cd \"dispositionCd\", "
            +" ct_contact.prog_area_cd \"progAreaCd\"," +
            " ct_contact.named_during_interview_uid \"namedDuringInterviewUid\"," +
            " ct_contact.contact_referral_basis_cd \"contactReferralBasisCd\", "
            +" ct_contact.third_party_entity_uid \"thirdPartyEntityUid\","
            + " ct_contact.third_party_entity_phc_uid \"thirdPartyEntityPhcUid\","
            + " ct_contact.processing_decision_cd \"contactProcessingDecisionCd\", "
            +" subjectCM.FLD_FOLL_UP_DISPO \"sourceDispositionCd\", " +
            "subject.cd \"sourceConditionCd\"," +
            " subjectperson.curr_sex_cd \"sourceCurrentSexCd\"," +
            " subjectCM.PAT_INTV_STATUS_CD \"sourceInterviewStatusCd\","
            +" ct_contact.SUBJECT_ENTITY_PHC_UID \"subjectEntityPhcUid\", " +
            " ixs.interview_date \"interviewDate\", " +
            " ct_contact.add_time \"createDate\"," +
            " subject.local_id \"subjectPhcLocalId\"," +
            " person.person_parent_uid \"contactMprUid\" , " +
            "subject.cd \"subjectPhcCd\"," +
            " subjectperson.person_parent_uid  \"subjectMprUid\" 	"
            +" from ct_contact with (nolock) "
            +"  left outer join public_health_case subject  with (nolock) on (ct_contact.SUBJECT_ENTITY_PHC_UID=subject.public_health_case_uid) "
            +"  left outer join case_management cm  with (nolock) on (ct_contact.THIRD_PARTY_ENTITY_PHC_UID=cm.public_health_case_uid) "
            +"  left outer join case_management subjectCM  with (nolock) on (ct_contact.SUBJECT_ENTITY_PHC_UID=subjectCM.public_health_case_uid) "
            +"  left outer join interview ixs with (nolock) on ct_contact.named_during_interview_uid=ixs.interview_uid "
            +" 	inner join person with (nolock) on ct_contact.THIRD_PARTY_ENTITY_UID=person.person_uid "
            +" 	inner join person subjectperson with (nolock) on ct_contact.SUBJECT_ENTITY_UID=subjectperson.person_uid "
            +" 	where  ct_contact.record_status_cd='ACTIVE' and THIRD_PARTY_ENTITY_PHC_UID =";

    public static final String SELECT_LABRESULTED_REFLEXTEST_SUMMARY_FORWORKUP_SQL =
            "SELECT distinct " +
                    " obs.observation_uid \"observationUid\" , "+
                    " obs1.ctrl_Cd_User_Defined_1 \"ctrlCdUserDefined1\", " +
                    " act.source_act_uid \"sourceActUid\", " +
                    " obs1.local_id \"localId\"," +
                    " obs1.cd_desc_txt \"resultedTest\", "+
                    " obs1.cd \"resultedTestCd\", " +
                    " obs1.cd_system_cd \"cdSystemCd\", "+
                    " obs1.status_cd \"resultedTestStatusCd\", "+ // Added this line for ER16368
                    " obsvaluecoded.code \"codedResultValue\", " +
                    " obsvaluecoded.display_name \"organismName\", " +
                    " obsvaluecoded.code_system_cd \"organismCodeSystemCd\", " +
                    " obsnumeric.high_range \"highRange\","+
                    " obsnumeric.low_range \"lowRange\","+
                    " obsnumeric.comparator_cd_1 \"numericResultCompare\","+
                    " obsnumeric.separator_cd \"numericResultSeperator\", " +
                    " obsnumeric.numeric_value_1 \"numericResultValue1\","+
                    " obsnumeric.numeric_value_2 \"numericResultValue2\", " +
                    " obsnumeric.numeric_scale_1 \"numericScale1\"," +
                    " obsnumeric.numeric_scale_2 \"numericScale2\", " +
                    " obsnumeric.numeric_unit_cd \"numericResultUnits\", "+
                    " obsvaluetext.value_txt \"textResultValue\" " +
                    "FROM observation obs " +
                    "    inner JOIN act_relationship act ON  act.target_act_uid = obs.observation_uid " +
                    "        AND (act.target_class_cd = 'OBS') " +
                    "        AND (act.type_cd = :TypeCode) " +
                    "        AND (act.source_class_cd = 'OBS') " +
                    "        AND (act.record_status_cd = 'ACTIVE') " +
                    "    inner JOIN observation obs1 ON  act.source_act_uid = obs1.observation_uid " +
                    "   and (obs1.obs_domain_cd_st_1 = 'Result')"  +
                    "    LEFT OUTER JOIN obs_value_numeric obsnumeric on obsnumeric.observation_uid= obs1.observation_uid " +
                    "    LEFT OUTER JOIN obs_value_coded obsvaluecoded on obsvaluecoded.observation_uid = obs1.observation_uid " +
                    "    LEFT OUTER JOIN obs_value_txt obsvaluetext on obsvaluetext.observation_uid = obs1.observation_uid " +
                    "    and ((obsvaluetext.txt_type_cd IS NULL) or (obsvaluetext.txt_type_cd = 'O'))" +
                    "       and obsvaluetext.obs_value_txt_seq='1'" +
                    "WHERE " +
                    "act.TARGET_ACT_UID = :TargetActUid";

    public static final String GET_SOURCE_ACT_UID_FOR_SUSCEPTIBILITES_SQL = "SELECT distinct " +
            "act.source_act_uid \"uid\" " +
            "FROM " +
            "act_relationship act " +
            "WHERE ( (act.target_class_cd = 'OBS') " +
            "AND (act.type_cd = :TypeCode) " +
            "AND (act.source_class_cd = 'OBS') " +
            "AND (act.record_status_cd = 'ACTIVE') " +
            "AND (act.TARGET_ACT_UID = :TargetActUid) )";

    public static final String SELECT_LABSUSCEPTIBILITES_REFLEXTEST_SUMMARY_FORWORKUP_SQLSERVER =
            "SELECT DISTINCT " +
                    "obs.observation_uid observationUid, " +
                    "obs.ctrl_Cd_User_Defined_1 ctrlCdUserDefined1, " +
                    "act.source_act_uid sourceActUid, " +
                    "obs1.local_id localId, " +
                    "obs1.cd_desc_txt resultedTest," +
                    " obs1.cd resultedTestCd," +
                    " obs1.cd_system_cd cdSystemCd, " +
                    "obsvaluecoded.code codedResultValue, " +
                    "obsvaluecoded.display_name organismName, " +
                    "obsnumeric.comparator_cd_1 numericResultCompare, " +
                    "obsnumeric.high_range highRange," +
                    "obsnumeric.low_range lowRange," +
                    "obsnumeric.separator_cd numericResultSeperator," +
                    "obsnumeric.numeric_value_1 numericResultValue1, " +
                    " obsnumeric.numeric_value_2 numericResultValue2, " +
                    "obsnumeric.numeric_scale_1 numericScale1, " +
                    "obsnumeric.numeric_scale_2 numericScale2, " +
                    "obsnumeric.numeric_unit_cd numericResultUnits " +

                    " FROM         observation obs " +
                    " inner join act_relationship act on act.target_act_uid = obs.observation_uid " +
                    " inner join  observation obs1 on act.source_act_uid = obs1.observation_uid   " +
                    "    and obs1.obs_domain_cd_st_1 = 'R_Result'" +
                    " left outer join obs_value_numeric obsnumeric on obsnumeric.observation_uid = obs1.observation_uid " +
                    " left outer join obs_value_coded obsvaluecoded on obsvaluecoded.observation_uid = obs1.observation_uid " +
                    " WHERE " +
                    " act.target_class_cd = 'OBS' " +
                    " AND act.type_cd = :TypeCode AND act.source_class_cd = 'OBS' " +
                    " AND act.record_status_cd = 'ACTIVE' " +
                    " AND act.TARGET_ACT_UID = :TargetActUid";


    public static final String ASSOCIATED_INV_QUERY =
            "select public_health_case.local_id \"localId\", " +
                    "public_health_case.cd \"cd\", " +
                    "act_relationship.add_reason_cd \"dispositionCd\" " +
                    "from public_health_case  with (nolock) " +
                    "inner join act_relationship  with (nolock) on " +
                    "public_health_case.public_health_case_uid = act_relationship.target_act_uid " +
                    "and act_relationship.source_act_uid = :ActUid " +
                    "and act_relationship.source_class_cd = :ClassCd " +
                    "and target_class_cd = 'CASE' " +
                    "and public_health_case.record_status_cd!='LOG_DEL' ";

    public static final String TREATMENTS_FOR_A_PHC_ORACLE =
            "SELECT       " +
                    "phc.public_health_case_uid \"phcUid\", " +
                    "Treatment.treatment_uid \"treatmentUid\", " +
                    "Treatment.cd \"treatmentNameCode\", " +
                    "Treatment.cd_desc_txt \"customTreatmentNameCode\", " +
                    "Treatment.activity_from_time \"activityFromTime\",  " +

                    "FROM " +     "Public_Health_Case" + " phc with (nolock) , " +
                    "Act_Relationship" + " ar with (nolock) , " +
                    "treatment" + " Treatment with (nolock) , " +
                    "Participation" + " par  with (nolock) " +
                    "WHERE        phc.public_health_case_uid = ar.target_act_uid " +
                    "AND          UPPER(ar.record_status_cd) = 'ACTIVE' " +
                    "AND          UPPER(Treatment.record_status_cd) = 'ACTIVE' " +
                    "AND          ar.source_class_cd = '" + NEDSSConstant.TREATMENT_CLASS_CODE + "' " +
                    "AND          ar.target_class_cd = '" + NEDSSConstant.PUBLIC_HEALTH_CASE_CLASS_CODE + "' " +
                    "AND          Treatment.treatment_uid = ar.source_act_uid " +
                    "AND          par.act_uid =  Treatment.treatment_uid " +
                    "AND          par.act_class_cd = '" + NEDSSConstant.TREATMENT_CLASS_CODE + "' " +
                    "AND          par.record_status_cd = 'ACTIVE' " +
                    "AND          phc.public_health_case_uid = :PhcUid ";

    public static final String DOCUMENT_FOR_A_PHC ="SELECT " +
            "phc.public_health_case_uid \"phcUid\"," +
            "Document.nbs_document_uid  \"nbsDocumentUid\"," +
            "Document.doc_type_cd \"docType\", "+
            "Document.cd_desc_txt \"cdDescTxt\", " +
            "Document.add_time \"addTime\",  " +
            "Document.local_id \"localId\"," +
            "Document.external_version_ctrl_nbr \"externalVersionCtrlNbr\"," +
            "Document.doc_purpose_cd \"docPurposeCd\", " +
            "ndm.doc_type_cd \"docTypeCd\", " +
            "ndm.nbs_document_metadata_uid \"nbsDocumentMetadataUid\", " +
            "Document.doc_status_cd \"docStatusCd\", "
            + "eep.doc_event_type_cd   \"docEventTypeCd\" " +
            "FROM " +
            "Public_Health_Case phc  with (nolock)  inner join " +
            "Act_Relationship ar  with (nolock) on "
            + "phc.public_health_case_uid = ar.target_act_uid "
            + "AND UPPER(ar.record_status_cd) = 'ACTIVE' "
            + "AND ar.source_class_cd = 'DOC' "
            + "AND ar.target_class_cd = 'CASE' "
            + "AND ar.status_cd='A' "
            + "AND ar.type_cd='DocToPHC' "
            + "inner join nbs_document Document  with (nolock) on "
            + "Document.nbs_document_uid = ar.source_act_uid "
            + "AND UPPER(Document.record_status_cd) = 'PROCESSED' "
            + "inner join nbs_document_metadata ndm  with (nolock) on "
            + "Document.nbs_document_metadata_uid = ndm.nbs_document_metadata_uid "
            + " left outer join edx_event_process eep  with (nolock) on "
            + " eep.nbs_document_uid = Document.nbs_document_uid "
            + " and eep.doc_event_type_cd in('CASE','LabReport','MorbReport','CT') "
            + " and eep.parsed_ind = 'N' "
            + " Where phc.public_health_case_uid = :PhcUid ";


    public static final String SELECT_NOTIFICATION_FOR_INVESTIGATION_SQL =
            "select Notification.notification_uid NotificationUid, " +
                    " Notification.cd cdNotif, " +
                    "Notification.add_time AddTime," +
                    "Notification.rpt_sent_time RptSentTime, " +
                    "Notification.record_status_time RecordStatusTime, " +
                    " Notification.case_condition_cd Cd, " +
                    " Notification.jurisdiction_cd jurisdictionCd , "+
                    " Notification.program_jurisdiction_oid programJurisdictionOid , "+
                    " Public_health_case.case_class_cd ," +
                    " Notification.auto_resend_ind AutoResendInd, "+
                    " Notification.case_class_cd CaseClassCd, "+
                    " Notification.local_id LocalId, " +
                    "Notification.txt Txt, "+
                    " Notification.record_status_cd RecordStatusCd, " +
                    "'F' isHistory ," +
                    " cc.nnd_ind \"nndInd\" , " +
                    "exportReceiving.receiving_system_nm recipient " +
                    " from Public_health_case Public_health_case  with (nolock) , act_relationship ar  with (nolock) , nbs_srte..condition_code cc  with (nolock) , "+
                    " notification Notification   with (nolock) " +
                    "LEFT JOIN Export_receiving_facility exportReceiving  with (nolock) " +
                    " ON exportReceiving.export_receiving_facility_uid = Notification.export_receiving_facility_uid " +
                    " where " +
                    " Public_health_case.Public_health_case_uid = ar.target_act_uid " +
                    " and ar.type_cd = '" +
                    NEDSSConstant.ACT106_TYP_CD +
                    "' and ar.target_class_cd = 'CASE' " +
                    " and Notification.notification_uid = ar.source_act_uid" +
                    " and Notification.case_condition_cd = cc.condition_cd " +
                    " and Public_health_case.record_status_cd <> '" +
                    NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE + "'" +
                    " and Public_health_case.public_health_case_uid = :PhcUid";

    public static final String SELECT_NOTIFICATION_HIST_FOR_INVESTIGATION_SQL =
            "select notHist.notification_uid NotificationUid, " +
                    " Notification.cd cdNotif,"+
                    " notHist.add_time AddTime," +
                    " notHist.rpt_sent_time RptSentTime, " +
                    " notHist.record_status_time RecordStatusTime, " +
                    " notHist.jurisdiction_cd jurisdictionCd, "+
                    " notHist.program_jurisdiction_oid programJurisdictionOid, "+
                    " notHist.case_condition_cd Cd, " +
                    "notHist.version_ctrl_nbr VersionCtrlNbr,"+
                    " Public_health_case.case_class_cd ," +
                    " notHist.case_class_cd CaseClassCd, "+
                    " notHist.local_id LocalId," +
                    " notHist.txt Txt, "+
                    " notHist.record_status_cd RecordStatusCd," +
                    " 'T' isHistory ," +
                    " cc.nnd_ind \"nndInd\" , " +
                    " exportReceiving.receiving_system_nm recipient " +
                    " from Public_health_case Public_health_case  with (nolock) , act_relationship ar  with (nolock) , nbs_srte..condition_code cc  with (nolock) , "+
                    " notification Notification  with (nolock) , notification_hist notHist  with (nolock)  " +
                    "LEFT JOIN Export_receiving_facility exportReceiving  with (nolock) " +
                    " ON exportReceiving.export_receiving_facility_uid = notHist.export_receiving_facility_uid " +
                    " where " +
                    " Public_health_case.Public_health_case_uid = ar.target_act_uid " +
                    " and ar.type_cd = '" +
                    NEDSSConstant.ACT106_TYP_CD +
                    "' and ar.target_class_cd = 'CASE' " +
                    " and Notification.notification_uid = ar.source_act_uid " +
                    " and Notification.case_condition_cd = cc.condition_cd " +
                    " and notHist.notification_uid = Notification.notification_uid " +
                    " and Public_health_case.record_status_cd <> '" +
                    NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE + "'" +
                    " and Public_health_case.public_health_case_uid = :PhcUid";


    public static final String SELECT_NOTIFICATION_FOR_INVESTIGATION_SQL1 =
            " select Notification.notification_uid NotificationUid,  Notification.cd cdNotif,"+
                    "  Notification.add_time AddTime, " +
                    " Notification.rpt_sent_time RptSentTime, " +
                    " Notification.record_status_time \"recordStatusTime\", " +
                    " Public_health_case.case_class_cd, " +
                    " Notification.case_condition_cd \"Cd\", "+
                    " Notification.jurisdiction_cd \"jurisdictionCd\" , "+
                    " Notification.program_jurisdiction_oid \"programJurisdictionOid\" , "+
                    " Notification.auto_resend_ind AutoResendInd, "+
                    " Notification.case_class_cd CaseClassCd, "+
                    " Notification.local_id LocalId, Notification.txt Txt, "+
                    " Notification.record_status_cd RecordStatusCd, 'F' isHistory  ," +
                    " cc.nnd_ind \"nndInd\" , " +
                    " exportReceiving.receiving_system_nm recipient " +
                    " from Public_health_case Public_health_case  with (nolock) "+
                    " inner join  act_relationship ar  with (nolock)  on " +
                    " Public_health_case.Public_health_case_uid = ar.target_act_uid  " +
                    " and ar.type_cd = '" + NEDSSConstant.ACT106_TYP_CD + "'" +
                    " and ar.target_class_cd = 'CASE'  " +
                    " inner join notification Notification  with (nolock) on " +
                    " Notification.notification_uid = ar.source_act_uid " +
                    " inner join nbs_srte..condition_code cc  with (nolock) on " +
                    " Notification.case_condition_cd = cc.condition_cd " +
                    " LEFT JOIN Export_receiving_facility exportReceiving  with (nolock) " +
                    " ON exportReceiving.export_receiving_facility_uid = Notification.export_receiving_facility_uid " +
                    " where Public_health_case.record_status_cd <> '" +
                    NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE + "'" +
                    " and Public_health_case.public_health_case_uid = :PhcUid";



    public static final String SELECT_NOTIFICATION_HIST_FOR_INVESTIGATION_SQL1 =
            " select notHist.notification_uid NotificationUid, " +
                    " notHist.cd cdNotif, " +
                    "notHist.add_time AddTime, " +
                    " notHist.rpt_sent_time RptSentTime, " +
                    " notHist.record_status_time \"recordStatusTime\", " +
                    " Public_health_case.case_class_cd, " +
                    " notHist.case_condition_cd \"Cd\", " +
                    " notHist.jurisdiction_cd \"jurisdictionCd\" , "+
                    " notHist.program_jurisdiction_oid \"programJurisdictionOid\" , "+
                    " notHist.case_class_cd CaseClassCd, "+
                    " notHist.version_ctrl_nbr VersionCtrlNbr, "+
                    " notHist.local_id LocalId, notHist.txt Txt, "+
                    " notHist.record_status_cd RecordStatusCd, 'T' isHistory ," +
                    " cc.nnd_ind \"nndInd\" , " +
                    " exportReceiving.receiving_system_nm recipient " +
                    " from Public_health_case Public_health_case  with (nolock) "+
                    " inner join  act_relationship ar  with (nolock) on " +
                    " Public_health_case.Public_health_case_uid = ar.target_act_uid  " +
                    " and ar.type_cd = '" + NEDSSConstant.ACT106_TYP_CD + "'" +
                    " and ar.target_class_cd = 'CASE'  " +
                    " inner join notification Notification  with (nolock) on " +
                    " Notification.notification_uid = ar.source_act_uid " +
                    " inner join nbs_srte..condition_code cc  with (nolock) on " +
                    " Notification.case_condition_cd = cc.condition_cd " +
                    " inner join notification_hist notHist  with (nolock) on" +
                    " notHist.notification_uid = Notification.notification_uid " +
                    " LEFT JOIN Export_receiving_facility exportReceiving  with (nolock) " +
                    " ON exportReceiving.export_receiving_facility_uid = notHist.export_receiving_facility_uid " +
                    " where Public_health_case.record_status_cd <> '" +
                    NEDSSConstant.RECORD_STATUS_LOGICAL_DELETE + "'" +
                    " and Public_health_case.public_health_case_uid = :PhcUid";


    public static String SELECT_LDF =  "SELECT "+
            " sf.ldf_uid \"ldfUid\", "+
            " sf.business_object_nm  \"businessObjNm\", "+
            " sf.add_time    \"addTime\", "+
            " sf.business_object_uid  \"businessObjUid\", "+
            " sf.last_chg_time \"lastChgTime\", "+
            " sf.ldf_value    \"ldfValue\", "+
            " sf.version_ctrl_nbr \"versionCtrlNbr\" "+
            " from State_defined_field_data sf, "+
            " state_defined_field_metadata sdfmd "+
            " where  sf.ldf_uid = sdfmd.ldf_uid "+
            " and sf.business_object_uid = :businessObjUid ";


    public static final String GET_NBS_DOCUMENT = " SELECT"
            + " nbsdoc.nbs_document_uid  \"nbsDocumentUid\", "
            + " nbsdoc.local_id  \"localId\","
            + " nbsdoc.doc_type_cd  \"docTypeCd\","
            + " nbsdoc.jurisdiction_cd \"jurisdictionCd\","
            + " nbsdoc.prog_area_cd  \"progAreaCd\","
            + " nbsdoc.doc_status_cd \"docStatusCd\", "
            + " nbsdoc.add_time \"addTime\", "
            + " nbsdoc.txt \"txt\", "
            + " nbsdoc.version_ctrl_nbr \"versionCtrlNbr\", "
            + " nbsdoc.doc_purpose_cd \"docPurposeCd\", "
            + " nbsdoc.cd_desc_txt \"cdDescTxt\", "
            + " nbsdoc.sending_facility_nm \"sendingFacilityNm\", "
            + " nbsdoc.add_user_id \"addUserId\", "
            + " nbsdoc.record_status_cd \"recordStatusCd\", "
            + " nbsdoc.processing_decision_cd \"processingDecisionCd\", "
            + " nbsdoc.processing_decision_txt \"processingDecisiontxt\", "
            + " nbsdoc.external_version_ctrl_nbr \"externalVersionCtrlNbr\", "
            + " nbsdoc.cd \"cd\", "
            + " nbsdoc.doc_payload \"docPayload\", "
            + " nbsdoc.phdc_doc_derived \"phdcDocDerived\", "
            + " nbsdoc.payload_view_ind_cd \"payloadViewIndCd\", "
            + " nbsdoc.nbs_document_metadata_uid \"nbsDocumentMetadataUid\", "
            + " nbsdoc.record_status_Time \"recordStatusTime\", "
            + " nbsdoc.program_jurisdiction_oid \"programJurisdictionOid\", "
            + " nbsdoc.shared_ind \"sharedInd\", "
            + " nbsdoc.last_chg_user_id \"lastChgUserId\", "
            + " nbsdoc.nbs_interface_uid \"nbsInterfaceUid\", "
            + " eep.doc_event_type_cd \"docEventTypeCd\" "
            + " nbsdoc.effective_time \"effectiveTime\", "

            + " per.person_uid  \"personUid\", "
            + " per.person_parent_uid \"MPRUid\", "
            + " nbsdoc.last_chg_time \"lastChgTime\", "

            + " FROM nbs_document nbsdoc "
            + " inner join participation particip on "
            + " particip.act_uid = nbsdoc.nbs_document_uid "
            + " inner join person per on "
            + " particip.subject_entity_uid = per.person_uid "
            + " and particip.type_cd='"+NEDSSConstant.SUBJECT_OF_DOC+ "' "
            + " inner join person_name pername on "
            + " per.person_uid = pername.person_uid "
            + " left outer join edx_event_process eep on "
            + " eep.nbs_document_uid = nbsdoc.nbs_document_uid "
            + " and eep.doc_event_type_cd in('CASE','LabReport','MorbReport','CT') "
            + " and eep.parsed_ind = 'N' "
            + " WHERE  nbsdoc.nbs_document_uid = :NbsUid";



    public static final String COINFECTION_INV_LIST_FOR_GIVEN_COINFECTION_ID_SQL =
            "select distinct " +
                    "phc.public_health_case_uid \"publicHealthCaseUid\"," +
                    " phc.cd \"conditionCd\" " +
                    "from "
            + "Public_health_case phc, person, Participation   "
            + "where phc.investigation_status_cd='O'  and phc.record_status_cd !='LOG_DEL'  "
            + "and phc.public_health_case_uid=participation.act_uid "
            + "and participation.type_cd ='SubjOfPHC' "
            + "and participation.subject_entity_uid =person.person_uid "
            + "and coinfection_id= :CoInfect "
            + "and person.person_parent_uid = :PersonUid ";


}
