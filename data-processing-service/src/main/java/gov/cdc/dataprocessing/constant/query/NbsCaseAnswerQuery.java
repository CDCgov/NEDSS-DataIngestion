package gov.cdc.dataprocessing.constant.query;

public class NbsCaseAnswerQuery {
    public static final String SELECT_NBS_CASE_ANSWER_BY_ACT_UID = """
SELECT
    nbs_case_answer_uid             AS id,
    act_uid                         AS actUid,
    add_time                        AS addTime,
    add_user_id                     AS addUserId,
    answer_txt                      AS answerTxt,
    nbs_question_uid                AS nbsQuestionUid,
    nbs_question_version_ctrl_nbr   AS nbsQuestionVersionCtrlNbr,
    last_chg_time                   AS lastChgTime,
    last_chg_user_id                AS lastChgUserId,
    record_status_cd                AS recordStatusCd,
    record_status_time              AS recordStatusTime,
    seq_nbr                         AS seqNbr,
    answer_large_txt                AS answerLargeTxt,
    nbs_table_metadata_uid          AS nbsTableMetadataUid,
    nbs_ui_metadata_ver_ctrl_nbr    AS nbsUiMetadataVerCtrlNbr,
    answer_group_seq_nbr            AS answerGroupSeqNbr
FROM NBS_case_answer
WHERE act_uid = :uid
""";

}
