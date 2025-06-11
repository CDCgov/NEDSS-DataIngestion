package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.constant.query.ClinicalDocumentQuery;
import gov.cdc.dataprocessing.constant.query.NonPersonLivingSubjectQuery;
import gov.cdc.dataprocessing.constant.query.PatientEncounterQuery;
import gov.cdc.dataprocessing.constant.query.ReferralDocQuery;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class SupportForPhcJdbcRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private SupportForPhcJdbcRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new SupportForPhcJdbcRepository(jdbcTemplate);
    }

    @Test
    void testFindPlaceById() {
        Place place = new Place();
        when(jdbcTemplate.query(any(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(place));

        Place result = repository.findPlaceById(1L);
        assertNotNull(result);
    }

    @Test
    void testFindPatientEncounterById() {
        PatientEncounter pe = new PatientEncounter();
        when(jdbcTemplate.query(eq(PatientEncounterQuery.FIND_BY_ID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(pe));

        PatientEncounter result = repository.findPatientEncounterById(1L);
        assertNotNull(result);
    }

    @Test
    void testFindReferralById() {
        Referral referral = new Referral();
        when(jdbcTemplate.query(eq(ReferralDocQuery.FIND_BY_ID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(referral));

        Referral result = repository.findReferralById(1L);
        assertNotNull(result);
    }

    @Test
    void testFindClinicalDocumentById() {
        ClinicalDocument doc = new ClinicalDocument();
        when(jdbcTemplate.query(eq(ClinicalDocumentQuery.FIND_BY_ID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(doc));

        ClinicalDocument result = repository.findClinicalDocumentById(1L);
        assertNotNull(result);
    }

    @Test
    void testFindNonPersonLivingSubjectById() {
        NonPersonLivingSubject subject = new NonPersonLivingSubject();
        when(jdbcTemplate.query(eq(NonPersonLivingSubjectQuery.FIND_BY_ID), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(subject));

        NonPersonLivingSubject result = repository.findNonPersonLivingSubjectById(1L);
        assertNotNull(result);
    }

    @Test
    void testFindEntityGroupById() {
        EntityGroup group = new EntityGroup();
        when(jdbcTemplate.query(any(), any(MapSqlParameterSource.class), any(BeanPropertyRowMapper.class)))
                .thenReturn(List.of(group));

        EntityGroup result = repository.findEntityGroupById(1L);
        assertNotNull(result);
    }
}
