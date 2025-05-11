package gov.cdc.dataprocessing.constant.query;

public class ObservationQuery {
    public static final String INSERT_OBSERVATION = """
            INSERT INTO Observation (
                observation_uid,
                activity_duration_amt,
                activity_duration_unit_cd,
                activity_from_time,
                activity_to_time,
                add_reason_cd,
                add_time,
                add_user_id,
                cd,
                cd_desc_txt,
                cd_system_cd,
                cd_system_desc_txt,
                confidentiality_cd,
                confidentiality_desc_txt,
                ctrl_cd_display_form,
                ctrl_cd_user_defined_1,
                ctrl_cd_user_defined_2,
                ctrl_cd_user_defined_3,
                ctrl_cd_user_defined_4,
                derivation_exp,
                effective_duration_amt,
                effective_duration_unit_cd,
                effective_from_time,
                effective_to_time,
                electronic_ind,
                group_level_cd,
                jurisdiction_cd,
                lab_condition_cd,
                last_chg_reason_cd,
                last_chg_time,
                last_chg_user_id,
                local_id,
                method_cd,
                method_desc_txt,
                obs_domain_cd,
                obs_domain_cd_st_1,
                pnu_cd,
                priority_cd,
                priority_desc_txt,
                prog_area_cd,
                record_status_cd,
                record_status_time,
                repeat_nbr,
                status_cd,
                status_time,
                subject_person_uid,
                target_site_cd,
                target_site_desc_txt,
                txt,
                user_affiliation_txt,
                value_cd,
                ynu_cd,
                program_jurisdiction_oid,
                shared_ind,
                version_ctrl_nbr,
                alt_cd,
                alt_cd_desc_txt,
                alt_cd_system_cd,
                alt_cd_system_desc_txt,
                cd_derived_ind,
                rpt_to_state_time,
                cd_version,
                processing_decision_cd,
                pregnant_ind_cd,
                pregnant_week,
                processing_decision_txt
            ) VALUES (
                :observation_uid,
                :activity_duration_amt,
                :activity_duration_unit_cd,
                :activity_from_time,
                :activity_to_time,
                :add_reason_cd,
                :add_time,
                :add_user_id,
                :cd,
                :cd_desc_txt,
                :cd_system_cd,
                :cd_system_desc_txt,
                :confidentiality_cd,
                :confidentiality_desc_txt,
                :ctrl_cd_display_form,
                :ctrl_cd_user_defined_1,
                :ctrl_cd_user_defined_2,
                :ctrl_cd_user_defined_3,
                :ctrl_cd_user_defined_4,
                :derivation_exp,
                :effective_duration_amt,
                :effective_duration_unit_cd,
                :effective_from_time,
                :effective_to_time,
                :electronic_ind,
                :group_level_cd,
                :jurisdiction_cd,
                :lab_condition_cd,
                :last_chg_reason_cd,
                :last_chg_time,
                :last_chg_user_id,
                :local_id,
                :method_cd,
                :method_desc_txt,
                :obs_domain_cd,
                :obs_domain_cd_st_1,
                :pnu_cd,
                :priority_cd,
                :priority_desc_txt,
                :prog_area_cd,
                :record_status_cd,
                :record_status_time,
                :repeat_nbr,
                :status_cd,
                :status_time,
                :subject_person_uid,
                :target_site_cd,
                :target_site_desc_txt,
                :txt,
                :user_affiliation_txt,
                :value_cd,
                :ynu_cd,
                :program_jurisdiction_oid,
                :shared_ind,
                :version_ctrl_nbr,
                :alt_cd,
                :alt_cd_desc_txt,
                :alt_cd_system_cd,
                :alt_cd_system_desc_txt,
                :cd_derived_ind,
                :rpt_to_state_time,
                :cd_version,
                :processing_decision_cd,
                :pregnant_ind_cd,
                :pregnant_week,
                :processing_decision_txt
            );
            
            """;

