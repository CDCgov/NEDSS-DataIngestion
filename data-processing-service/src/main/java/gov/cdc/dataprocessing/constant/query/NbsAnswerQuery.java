package gov.cdc.dataprocessing.constant.query;

public class NbsAnswerQuery {
    public static final String MERGE_NBS_ANSWER = """
MERGE INTO nbs_answer AS target
USING (SELECT 
           :nbsAnswerUid AS nbs_answer_uid,
           :actUid AS act_uid,
           :answerTxt AS answer_txt,
           :nbsQuestionUid AS nbs_question_uid,
           :nbsQuestionVersionCtrlNbr AS nbs_question_version_ctrl_nbr,
           :seqNbr AS seq_nbr,
           :answerLargeTxt AS answer_large_txt,
           :answerGroupSeqNbr AS answer_group_seq_nbr,
           :recordStatusCd AS record_status_cd,
           :recordStatusTime AS record_status_time,
           :lastChgTime AS last_chg_time,
           :lastChgUserId AS last_chg_user_id
       ) AS source
ON target.nbs_answer_uid = source.nbs_answer_uid
WHEN MATCHED THEN UPDATE SET
    act_uid = source.act_uid,
    answer_txt = source.answer_txt,
    nbs_question_uid = source.nbs_question_uid,
    nbs_question_version_ctrl_nbr = source.nbs_question_version_ctrl_nbr,
    seq_nbr = source.seq_nbr,
    answer_large_txt = source.answer_large_txt,
    answer_group_seq_nbr = source.answer_group_seq_nbr,
    record_status_cd = source.record_status_cd,
    record_status_time = source.record_status_time,
    last_chg_time = source.last_chg_time,
    last_chg_user_id = source.last_chg_user_id
WHEN NOT MATCHED THEN INSERT (
    nbs_answer_uid,
    act_uid,
    answer_txt,
    nbs_question_uid,
    nbs_question_version_ctrl_nbr,
    seq_nbr,
    answer_large_txt,
    answer_group_seq_nbr,
    record_status_cd,
    record_status_time,
    last_chg_time,
    last_chg_user_id
) VALUES (
    source.nbs_answer_uid,
    source.act_uid,
    source.answer_txt,
    source.nbs_question_uid,
    source.nbs_question_version_ctrl_nbr,
    source.seq_nbr,
    source.answer_large_txt,
    source.answer_group_seq_nbr,
    source.record_status_cd,
    source.record_status_time,
    source.last_chg_time,
    source.last_chg_user_id
);
""";


    public static final String SELECT_NBS_ANSWER_BY_ACT_UID = """
SELECT
    nbs_answer_uid AS nbsAnswerUid,
    act_uid AS actUid,
    answer_txt AS answerTxt,
    nbs_question_uid AS nbsQuestionUid,
    nbs_question_version_ctrl_nbr AS nbsQuestionVersionCtrlNbr,
    seq_nbr AS seqNbr,
    answer_large_txt AS answerLargeTxt,
    answer_group_seq_nbr AS answerGroupSeqNbr,
    record_status_cd AS recordStatusCd,
    record_status_time AS recordStatusTime,
    last_chg_time AS lastChgTime,
    last_chg_user_id AS lastChgUserId
FROM nbs_answer
WHERE act_uid = :uid
""";

    public static final String DELETE_NBS_ANSWER_BY_UID = """
DELETE FROM nbs_answer
WHERE nbs_answer_uid = :nbsAnswerUid
""";

    public static final String MERGE_NBS_ANSWER_HIST = """
MERGE INTO nbs_answer_hist AS target
USING (SELECT 
           :nbsAnswerUid AS nbs_answer_uid,
           :actUid AS act_uid,
           :answerTxt AS answer_txt,
           :nbsQuestionUid AS nbs_question_uid,
           :nbsQuestionVersionCtrlNbr AS nbs_question_version_ctrl_nbr,
           :seqNbr AS seq_nbr,
           :answerLargeTxt AS answer_large_txt,
           :answerGroupSeqNbr AS answer_group_seq_nbr,
           :recordStatusCd AS record_status_cd,
           :recordStatusTime AS record_status_time,
           :lastChgTime AS last_chg_time,
           :lastChgUserId AS last_chg_user_id
       ) AS source
ON target.nbs_answer_uid = source.nbs_answer_uid
WHEN MATCHED THEN UPDATE SET
    act_uid = source.act_uid,
    answer_txt = source.answer_txt,
    nbs_question_uid = source.nbs_question_uid,
    nbs_question_version_ctrl_nbr = source.nbs_question_version_ctrl_nbr,
    seq_nbr = source.seq_nbr,
    answer_large_txt = source.answer_large_txt,
    answer_group_seq_nbr = source.answer_group_seq_nbr,
    record_status_cd = source.record_status_cd,
    record_status_time = source.record_status_time,
    last_chg_time = source.last_chg_time,
    last_chg_user_id = source.last_chg_user_id
WHEN NOT MATCHED THEN INSERT (
    nbs_answer_uid,
    act_uid,
    answer_txt,
    nbs_question_uid,
    nbs_question_version_ctrl_nbr,
    seq_nbr,
    answer_large_txt,
    answer_group_seq_nbr,
    record_status_cd,
    record_status_time,
    last_chg_time,
    last_chg_user_id
) VALUES (
    source.nbs_answer_uid,
    source.act_uid,
    source.answer_txt,
    source.nbs_question_uid,
    source.nbs_question_version_ctrl_nbr,
    source.seq_nbr,
    source.answer_large_txt,
    source.answer_group_seq_nbr,
    source.record_status_cd,
    source.record_status_time,
    source.last_chg_time,
    source.last_chg_user_id
);
""";

}
