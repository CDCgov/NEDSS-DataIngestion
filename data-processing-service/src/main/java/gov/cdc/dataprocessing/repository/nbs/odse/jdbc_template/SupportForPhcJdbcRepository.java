package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.constant.query.ClinicalDocumentQuery;
import gov.cdc.dataprocessing.constant.query.NonPersonLivingSubjectQuery;
import gov.cdc.dataprocessing.constant.query.PatientEncounterQuery;
import gov.cdc.dataprocessing.constant.query.ReferralDocQuery;
import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static gov.cdc.dataprocessing.constant.query.EntityGroupQuery.FIND_BY_UID;
import static gov.cdc.dataprocessing.constant.query.PlaceQuery.FIND_BY_ID;

@Component
public class SupportForPhcJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public SupportForPhcJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public Place findPlaceById(Long placeUid) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("placeUid", placeUid);

        List<Place> results = jdbcTemplateOdse.query(
                FIND_BY_ID,
                params,
                new BeanPropertyRowMapper<>(Place.class)
        );
        return results.isEmpty() ? null : results.getFirst();
    }

    public PatientEncounter findPatientEncounterById(Long patientEncounterUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("patientEncounterUid", patientEncounterUid);

        List<PatientEncounter> result = jdbcTemplateOdse.query(
                PatientEncounterQuery.FIND_BY_ID,
                params,
                new BeanPropertyRowMapper<>(PatientEncounter.class)
        );

        return result.isEmpty() ? null : result.getFirst();
    }

    public Referral findReferralById(Long referralUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("referralUid", referralUid);

        List<Referral> result = jdbcTemplateOdse.query(
                ReferralDocQuery.FIND_BY_ID,
                params,
                new BeanPropertyRowMapper<>(Referral.class)
        );

        return result.isEmpty() ? null : result.getFirst();
    }

    public ClinicalDocument findClinicalDocumentById(Long clinicalDocumentUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("clinicalDocumentUid", clinicalDocumentUid);

        List<ClinicalDocument> result = jdbcTemplateOdse.query(
                ClinicalDocumentQuery.FIND_BY_ID,
                params,
                new BeanPropertyRowMapper<>(ClinicalDocument.class)
        );

        return result.isEmpty() ? null : result.getFirst();
    }


    public NonPersonLivingSubject findNonPersonLivingSubjectById(Long nonPersonUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nonPersonUid", nonPersonUid);

        List<NonPersonLivingSubject> result = jdbcTemplateOdse.query(
                NonPersonLivingSubjectQuery.FIND_BY_ID,
                params,
                new BeanPropertyRowMapper<>(NonPersonLivingSubject.class)
        );

        return result.isEmpty() ? null : result.getFirst();
    }

    public EntityGroup findEntityGroupById(Long entityGroupUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("entityGroupUid", entityGroupUid);

        List<EntityGroup> results = jdbcTemplateOdse.query(
                FIND_BY_UID,
                params,
                new BeanPropertyRowMapper<>(EntityGroup.class)
        );

        return results.isEmpty() ? null : results.getFirst();
    }




}