    public static final String UPDATE_OBSERVATION = """
            UPDATE Observation SET
                activity_duration_amt = :activity_duration_amt,
                activity_duration_unit_cd = :activity_duration_unit_cd,
                activity_from_time = :activity_from_time,
                activity_to_time = :activity_to_time,
                add_reason_cd = :add_reason_cd,
                add_time = :add_time,
                add_user_id = :add_user_id,
                cd = :cd,
                cd_desc_txt = :cd_desc_txt,
                cd_system_cd = :cd_system_cd,
                cd_system_desc_txt = :cd_system_desc_txt,
                confidentiality_cd = :confidentiality_cd,
                confidentiality_desc_txt = :confidentiality_desc_txt,
                ctrl_cd_display_form = :ctrl_cd_display_form,
                ctrl_cd_user_defined_1 = :ctrl_cd_user_defined_1,
                ctrl_cd_user_defined_2 = :ctrl_cd_user_defined_2,
                ctrl_cd_user_defined_3 = :ctrl_cd_user_defined_3,
                ctrl_cd_user_defined_4 = :ctrl_cd_user_defined_4,
                derivation_exp = :derivation_exp,
                effective_duration_amt = :effective_duration_amt,
                effective_duration_unit_cd = :effective_duration_unit_cd,
                effective_from_time = :effective_from_time,
                effective_to_time = :effective_to_time,
                electronic_ind = :electronic_ind,
                group_level_cd = :group_level_cd,
                jurisdiction_cd = :jurisdiction_cd,
                lab_condition_cd = :lab_condition_cd,
                last_chg_reason_cd = :last_chg_reason_cd,
                last_chg_time = :last_chg_time,
                last_chg_user_id = :last_chg_user_id,
                local_id = :local_id,
                method_cd = :method_cd,
                method_desc_txt = :method_desc_txt,
                obs_domain_cd = :obs_domain_cd,
                obs_domain_cd_st_1 = :obs_domain_cd_st_1,
                pnu_cd = :pnu_cd,
                priority_cd = :priority_cd,
                priority_desc_txt = :priority_desc_txt,
                prog_area_cd = :prog_area_cd,
                record_status_cd = :record_status_cd,
                record_status_time = :record_status_time,
                repeat_nbr = :repeat_nbr,
                status_cd = :status_cd,
                status_time = :status_time,
                subject_person_uid = :subject_person_uid,
                target_site_cd = :target_site_cd,
                target_site_desc_txt = :target_site_desc_txt,
                txt = :txt,
                user_affiliation_txt = :user_affiliation_txt,
                value_cd = :value_cd,
                ynu_cd = :ynu_cd,
                program_jurisdiction_oid = :program_jurisdiction_oid,
                shared_ind = :shared_ind,
                version_ctrl_nbr = :version_ctrl_nbr,
                alt_cd = :alt_cd,
                alt_cd_desc_txt = :alt_cd_desc_txt,
                alt_cd_system_cd = :alt_cd_system_cd,
                alt_cd_system_desc_txt = :alt_cd_system_desc_txt,
                cd_derived_ind = :cd_derived_ind,
                rpt_to_state_time = :rpt_to_state_time,
                cd_version = :cd_version,
                processing_decision_cd = :processing_decision_cd,
                pregnant_ind_cd = :pregnant_ind_cd,
                pregnant_week = :pregnant_week,
                processing_decision_txt = :processing_decision_txt
            WHERE observation_uid = :observation_uid;
            
            """;

    public static final String INSERT_SQL_OBS_REASON = """
        INSERT INTO Observation_reason (
            observation_uid, reason_cd, reason_desc_txt
        ) VALUES (
            :observation_uid, :reason_cd, :reason_desc_txt
        )
        """;

    public static final String UPDATE_SQL_OBS_REASON = """
        UPDATE Observation_reason SET
            reason_desc_txt = :reason_desc_txt
        WHERE observation_uid = :observation_uid
          AND reason_cd = :reason_cd
        """;

    public static final String DELETE_SQL_OBS_REASON = """
    DELETE FROM Observation_reason
    WHERE observation_uid = :observation_uid
      AND reason_cd = :reason_cd
    """;

    public static final String INSERT_SQL_OBS_INTERP = """
        INSERT INTO Observation_interp (
            observation_uid, interpretation_cd, interpretation_desc_txt
        ) VALUES (
            :observation_uid, :interpretation_cd, :interpretation_desc_txt
        )
        """;

