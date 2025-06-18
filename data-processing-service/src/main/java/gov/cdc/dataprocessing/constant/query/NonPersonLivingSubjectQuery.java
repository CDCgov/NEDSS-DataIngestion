package gov.cdc.dataprocessing.constant.query;

public class NonPersonLivingSubjectQuery {
    public static final String FIND_BY_ID = """
        SELECT
            non_person_uid AS nonPersonUid,
            add_reason_cd AS addReasonCd,
            add_time AS addTime,
            add_user_id AS addUserId,
            birth_sex_cd AS birthSexCd,
            birth_order_nbr AS birthOrderNbr,
            birth_time AS birthTime,
            breed_cd AS breedCd,
            breed_desc_txt AS breedDescTxt,
            cd AS cd,
            cd_desc_txt AS cdDescTxt,
            deceased_ind_cd AS deceasedIndCd,
            deceased_time AS deceasedTime,
            description AS description,
            last_chg_reason_cd AS lastChgReasonCd,
            last_chg_time AS lastChgTime,
            last_chg_user_id AS lastChgUserId,
            local_id AS localId,
            multiple_birth_ind AS multipleBirthInd,
            nm AS nm,
            record_status_cd AS recordStatusCd,
            record_status_time AS recordStatusTime,
            status_cd AS statusCd,
            status_time AS statusTime,
            taxonomic_classification_cd AS taxonomicClassificationCd,
            taxonomic_classification_desc AS taxonomicClassificationDesc,
            user_affiliation_txt AS userAffiliationTxt,
            version_ctrl_nbr AS versionCtrlNbr
        FROM Non_Person_living_subject
        WHERE non_person_uid = :nonPersonUid
    """;
}
