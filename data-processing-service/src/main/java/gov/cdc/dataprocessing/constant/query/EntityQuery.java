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

}