    public static final String UPDATE_SQL_OBS_INTERP = """
        UPDATE Observation_interp SET
            interpretation_desc_txt = :interpretation_desc_txt
        WHERE observation_uid = :observation_uid
          AND interpretation_cd = :interpretation_cd
        """;

    public static final String DELETE_SQL_OBS_INTERP = """
    DELETE FROM Observation_interp
    WHERE observation_uid = :observation_uid
      AND interpretation_cd = :interpretation_cd
    """;

    public static final String INSERT_SQL_OBS_VALUE_CODED = """
        INSERT INTO Obs_value_coded (
            observation_uid, code, code_system_cd, code_system_desc_txt, code_version,
            display_name, original_txt, alt_cd, alt_cd_desc_txt, alt_cd_system_cd,
            alt_cd_system_desc_txt, code_derived_ind
        ) VALUES (
            :observation_uid, :code, :code_system_cd, :code_system_desc_txt, :code_version,
            :display_name, :original_txt, :alt_cd, :alt_cd_desc_txt, :alt_cd_system_cd,
            :alt_cd_system_desc_txt, :code_derived_ind
        )
        """;

    public static final String UPDATE_SQL_OBS_VALUE_CODED = """
        UPDATE Obs_value_coded SET
            code_system_cd = :code_system_cd,
            code_system_desc_txt = :code_system_desc_txt,
            code_version = :code_version,
            display_name = :display_name,
            original_txt = :original_txt,
            alt_cd = :alt_cd,
            alt_cd_desc_txt = :alt_cd_desc_txt,
            alt_cd_system_cd = :alt_cd_system_cd,
            alt_cd_system_desc_txt = :alt_cd_system_desc_txt,
            code_derived_ind = :code_derived_ind
        WHERE observation_uid = :observation_uid AND code = :code
        """;

    public static final String DELETE_SQL_OBS_VALUE_CODED = """
        DELETE FROM Obs_value_coded
        WHERE observation_uid = :observation_uid AND code = :code
        """;

    public static final String INSERT_SQL_OBS_VALUE_TXT = """
            INSERT INTO Obs_value_txt (
                        observation_uid, obs_value_txt_seq, data_subtype_cd, encoding_type_cd,
                        txt_type_cd, value_image_txt, value_txt
                    ) VALUES (
                        :observation_uid, :obs_value_txt_seq, :data_subtype_cd, :encoding_type_cd,
                        :txt_type_cd, :value_image_txt, :value_txt
                    )
            """;

    public static final String UPDATE_SQL_OBS_VALUE_TXT = """
        UPDATE Obs_value_txt SET
            data_subtype_cd = :data_subtype_cd,
            encoding_type_cd = :encoding_type_cd,
            txt_type_cd = :txt_type_cd,
            value_image_txt = :value_image_txt,
            value_txt = :value_txt
        WHERE observation_uid = :observation_uid AND obs_value_txt_seq = :obs_value_txt_seq
        """;

    public static final String DELETE_SQL_OBS_VALUE_TXT = """
        DELETE FROM Obs_value_txt
        WHERE observation_uid = :observation_uid AND obs_value_txt_seq = :obs_value_txt_seq
        """;

    public static final String INSERT_SQL_OBS_VALUE_DATE = """
        INSERT INTO Obs_value_date (
            observation_uid, obs_value_date_seq, duration_amt, duration_unit_cd,
            from_time, to_time
        ) VALUES (
            :observation_uid, :obs_value_date_seq, :duration_amt, :duration_unit_cd,
            :from_time, :to_time
        )
        """;

    public static final String UPDATE_SQL_OBS_VALUE_DATE = """
        UPDATE Obs_value_date SET
            duration_amt = :duration_amt,
            duration_unit_cd = :duration_unit_cd,
            from_time = :from_time,
            to_time = :to_time
        WHERE observation_uid = :observation_uid AND obs_value_date_seq = :obs_value_date_seq
        """;

