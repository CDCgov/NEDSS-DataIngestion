package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PhysicalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.EntityLocatorQuery.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EntityLocatorJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private EntityLocatorJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePhysicalLocator_shouldCallUpdate() {
        PhysicalLocator locator = new PhysicalLocator();
        locator.setPhysicalLocatorUid(1L);
        repository.createPhysicalLocator(locator);
        verify(jdbcTemplateOdse).update(eq(INSERT_SQL_PHYSICAL_LOCATOR), any(MapSqlParameterSource.class));
    }

    @Test
    void testCreatePostalLocator_shouldCallUpdate() {
        PostalLocator locator = new PostalLocator();
        locator.setPostalLocatorUid(1L);
        repository.createPostalLocator(locator);
        verify(jdbcTemplateOdse).update(eq(INSERT_SQL_POSTAL_LOCATOR), any(MapSqlParameterSource.class));
    }

    @Test
    void testCreateTeleLocator_shouldCallUpdate() {
        TeleLocator locator = new TeleLocator();
        locator.setTeleLocatorUid(1L);
        repository.createTeleLocator(locator);
        verify(jdbcTemplateOdse).update(eq(INSERT_SQL_TELE_LOCATOR), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindEntityLocatorParticipations_shouldReturnList() {
        EntityLocatorParticipation resultObj = new EntityLocatorParticipation();
        when(jdbcTemplateOdse.query(eq(SELECT_ENTITY_LOCATOR_PARTICIPATIONS_BY_ENTITY_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(resultObj));

        List<EntityLocatorParticipation> result = repository.findEntityLocatorParticipations(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByEntityUid_shouldReturnList() {
        EntityLocatorParticipation resultObj = new EntityLocatorParticipation();
        when(jdbcTemplateOdse.query(eq(SELECT_ENTITY_LOCATOR_BY_ENTITY_UID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(resultObj));

        List<EntityLocatorParticipation> result = repository.findByEntityUid(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByPostalLocatorUids_shouldReturnList() {
        PostalLocator resultObj = new PostalLocator();
        when(jdbcTemplateOdse.query(eq(SELECT_POSTAL_LOCATOR_BY_UIDS), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(resultObj));

        List<PostalLocator> result = repository.findByPostalLocatorUids(List.of(1L));
        assertEquals(1, result.size());
    }

    @Test
    void testFindByPhysicalLocatorUids_shouldReturnList() {
        PhysicalLocator resultObj = new PhysicalLocator();
        when(jdbcTemplateOdse.query(eq(SELECT_PHYSICAL_LOCATOR_BY_UIDS), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(resultObj));

        List<PhysicalLocator> result = repository.findByPhysicalLocatorUids(List.of(1L));
        assertEquals(1, result.size());
    }

    @Test
    void testFindByTeleLocatorUids_shouldReturnList() {
        TeleLocator resultObj = new TeleLocator();
        when(jdbcTemplateOdse.query(eq(SELECT_TELE_LOCATOR_BY_UIDS), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(resultObj));

        List<TeleLocator> result = repository.findByTeleLocatorUids(List.of(1L));
        assertEquals(1, result.size());
    }


    @Test
    void testUpdateTeleLocator() {
        TeleLocator teleLocator = new TeleLocator();
        when(jdbcTemplateOdse.update(any(String.class), any(MapSqlParameterSource.class))).thenReturn(1);
        repository.updateTeleLocator(teleLocator);
        verify(jdbcTemplateOdse).update(any(String.class), any(MapSqlParameterSource.class));
    }

    @Test
    void testCreateEntityLocatorParticipation() {
        EntityLocatorParticipation entityLocatorParticipation = new EntityLocatorParticipation();
        when(jdbcTemplateOdse.update(any(String.class), any(MapSqlParameterSource.class))).thenReturn(1);
        repository.createEntityLocatorParticipation(entityLocatorParticipation);
        verify(jdbcTemplateOdse).update(any(String.class), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdateEntityLocatorParticipation() {
        EntityLocatorParticipation entity = new EntityLocatorParticipation();
        when(jdbcTemplateOdse.update(any(String.class), any(MapSqlParameterSource.class))).thenReturn(1);
        repository.updateEntityLocatorParticipation(entity);
        verify(jdbcTemplateOdse).update(any(String.class), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdatePostalLocator() {
        PostalLocator postalLocator = new PostalLocator();
        when(jdbcTemplateOdse.update(any(String.class), any(MapSqlParameterSource.class))).thenReturn(1);
        repository.updatePostalLocator(postalLocator);
        verify(jdbcTemplateOdse).update(any(String.class), any(MapSqlParameterSource.class));
    }

    @Test
    void testUpdatePhysicalLocator() {
        PhysicalLocator physicalLocator = new PhysicalLocator();
        when(jdbcTemplateOdse.update(any(String.class), any(MapSqlParameterSource.class))).thenReturn(1);
        repository.updatePhysicalLocator(physicalLocator);
        verify(jdbcTemplateOdse).update(any(String.class), any(MapSqlParameterSource.class));
    }
}