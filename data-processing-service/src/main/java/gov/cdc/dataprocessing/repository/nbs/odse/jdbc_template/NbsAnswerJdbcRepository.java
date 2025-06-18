package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsAnswerHist;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.data_field.*;
import static gov.cdc.dataprocessing.constant.query.NbsAnswerQuery.*;

@Component
public class NbsAnswerJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public NbsAnswerJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void mergeNbsAnswer(NbsAnswer answer) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(NBS_ANSWER_UID_JAVA, answer.getNbsAnswerUid());
        params.addValue(ACT_UID_JAVA, answer.getActUid());
        params.addValue("answerTxt", answer.getAnswerTxt());
        params.addValue("nbsQuestionUid", answer.getNbsQuestionUid());
        params.addValue("nbsQuestionVersionCtrlNbr", answer.getNbsQuestionVersionCtrlNbr());
        params.addValue("seqNbr", answer.getSeqNbr());
        params.addValue("answerLargeTxt", answer.getAnswerLargeTxt());
        params.addValue("answerGroupSeqNbr", answer.getAnswerGroupSeqNbr());
        params.addValue(RECORD_STATUS_CD_JAVA, answer.getRecordStatusCd());
        params.addValue(RECORD_STATUS_TIME_JAVA, answer.getRecordStatusTime());
        params.addValue(LAST_CHG_TIME_JAVA, answer.getLastChgTime());
        params.addValue(LAST_CHG_USER_ID_JAVA, answer.getLastChgUserId());

        jdbcTemplateOdse.update(MERGE_NBS_ANSWER, params);
    }

    public void mergeNbsAnswerHist(NbsAnswerHist hist) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue(NBS_ANSWER_UID_JAVA, hist.getNbsAnswerUid());
        params.addValue(ACT_UID_JAVA, hist.getActUid());
        params.addValue("answerTxt", hist.getAnswerTxt());
        params.addValue("nbsQuestionUid", hist.getNbsQuestionUid());
        params.addValue("nbsQuestionVersionCtrlNbr", hist.getNbsQuestionVersionCtrlNbr());
        params.addValue("seqNbr", hist.getSeqNbr());
        params.addValue("answerLargeTxt", hist.getAnswerLargeTxt());
        params.addValue("answerGroupSeqNbr", hist.getAnswerGroupSeqNbr());
        params.addValue(RECORD_STATUS_CD_JAVA, hist.getRecordStatusCd());
        params.addValue(RECORD_STATUS_TIME_JAVA, hist.getRecordStatusTime());
        params.addValue(LAST_CHG_TIME_JAVA, hist.getLastChgTime());
        params.addValue(LAST_CHG_USER_ID_JAVA, hist.getLastChgUserId());

        jdbcTemplateOdse.update(MERGE_NBS_ANSWER_HIST, params);
    }

    public void deleteByNbsAnswerUid(Long nbsAnswerUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(NBS_ANSWER_UID_JAVA, nbsAnswerUid);
        jdbcTemplateOdse.update(DELETE_NBS_ANSWER_BY_UID, params);
    }

    public List<NbsAnswer> findByActUid(Long uid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("uid", uid);
        return jdbcTemplateOdse.query(SELECT_NBS_ANSWER_BY_ACT_UID, params, new BeanPropertyRowMapper<>(NbsAnswer.class));
    }


}
