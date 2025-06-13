package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityODSE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import static gov.cdc.dataprocessing.constant.query.EntityQuery.INSERT_SQL_ENTITY;
import static gov.cdc.dataprocessing.constant.query.EntityQuery.UPDATE_ENTITY_BY_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EntityJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private EntityJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateEntity_shouldCallUpdate() {
        EntityODSE entity = new EntityODSE();
        entity.setEntityUid(1L);
        entity.setClassCd("PERSON");

        repository.createEntity(entity);

        verify(jdbcTemplateOdse, times(1)).update(eq(INSERT_SQL_ENTITY), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateEntity_shouldCallUpdate() {
        EntityODSE entity = new EntityODSE();
        entity.setEntityUid(2L);
        entity.setClassCd("ORG");

        repository.updateEntity(entity);

        verify(jdbcTemplateOdse, times(1)).update(eq(UPDATE_ENTITY_BY_ID), any(MapSqlParameterSource.class));
    }
}
