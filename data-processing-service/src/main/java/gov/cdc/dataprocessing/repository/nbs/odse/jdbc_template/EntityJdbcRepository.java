package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.query.EntityQuery.INSERT_SQL_ENTITY;

@Component
public class EntityJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public EntityJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public void createEntity(EntityODSE entity) {
        jdbcTemplateOdse.update(INSERT_SQL_ENTITY, new MapSqlParameterSource()
                .addValue("entity_uid", entity.getEntityUid())
                .addValue("class_cd", entity.getClassCd())
        );
    }
}
