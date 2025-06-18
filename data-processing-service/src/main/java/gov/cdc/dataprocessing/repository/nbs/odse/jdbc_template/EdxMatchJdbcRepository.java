package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxEntityMatch;
import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxPatientMatch;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.data_field.ENTITY_UID_JAVA;
import static gov.cdc.dataprocessing.constant.query.EdxMatchQuery.MERGE_EDX_ENTITY_MATCH;
import static gov.cdc.dataprocessing.constant.query.EdxMatchQuery.MERGE_EDX_PATIENT_MATCH;

@Component
public class EdxMatchJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public EdxMatchJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void mergeEdxPatientMatch(EdxPatientMatch match) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("patientUid", match.getPatientUid())
                .addValue("matchString", match.getMatchString())
                .addValue("typeCd", match.getTypeCd())
                .addValue("matchStringHashcode", match.getMatchStringHashcode());

        jdbcTemplateOdse.update(MERGE_EDX_PATIENT_MATCH, params);
    }

    public void mergeEdxEntityMatch(EdxEntityMatch match) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(ENTITY_UID_JAVA, match.getEntityUid())
                .addValue("matchString", match.getMatchString())
                .addValue("typeCd", match.getTypeCd())
                .addValue("matchStringHashcode", match.getMatchStringHashcode());

        jdbcTemplateOdse.update(MERGE_EDX_ENTITY_MATCH, params);
    }

}
