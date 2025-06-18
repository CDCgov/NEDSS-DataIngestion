package gov.cdc.dataprocessing.constant.query;

public class NbsActQuery {
    public static final String SELECT_NBS_ACT_ENTITIES_BY_ACT_UID = """
SELECT
    nbs_act_entity_uid       AS nbsActEntityUid,
    act_uid                  AS actUid,
    add_time                 AS addTime,
    add_user_id              AS addUserId,
    entity_uid               AS entityUid,
    entity_version_ctrl_nbr  AS entityVersionCtrlNbr,
    last_chg_time            AS lastChgTime,
    last_chg_user_id         AS lastChgUserId,
    record_status_cd         AS recordStatusCd,
    record_status_time       AS recordStatusTime,
    type_cd                  AS typeCd
FROM NBS_act_entity
WHERE act_uid = :uid
""";

    public static final String MERGE_NBS_ACT_ENTITY = """
MERGE INTO nbs_act_entity AS target
USING (SELECT 
           :actUid AS act_uid,
           :addTime AS add_time,
           :addUserId AS add_user_id,
           :entityUid AS entity_uid,
           :entityVersionCtrlNbr AS entity_version_ctrl_nbr,
           :lastChgTime AS last_chg_time,
           :lastChgUserId AS last_chg_user_id,
           :recordStatusCd AS record_status_cd,
           :recordStatusTime AS record_status_time,
           :typeCd AS type_cd
       ) AS source
ON target.act_uid = source.act_uid 
   AND target.entity_uid = source.entity_uid 
   AND target.type_cd = source.type_cd
WHEN MATCHED THEN UPDATE SET
    add_time = source.add_time,
    add_user_id = source.add_user_id,
    entity_version_ctrl_nbr = source.entity_version_ctrl_nbr,
    last_chg_time = source.last_chg_time,
    last_chg_user_id = source.last_chg_user_id,
    record_status_cd = source.record_status_cd,
    record_status_time = source.record_status_time
WHEN NOT MATCHED THEN INSERT (
    act_uid,
    add_time,
    add_user_id,
    entity_uid,
    entity_version_ctrl_nbr,
    last_chg_time,
    last_chg_user_id,
    record_status_cd,
    record_status_time,
    type_cd
) VALUES (
    source.act_uid,
    source.add_time,
    source.add_user_id,
    source.entity_uid,
    source.entity_version_ctrl_nbr,
    source.last_chg_time,
    source.last_chg_user_id,
    source.record_status_cd,
    source.record_status_time,
    source.type_cd
);
""";


    public static final String DELETE_NBS_ACT_ENTITY_BY_UID = """
DELETE FROM NBS_act_entity
WHERE nbs_act_entity_uid = :nbsActEntityUid
""";


    public static final String MERGE_NBS_ACT_ENTITY_HIST = """
MERGE INTO nbs_act_entity_hist AS target
USING (SELECT 
           :nbsActEntityUid AS nbs_act_entity_uid,
           :actUid AS act_uid,
           :addTime AS add_time,
           :addUserId AS add_user_id,
           :entityUid AS entity_uid,
           :entityVersionCtrlNbr AS entity_version_ctrl_nbr,
           :lastChgTime AS last_chg_time,
           :lastChgUserId AS last_chg_user_id,
           :recordStatusCd AS record_status_cd,
           :recordStatusTime AS record_status_time,
           :typeCd AS type_cd
       ) AS source
ON target.nbs_act_entity_hist_uid = source.nbs_act_entity_uid
WHEN MATCHED THEN UPDATE SET
    act_uid = source.act_uid,
    add_time = source.add_time,
    add_user_id = source.add_user_id,
    entity_uid = source.entity_uid,
    entity_version_ctrl_nbr = source.entity_version_ctrl_nbr,
    last_chg_time = source.last_chg_time,
    last_chg_user_id = source.last_chg_user_id,
    record_status_cd = source.record_status_cd,
    record_status_time = source.record_status_time,
    type_cd = source.type_cd
WHEN NOT MATCHED THEN INSERT (
    nbs_act_entity_uid,
    act_uid,
    add_time,
    add_user_id,
    entity_uid,
    entity_version_ctrl_nbr,
    last_chg_time,
    last_chg_user_id,
    record_status_cd,
    record_status_time,
    type_cd
) VALUES (
    source.nbs_act_entity_uid,
    source.act_uid,
    source.add_time,
    source.add_user_id,
    source.entity_uid,
    source.entity_version_ctrl_nbr,
    source.last_chg_time,
    source.last_chg_user_id,
    source.record_status_cd,
    source.record_status_time,
    source.type_cd
);
""";
}
