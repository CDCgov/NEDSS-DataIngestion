SELECT  MSG_CASE_INVESTIGATION.INV_LOCAL_ID,
        MSG_CASE_INVESTIGATION.PAT_LOCAL_ID, MSG_CASE_INVESTIGATION.INV_AUTHOR_ID,
        MSG_CASE_INVESTIGATION.INV_CASE_STATUS_CD,
        MSG_CASE_INVESTIGATION.INV_CLOSE_DT, MSG_CASE_INVESTIGATION.INV_COMMENT_TXT,
        MSG_CASE_INVESTIGATION.INV_CONDITION_CD, MSG_CASE_INVESTIGATION.INV_CONTACT_INV_COMMENT_TXT,
        MSG_CASE_INVESTIGATION.INV_CONTACT_INV_PRIORITY_CD,
        MSG_CASE_INVESTIGATION.INV_CONTACT_INV_STATUS_CD, MSG_CASE_INVESTIGATION.INV_CURR_PROCESS_STATE_CD,
        MSG_CASE_INVESTIGATION.INV_DAYCARE_IND_CD,
        MSG_CASE_INVESTIGATION.INV_DETECTION_METHOD_CD, MSG_CASE_INVESTIGATION.INV_DIAGNOSIS_DT,
        MSG_CASE_INVESTIGATION.INV_DISEASE_ACQUIRED_LOC_CD, MSG_CASE_INVESTIGATION.INV_EFFECTIVE_TIME,
        MSG_CASE_INVESTIGATION.INV_FOODHANDLER_IND_CD, MSG_CASE_INVESTIGATION.INV_HOSPITALIZED_ADMIT_DT,
        MSG_CASE_INVESTIGATION.INV_HOSPITALIZED_DISCHARGE_DT,
        MSG_CASE_INVESTIGATION.INV_HOSPITALIZED_IND_CD, MSG_CASE_INVESTIGATION.INV_HOSP_STAY_DURATION,
        MSG_CASE_INVESTIGATION.INV_ILLNESS_START_DT,
        MSG_CASE_INVESTIGATION.INV_ILLNESS_END_DT, MSG_CASE_INVESTIGATION.INV_ILLNESS_DURATION,
        MSG_CASE_INVESTIGATION.INV_ILLNESS_DURATION_UNIT_CD,
        MSG_CASE_INVESTIGATION.INV_ILLNESS_ONSET_AGE, MSG_CASE_INVESTIGATION.INV_ILLNESS_ONSET_AGE_UNIT_CD,
        MSG_CASE_INVESTIGATION.INV_INVESTIGATOR_ASSIGNED_DT,
        MSG_CASE_INVESTIGATION.INV_IMPORT_CITY_TXT, MSG_CASE_INVESTIGATION.INV_IMPORT_COUNTY_CD,
        MSG_CASE_INVESTIGATION.INV_IMPORT_COUNTRY_CD,
        MSG_CASE_INVESTIGATION.INV_IMPORT_STATE_CD, MSG_CASE_INVESTIGATION.INV_INFECTIOUS_FROM_DT,
        MSG_CASE_INVESTIGATION.INV_INFECTIOUS_TO_DT, MSG_CASE_INVESTIGATION.INV_LEGACY_CASE_ID,
        MSG_CASE_INVESTIGATION.INV_MMWR_WEEK_TXT,
        MSG_CASE_INVESTIGATION.INV_MMWR_YEAR_TXT, MSG_CASE_INVESTIGATION.INV_OUTBREAK_IND_CD,
        MSG_CASE_INVESTIGATION.INV_OUTBREAK_NAME_CD, MSG_CASE_INVESTIGATION.INV_PATIENT_DEATH_DT,
        MSG_CASE_INVESTIGATION.INV_PATIENT_DEATH_IND_CD,
        MSG_CASE_INVESTIGATION.INV_PREGNANCY_IND_CD, MSG_CASE_INVESTIGATION.INV_REFERRAL_BASIS_CD,
        MSG_CASE_INVESTIGATION.INV_REPORT_DT,
        MSG_CASE_INVESTIGATION.INV_REPORT_TO_COUNTY_DT, MSG_CASE_INVESTIGATION.INV_REPORT_TO_STATE_DT,
        MSG_CASE_INVESTIGATION.INV_REPORTING_COUNTY_CD, MSG_CASE_INVESTIGATION.INV_SHARED_IND_CD,
        MSG_CASE_INVESTIGATION.INV_SOURCE_TYPE_CD,
        MSG_CASE_INVESTIGATION.INV_START_DT,
        MSG_CASE_INVESTIGATION.INV_STATE_ID, MSG_CASE_INVESTIGATION.INV_STATUS_CD,
        MSG_CASE_INVESTIGATION.INV_TRANSMISSION_MODE_CD
FROM MSG_CASE_INVESTIGATION
WHERE MSG_CASE_INVESTIGATION.MSG_CONTAINER_UID= :MSG_CONTAINER_UID ;