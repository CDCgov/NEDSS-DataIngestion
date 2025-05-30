package gov.cdc.dataprocessing.constant.query;

public class EntityQuery {
    public static final String INSERT_SQL_ENTITY_ID = """
INSERT INTO Entity_id (
    entity_uid, entity_id_seq, add_reason_cd, add_time, add_user_id,
    assigning_authority_cd, assigning_authority_desc_txt, duration_amt, duration_unit_cd,
    effective_from_time, effective_to_time, last_chg_reason_cd, last_chg_time, last_chg_user_id,
    record_status_cd, record_status_time, root_extension_txt, status_cd, status_time,
    type_cd, type_desc_txt, user_affiliation_txt, valid_from_time, valid_to_time, as_of_date,
    assigning_authority_id_type
) VALUES (
    :entity_uid, :entity_id_seq, :add_reason_cd, :add_time, :add_user_id,
    :assigning_authority_cd, :assigning_authority_desc_txt, :duration_amt, :duration_unit_cd,
    :effective_from_time, :effective_to_time, :last_chg_reason_cd, :last_chg_time, :last_chg_user_id,
    :record_status_cd, :record_status_time, :root_extension_txt, :status_cd, :status_time,
    :type_cd, :type_desc_txt, :user_affiliation_txt, :valid_from_time, :valid_to_time, :as_of_date,
    :assigning_authority_id_type
)
""";


    public static final String INSERT_SQL_ENTITY = """
INSERT INTO Entity (
    entity_uid, class_cd
) VALUES (
    :entity_uid, :class_cd
)
""";


    public static final String SELECT_ENTITY_ID_BY_ENTITY_ID = """
            SELECT
                entity_uid AS entityUid,
                entity_id_seq AS entityIdSeq,
                add_reason_cd AS addReasonCode,
                add_time AS addTime,
                add_user_id AS addUserId,
                assigning_authority_cd AS assigningAuthorityCode,
                assigning_authority_desc_txt AS assigningAuthorityDescription,
                duration_amt AS durationAmount,
                duration_unit_cd AS durationUnitCode,
                effective_from_time AS effectiveFromTime,
                effective_to_time AS effectiveToTime,
                last_chg_reason_cd AS lastChangeReasonCode,
                last_chg_time AS lastChangeTime,
                last_chg_user_id AS lastChangeUserId,
                record_status_cd AS recordStatusCode,
                record_status_time AS recordStatusTime,
                root_extension_txt AS rootExtensionText,
                status_cd AS statusCode,
                status_time AS statusTime,
                type_cd AS typeCode,
                type_desc_txt AS typeDescriptionText,
                user_affiliation_txt AS userAffiliationText,
                valid_from_time AS validFromTime,
                valid_to_time AS validToTime,
                as_of_date AS asOfDate,
                assigning_authority_id_type AS assigningAuthorityIdType
            FROM Entity_id
            WHERE entity_uid = :entity_uid
            
            """;

    public static final String SELECT_ENTITY_ID_BY_ENTITY_ID_ACTIVE = """
            SELECT
                entity_uid AS entityUid,
                entity_id_seq AS entityIdSeq,
                add_reason_cd AS addReasonCode,
                add_time AS addTime,
                add_user_id AS addUserId,
                assigning_authority_cd AS assigningAuthorityCode,
                assigning_authority_desc_txt AS assigningAuthorityDescription,
                duration_amt AS durationAmount,
                duration_unit_cd AS durationUnitCode,
                effective_from_time AS effectiveFromTime,
                effective_to_time AS effectiveToTime,
                last_chg_reason_cd AS lastChangeReasonCode,
                last_chg_time AS lastChangeTime,
                last_chg_user_id AS lastChangeUserId,
                record_status_cd AS recordStatusCode,
                record_status_time AS recordStatusTime,
                root_extension_txt AS rootExtensionText,
                status_cd AS statusCode,
                status_time AS statusTime,
                type_cd AS typeCode,
                type_desc_txt AS typeDescriptionText,
                user_affiliation_txt AS userAffiliationText,
                valid_from_time AS validFromTime,
                valid_to_time AS validToTime,
                as_of_date AS asOfDate,
                assigning_authority_id_type AS assigningAuthorityIdType
            FROM Entity_id
            WHERE entity_uid = :entity_uid
            AND record_status_cd = 'ACTIVE'
            """;

