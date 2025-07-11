package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.data_field.ENTITY_UID_DB;
import static gov.cdc.dataprocessing.constant.data_field.ENTITY_UID_JAVA;
import static gov.cdc.dataprocessing.constant.query.EntityQuery.INSERT_SQL_ENTITY;
import static gov.cdc.dataprocessing.constant.query.EntityQuery.UPDATE_ENTITY_BY_ID;

@Component
public class EntityJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public EntityJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void createEntity(EntityODSE entity) {
        jdbcTemplateOdse.update(INSERT_SQL_ENTITY, new MapSqlParameterSource()
                .addValue(ENTITY_UID_DB, entity.getEntityUid())
                .addValue("class_cd", entity.getClassCd())
        );
    }

    public void updateEntity(EntityODSE entity) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(ENTITY_UID_JAVA, entity.getEntityUid())
                .addValue("classCd", entity.getClassCd());

        jdbcTemplateOdse.update(UPDATE_ENTITY_BY_ID, params);
    }
}
