package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RoleJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplateOdse;

    @InjectMocks
    private RoleJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRole() {
        Role role = new Role();
        role.setSubjectEntityUid(1L);
        role.setCode("CD");
        role.setRoleSeq(1L);
        repository.createRole(role);
        verify(jdbcTemplateOdse).update(any(), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindRolesByParentUid() {
        Role role = new Role();
        when(jdbcTemplateOdse.query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.singletonList(role));

        List<Role> result = repository.findRolesByParentUid(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testUpdateRole() {
        Role role = new Role();
        role.setSubjectEntityUid(1L);
        role.setCode("CD");
        role.setRoleSeq(1L);
        repository.updateRole(role);
        verify(jdbcTemplateOdse).update(any(), any(MapSqlParameterSource.class));
    }

    @Test
    void testFindActiveBySubjectEntityUid() {
        Role role = new Role();
        when(jdbcTemplateOdse.query(anyString(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(Collections.singletonList(role));

        List<Role> result = repository.findActiveBySubjectEntityUid(1L);
        assertEquals(1, result.size());
    }
}
