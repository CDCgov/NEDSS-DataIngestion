package gov.cdc.dataprocessing.constant.query;

public class PatientEncounterQuery {
    public static final String FIND_BY_ID = """
            SELECT
                patient_encounter_uid AS patientEncounterUid,
                activity_duration_amt AS activityDurationAmt,
                activity_duration_unit_cd AS activityDurationUnitCd,
                activity_from_time AS activityFromTime,
                activity_to_time AS activityToTime,
                acuity_level_cd AS acuityLevelCd,
                acuity_level_desc_txt AS acuityLevelDescTxt,
                add_reason_cd AS addReasonCd,
                add_time AS addTime,
                add_user_id AS addUserId,
                admission_source_cd AS admissionSourceCd,
                admission_source_desc_txt AS admissionSourceDescTxt,
                birth_encounter_ind AS birthEncounterInd,
                cd AS cd,
                cd_desc_txt AS cdDescTxt,
                confidentiality_cd AS confidentialityCd,
                confidentiality_desc_txt AS confidentialityDescTxt,
                effective_duration_amt AS effectiveDurationAmt,
                effective_duration_unit_cd AS effectiveDurationUnitCd,
                effective_from_time AS effectiveFromTime,
                effective_to_time AS effectiveToTime,
                last_chg_reason_cd AS lastChgReasonCd,
                last_chg_time AS lastChgTime,
                last_chg_user_id AS lastChgUserId,
                local_id AS localId,
                priority_cd AS priorityCd,
                priority_desc_txt AS priorityDescTxt,
                record_status_cd AS recordStatusCd,
                record_status_time AS recordStatusTime,
                referral_source_cd AS referralSourceCd,
                referral_source_desc_txt AS referralSourceDescTxt,
                repeat_nbr AS repeatNbr,
                status_cd AS statusCd,
                status_time AS statusTime,
                txt AS txt,
                user_affiliation_txt AS userAffiliationTxt,
                program_jurisdiction_oid AS programJurisdictionOid,
                shared_ind AS sharedInd,
                version_ctrl_nbr AS versionCtrlNbr
            FROM Patient_encounter
            WHERE patient_encounter_uid = :patientEncounterUid
            """;
}