    public static final String UPDATE_ENTITY_BY_ID = """
    UPDATE Entity
    SET class_cd = :classCd
    WHERE entity_uid = :entityUid
    """;

    public static final String MERGE_ENTITY_ID = """
MERGE INTO Entity_id AS target
USING (VALUES (
    :entityUid, :entityIdSeq, :addReasonCode, :addTime, :addUserId, :assigningAuthorityCode,
    :assigningAuthorityDescription, :durationAmount, :durationUnitCode, :effectiveFromTime,
    :effectiveToTime, :lastChangeReasonCode, :lastChangeTime, :lastChangeUserId,
    :recordStatusCode, :recordStatusTime, :rootExtensionText, :statusCode, :statusTime,
    :typeCode, :typeDescriptionText, :userAffiliationText, :validFromTime, :validToTime,
    :asOfDate, :assigningAuthorityIdType
)) AS source (
    entity_uid, entity_id_seq, add_reason_cd, add_time, add_user_id, assigning_authority_cd,
    assigning_authority_desc_txt, duration_amt, duration_unit_cd, effective_from_time,
    effective_to_time, last_chg_reason_cd, last_chg_time, last_chg_user_id,
    record_status_cd, record_status_time, root_extension_txt, status_cd, status_time,
    type_cd, type_desc_txt, user_affiliation_txt, valid_from_time, valid_to_time,
    as_of_date, assigning_authority_id_type
)
ON target.entity_uid = source.entity_uid AND target.entity_id_seq = source.entity_id_seq
WHEN MATCHED THEN UPDATE SET
    add_reason_cd = source.add_reason_cd,
    add_time = source.add_time,
    add_user_id = source.add_user_id,
    assigning_authority_cd = source.assigning_authority_cd,
    assigning_authority_desc_txt = source.assigning_authority_desc_txt,
    duration_amt = source.duration_amt,
    duration_unit_cd = source.duration_unit_cd,
    effective_from_time = source.effective_from_time,
    effective_to_time = source.effective_to_time,
    last_chg_reason_cd = source.last_chg_reason_cd,
    last_chg_time = source.last_chg_time,
    last_chg_user_id = source.last_chg_user_id,
    record_status_cd = source.record_status_cd,
    record_status_time = source.record_status_time,
    root_extension_txt = source.root_extension_txt,
    status_cd = source.status_cd,
    status_time = source.status_time,
    type_cd = source.type_cd,
    type_desc_txt = source.type_desc_txt,
    user_affiliation_txt = source.user_affiliation_txt,
    valid_from_time = source.valid_from_time,
    valid_to_time = source.valid_to_time,
    as_of_date = source.as_of_date,
    assigning_authority_id_type = source.assigning_authority_id_type
WHEN NOT MATCHED THEN INSERT (
    entity_uid, entity_id_seq, add_reason_cd, add_time, add_user_id, assigning_authority_cd,
    assigning_authority_desc_txt, duration_amt, duration_unit_cd, effective_from_time,
    effective_to_time, last_chg_reason_cd, last_chg_time, last_chg_user_id,
    record_status_cd, record_status_time, root_extension_txt, status_cd, status_time,
    type_cd, type_desc_txt, user_affiliation_txt, valid_from_time, valid_to_time,
    as_of_date, assigning_authority_id_type
) VALUES (
    source.entity_uid, source.entity_id_seq, source.add_reason_cd, source.add_time, source.add_user_id, source.assigning_authority_cd,
    source.assigning_authority_desc_txt, source.duration_amt, source.duration_unit_cd, source.effective_from_time,
    source.effective_to_time, source.last_chg_reason_cd, source.last_chg_time, source.last_chg_user_id,
    source.record_status_cd, source.record_status_time, source.root_extension_txt, source.status_cd, source.status_time,
    source.type_cd, source.type_desc_txt, source.user_affiliation_txt, source.valid_from_time, source.valid_to_time,
    source.as_of_date, source.assigning_authority_id_type
);
""";

}
