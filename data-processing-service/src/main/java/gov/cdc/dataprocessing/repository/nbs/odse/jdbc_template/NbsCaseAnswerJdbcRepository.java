package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.NbsCaseAnswerQuery.SELECT_NBS_CASE_ANSWER_BY_ACT_UID;

@Component
public class NbsCaseAnswerJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public NbsCaseAnswerJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public List<NbsCaseAnswer> getNbsCaseAnswerByActUid(Long uid) {
        MapSqlParameterSource params = new MapSqlParameterSource("uid", uid);
        return jdbcTemplateOdse.query(
                SELECT_NBS_CASE_ANSWER_BY_ACT_UID,
                params,
                new BeanPropertyRowMapper<>(NbsCaseAnswer.class)
        );
    }

}
