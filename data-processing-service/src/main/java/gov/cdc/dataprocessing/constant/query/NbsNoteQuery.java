package gov.cdc.dataprocessing.constant.query;

public class NbsNoteQuery {
    public static final String MERGE_NBS_NOTE = """
MERGE INTO NBS_note AS target
USING (VALUES (
    :nbs_note_uid, :note_parent_uid, :record_status_cd, :record_status_time,
    :add_time, :add_user_id, :last_chg_time, :last_chg_user_id,
    :note, :private_ind_cd, :type_cd
)) AS source (
    nbs_note_uid, note_parent_uid, record_status_cd, record_status_time,
    add_time, add_user_id, last_chg_time, last_chg_user_id,
    note, private_ind_cd, type_cd
)
ON target.nbs_note_uid = source.nbs_note_uid
WHEN MATCHED THEN UPDATE SET
    note_parent_uid = source.note_parent_uid,
    record_status_cd = source.record_status_cd,
    record_status_time = source.record_status_time,
    add_time = source.add_time,
    add_user_id = source.add_user_id,
    last_chg_time = source.last_chg_time,
    last_chg_user_id = source.last_chg_user_id,
    note = source.note,
    private_ind_cd = source.private_ind_cd,
    type_cd = source.type_cd
WHEN NOT MATCHED THEN INSERT (
    nbs_note_uid, note_parent_uid, record_status_cd, record_status_time,
    add_time, add_user_id, last_chg_time, last_chg_user_id,
    note, private_ind_cd, type_cd
) VALUES (
    source.nbs_note_uid, source.note_parent_uid, source.record_status_cd, source.record_status_time,
    source.add_time, source.add_user_id, source.last_chg_time, source.last_chg_user_id,
    source.note, source.private_ind_cd, source.type_cd
);
""";

}
