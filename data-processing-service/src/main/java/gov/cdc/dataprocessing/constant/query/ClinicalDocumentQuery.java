package gov.cdc.dataprocessing.constant.query;

public class ClinicalDocumentQuery {
    public static final String FIND_BY_ID = """
        SELECT
            clinical_document_uid AS clinicalDocumentUid,
            activity_duration_amt AS activityDurationAmt,
            activity_duration_unit_cd AS activityDurationUnitCd,
            activity_from_time AS activityFromTime,
            activity_to_time AS activityToTime,
            add_reason_cd AS addReasonCd,
            add_time AS addTime,
            add_user_id AS addUserId,
            cd AS cd,
            cd_desc_txt AS cdDescTxt,
            confidentiality_cd AS confidentialityCd,
            confidentiality_desc_txt AS confidentialityDescTxt,
            copy_from_time AS copyFromTime,
            copy_to_time AS copyToTime,
            effective_duration_amt AS effectiveDurationAmt,
            effective_duration_unit_cd AS effectiveDurationUnitCd,
            effective_from_time AS effectiveFromTime,
            effective_to_time AS effectiveToTime,
            last_chg_reason_cd AS lastChgReasonCd,
            last_chg_time AS lastChgTime,
            last_chg_user_id AS lastChgUserId,
            local_id AS localId,
            practice_setting_cd AS practiceSettingCd,
            practice_setting_desc_txt AS practiceSettingDescTxt,
            record_status_cd AS recordStatusCd,
            record_status_time AS recordStatusTime,
            status_cd AS statusCd,
            status_time AS statusTime,
            txt AS txt,
            user_affiliation_txt AS userAffiliationTxt,
            version_nbr AS versionNbr,
            program_jurisdiction_oid AS programJurisdictionOid,
            shared_ind AS sharedInd,
            version_ctrl_nbr AS versionCtrlNbr
        FROM Clinical_document
        WHERE clinical_document_uid = :clinicalDocumentUid
    """;
}