    public static final String DELETE_SQL_OBS_VALUE_DATE = """
        DELETE FROM Obs_value_date
        WHERE observation_uid = :observation_uid AND obs_value_date_seq = :obs_value_date_seq
        """;

    public static final String INSERT_SQL_OBS_VALUE_NUMERIC = """
        INSERT INTO Obs_value_numeric (
            observation_uid, obs_value_numeric_seq, high_range, low_range, comparator_cd_1,
            numeric_value_1, numeric_value_2, numeric_unit_cd, separator_cd,
            numeric_scale_1, numeric_scale_2
        ) VALUES (
            :observation_uid, :obs_value_numeric_seq, :high_range, :low_range, :comparator_cd_1,
            :numeric_value_1, :numeric_value_2, :numeric_unit_cd, :separator_cd,
            :numeric_scale_1, :numeric_scale_2
        )
        """;

    public static final String UPDATE_SQL_OBS_VALUE_NUMERIC = """
        UPDATE Obs_value_numeric SET
            high_range = :high_range,
            low_range = :low_range,
            comparator_cd_1 = :comparator_cd_1,
            numeric_value_1 = :numeric_value_1,
            numeric_value_2 = :numeric_value_2,
            numeric_unit_cd = :numeric_unit_cd,
            separator_cd = :separator_cd,
            numeric_scale_1 = :numeric_scale_1,
            numeric_scale_2 = :numeric_scale_2
        WHERE observation_uid = :observation_uid AND obs_value_numeric_seq = :obs_value_numeric_seq
        """;

    public static final String DELETE_SQL_OBS_VALUE_NUMERIC = """
        DELETE FROM Obs_value_numeric
        WHERE observation_uid = :observation_uid AND obs_value_numeric_seq = :obs_value_numeric_seq
        """;

    public static final String SELECT_OBSERVATION_BY_UID = """
        SELECT 
            observation_uid AS observationUid,
            activity_duration_amt AS activityDurationAmt,
            activity_duration_unit_cd AS activityDurationUnitCd,
            activity_from_time AS activityFromTime,
            activity_to_time AS activityToTime,
            add_reason_cd AS addReasonCd,
            add_time AS addTime,
            add_user_id AS addUserId,
            alt_cd AS altCd,
            alt_cd_desc_txt AS altCdDescTxt,
            alt_cd_system_cd AS altCdSystemCd,
            alt_cd_system_desc_txt AS altCdSystemDescTxt,
            cd AS cd,
            cd_desc_txt AS cdDescTxt,
            cd_system_cd AS cdSystemCd,
            cd_system_desc_txt AS cdSystemDescTxt,
            confidentiality_cd AS confidentialityCd,
            confidentiality_desc_txt AS confidentialityDescTxt,
            ctrl_cd_display_form AS ctrlCdDisplayForm,
            ctrl_cd_user_defined_1 AS ctrlCdUserDefined1,
            ctrl_cd_user_defined_2 AS ctrlCdUserDefined2,
            ctrl_cd_user_defined_3 AS ctrlCdUserDefined3,
            ctrl_cd_user_defined_4 AS ctrlCdUserDefined4,
            derivation_exp AS derivationExp,
            effective_duration_amt AS effectiveDurationAmt,
            effective_duration_unit_cd AS effectiveDurationUnitCd,
            effective_from_time AS effectiveFromTime,
            effective_to_time AS effectiveToTime,
            electronic_ind AS electronicInd,
            group_level_cd AS groupLevelCd,
            jurisdiction_cd AS jurisdictionCd,
            lab_condition_cd AS labConditionCd,
            last_chg_reason_cd AS lastChgReasonCd,
            last_chg_time AS lastChgTime,
            last_chg_user_id AS lastChgUserId,
            local_id AS localId,
            method_cd AS methodCd,
            method_desc_txt AS methodDescTxt,
            obs_domain_cd AS obsDomainCd,
            obs_domain_cd_st_1 AS obsDomainCdSt1,
            pnu_cd AS pnuCd,
            priority_cd AS priorityCd,
            priority_desc_txt AS priorityDescTxt,
            prog_area_cd AS progAreaCd,
            record_status_cd AS recordStatusCd,
            record_status_time AS recordStatusTime,
            repeat_nbr AS repeatNbr,
            status_cd AS statusCd,
            status_time AS statusTime,
            subject_person_uid AS subjectPersonUid,
            target_site_cd AS targetSiteCd,
            target_site_desc_txt AS targetSiteDescTxt,
            txt AS txt,
            user_affiliation_txt AS userAffiliationTxt,
            value_cd AS valueCd,
            ynu_cd AS ynuCd,
            program_jurisdiction_oid AS programJurisdictionOid,
            shared_ind AS sharedInd,
            version_ctrl_nbr AS versionCtrlNbr,
            cd_derived_ind AS cdDerivedInd,
            rpt_to_state_time AS rptToStateTime,
            cd_version AS cdVersion,
            processing_decision_cd AS processingDecisionCd,
            processing_decision_txt AS processingDecisionTxt,
            pregnant_ind_cd AS pregnantIndCd,
            pregnant_week AS pregnantWeek
        FROM Observation
        WHERE observation_uid = :observation_uid
        """;


