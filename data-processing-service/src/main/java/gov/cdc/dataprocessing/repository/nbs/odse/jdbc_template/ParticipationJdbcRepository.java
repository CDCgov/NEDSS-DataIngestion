package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.ParticipationQuery.*;

@Component
public class ParticipationJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public ParticipationJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public List<Participation> findByActUid(Long actUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("act_uid", actUid);

        return jdbcTemplateOdse.query(
                SELECT_PARTICIPATION_BY_ACT_UID,
                params,
                new BeanPropertyRowMapper<>(Participation.class)
        );
    }

    public List<Participation> selectParticipationBySubjectEntityUid(Long subjectEntityUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("subjectEntityUid", subjectEntityUid);

        return jdbcTemplateOdse.query(SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID, params, new BeanPropertyRowMapper<>(Participation.class));
    }

    public List<Participation> selectParticipationBySubjectAndActUid(Long subjectEntityUid, Long actUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("subjectEntityUid", subjectEntityUid)
                .addValue("actUid", actUid);

        return jdbcTemplateOdse.query(SELECT_PARTICIPATION_BY_SUBJECT_ENTITY_ID_AND_ACT_UID, params, new BeanPropertyRowMapper<>(Participation.class));
    }
}
