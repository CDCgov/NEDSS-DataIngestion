package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.ConfirmationMethod;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.ConfirmationMethodQuery.MERGE_CONFIRMATION_METHOD;
import static gov.cdc.dataprocessing.constant.query.ConfirmationMethodQuery.SELECT_CONFIRMATION_METHOD_BY_UID;

@Component
public class ConfirmationMethodJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public ConfirmationMethodJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void upsertConfirmationMethod(ConfirmationMethod entity) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("publicHealthCaseUid", entity.getPublicHealthCaseUid())
                .addValue("confirmationMethodCd", entity.getConfirmationMethodCd())
                .addValue("confirmationMethodDescTxt", entity.getConfirmationMethodDescTxt())
                .addValue("confirmationMethodTime", entity.getConfirmationMethodTime());

        jdbcTemplateOdse.update(MERGE_CONFIRMATION_METHOD, params);
    }

    public List<ConfirmationMethod> findByPublicHealthCaseUid(Long uid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("uid", uid);

        return jdbcTemplateOdse.query(
                SELECT_CONFIRMATION_METHOD_BY_UID,
                params,
                new BeanPropertyRowMapper<>(ConfirmationMethod.class)
        );
    }





}