    public static final String RETRIEVE_OBSERVATION_QUESTION_SQL =
            " select obs.*," +
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
                    "  ar.target_act_uid  = :target_act_uid " +
                    " order by obs.observation_uid, ar2.source_act_uid  ";

    public static final String SELECT_BY_OBS_REASONS = """
        SELECT
            observation_uid AS observationUid,
            reason_cd AS reasonCd,
            reason_desc_txt AS reasonDescTxt
        FROM Observation_reason
        WHERE observation_uid = :observation_uid
        """;

    public static final String SELECT_BY_OBS_INTERP_UID = """
        SELECT
            observation_uid AS observationUid,
            interpretation_cd AS interpretationCd,
            interpretation_desc_txt AS interpretationDescTxt
        FROM Observation_interp
        WHERE observation_uid = :observation_uid
        """;

    public static final String SELECT_BY_OBS_CODED_UID = """
        SELECT
            observation_uid AS observationUid,
            code AS code,
            code_system_cd AS codeSystemCd,
            code_system_desc_txt AS codeSystemDescTxt,
            code_version AS codeVersion,
            display_name AS displayName,
            original_txt AS originalTxt,
            alt_cd AS altCd,
            alt_cd_desc_txt AS altCdDescTxt,
            alt_cd_system_cd AS altCdSystemCd,
            alt_cd_system_desc_txt AS altCdSystemDescTxt,
            code_derived_ind AS codeDerivedInd
        FROM Obs_value_coded
        WHERE observation_uid = :observation_uid
        """;

    public static final String SELECT_BY_OBS_TXT = """
        SELECT
            observation_uid AS observationUid,
            obs_value_txt_seq AS obsValueTxtSeq,
            data_subtype_cd AS dataSubtypeCd,
            encoding_type_cd AS encodingTypeCd,
            txt_type_cd AS txtTypeCd,
            value_image_txt AS valueImageTxt,
            value_txt AS valueTxt
        FROM Obs_value_txt
        WHERE observation_uid = :observation_uid
        """;

    public static final String SELECT_BY_OBS_DATE_UID = """
        SELECT
            observation_uid AS observationUid,
            obs_value_date_seq AS obsValueDateSeq,
            duration_amt AS durationAmt,
            duration_unit_cd AS durationUnitCd,
            from_time AS fromTime,
            to_time AS toTime
        FROM Obs_value_date
        WHERE observation_uid = :observation_uid
        """;

    public static final String SELECT_BY_OBS_NUMERIC_UID = """
        SELECT
            observation_uid AS observationUid,
            obs_value_numeric_seq AS obsValueNumericSeq,
            high_range AS highRange,
            low_range AS lowRange,
            comparator_cd_1 AS comparatorCd1,
            numeric_value_1 AS numericValue1,
            numeric_value_2 AS numericValue2,
            numeric_unit_cd AS numericUnitCd,
            separator_cd AS separatorCd,
            numeric_scale_1 AS numericScale1,
            numeric_scale_2 AS numericScale2
        FROM Obs_value_numeric
        WHERE observation_uid = :observation_uid
        """;
}
