package gov.cdc.dataprocessing.utilities.component.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DataModifierReposJdbcTest {

    private JdbcTemplate jdbcTemplate;
    private DataModifierReposJdbc dataModifierReposJdbc;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        dataModifierReposJdbc = new DataModifierReposJdbc(jdbcTemplate);
    }

    @Test
    void testUpdatePersonNameStatus() {
        dataModifierReposJdbc.updatePersonNameStatus(123L, 1);
        verify(jdbcTemplate).update(eq("UPDATE Person_name SET status_cd = 'I' WHERE person_uid = ? AND person_name_seq = ?"), eq(123L), eq(1));
    }

    @Test
    void testDeleteEntityIdAndSeq() {
        dataModifierReposJdbc.deleteEntityIdAndSeq(111L, 2);
        verify(jdbcTemplate).update(eq("DELETE FROM Entity_id WHERE entity_uid = ? AND entity_id_seq = ?"), eq(111L), eq(2));
    }

    @Test
    void testDeleteByPatientUidAndMatchStringNotLike() {
        dataModifierReposJdbc.deleteByPatientUidAndMatchStringNotLike(222L);
        verify(jdbcTemplate).update(eq("DELETE FROM EDX_patient_match WHERE Patient_uid = ? AND match_string NOT LIKE 'LR^%'"), eq(222L));
    }

    @Test
    void testDeletePersonRaceByUidAndCode() {
        dataModifierReposJdbc.deletePersonRaceByUidAndCode(333L, "A1");
        verify(jdbcTemplate).update(eq("DELETE FROM Person_race WHERE person_uid = ? AND race_cd = ?"), eq(333L), eq("A1"));
    }

    @Test
    void testDeletePersonRaceByUid() {
        List<String> raceCodes = List.of("A", "B", "C");
        dataModifierReposJdbc.deletePersonRaceByUid(444L, raceCodes);

        ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
        verify(jdbcTemplate).update(startsWith("DELETE FROM Person_race WHERE person_uid = ? AND race_cd NOT IN ("), captor.capture());

        Object[] actualParams = captor.getValue();
        Object[] expectedParams = new Object[]{"444", "A", "B", "C"};
        assertArrayEquals(new Object[]{444L, "A", "B", "C"}, actualParams);
    }

    @Test
    void testUpdateExistingPersonEdxIndByUid() {
        when(jdbcTemplate.update(anyString(), any(Long.class))).thenReturn(1);
        int result = dataModifierReposJdbc.updateExistingPersonEdxIndByUid(555L);
        verify(jdbcTemplate).update(eq("UPDATE Person SET edx_ind = 'Y' WHERE person_uid = ?"), eq(555L));
        assert result == 1;
    }

    @Test
    void testDeletePostalLocatorById() {
        dataModifierReposJdbc.deletePostalLocatorById(666L);
        verify(jdbcTemplate).update(eq("DELETE FROM Postal_locator WHERE postal_locator_uid = ?"), eq(666L));
    }

    @Test
    void testDeleteActRelationshipByPk() {
        dataModifierReposJdbc.deleteActRelationshipByPk(777L, 888L, "T1");
        verify(jdbcTemplate).update(eq("DELETE FROM Act_relationship WHERE target_act_uid = ? AND source_act_uid = ? AND type_cd = ?"),
                eq(777L), eq(888L), eq("T1"));
    }

    @Test
    void testDeleteLocatorById() {
        dataModifierReposJdbc.deleteLocatorById(999L, 1000L);
        verify(jdbcTemplate).update(eq("DELETE FROM Entity_locator_participation WHERE entity_uid = ? AND locator_uid = ?"),
                eq(999L), eq(1000L));
    }

    @Test
    void testDeleteParticipationByPk() {
        dataModifierReposJdbc.deleteParticipationByPk(2000L, 3000L, "X1");
        verify(jdbcTemplate).update(eq("DELETE FROM Participation WHERE subject_entity_uid = ? AND act_uid = ? AND type_cd = ?"),
                eq(2000L), eq(3000L), eq("X1"));
    }

    @Test
    void testDeleteRoleByPk() {
        dataModifierReposJdbc.deleteRoleByPk(4000L, "Y2", 5000L);
        verify(jdbcTemplate).update(eq("DELETE FROM Role WHERE subject_entity_uid = ? AND cd = ? AND role_seq = ?"),
                eq(4000L), eq("Y2"), eq(5000L));
    }
}
