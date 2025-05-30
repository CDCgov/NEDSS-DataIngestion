package gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.CaseManagement;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import static gov.cdc.dataprocessing.constant.query.CaseManagementQuery.MERGE_CASE_MANAGEMENT;
import static gov.cdc.dataprocessing.constant.query.CaseManagementQuery.SELECT_CASE_MANAGEMENT_BY_PH_CASE_UID;

@Component
public class CaseManagementJdbcRepository {
    private final NamedParameterJdbcTemplate jdbcTemplateOdse;

    public CaseManagementJdbcRepository(@Qualifier("odseNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplateOdse) {
        this.jdbcTemplateOdse = jdbcTemplateOdse;
    }

    public CaseManagement findByPublicHealthCaseUid(Long publicHealthCaseUid) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("publicHealthCaseUid", publicHealthCaseUid);

        try {
            return jdbcTemplateOdse.queryForObject(
                    SELECT_CASE_MANAGEMENT_BY_PH_CASE_UID,
                    params,
                    new BeanPropertyRowMapper<>(CaseManagement.class)
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }



    }

    public void mergeCaseManagement(CaseManagement caseManagement) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("caseManagementUid", caseManagement.getCaseManagementUid());
        params.addValue("publicHealthCaseUid", caseManagement.getPublicHealthCaseUid());
        params.addValue("status900", caseManagement.getStatus900());
        params.addValue("eharsId", caseManagement.getEharsId());
        params.addValue("epiLinkId", caseManagement.getEpiLinkId());
        params.addValue("fieldFollUpOojOutcome", caseManagement.getFieldFollUpOojOutcome());
        params.addValue("fieldRecordNumber", caseManagement.getFieldRecordNumber());
        params.addValue("fldFollUpDispo", caseManagement.getFldFollUpDispo());
        params.addValue("fldFollUpDispoDate", caseManagement.getFldFollUpDispoDate());
        params.addValue("fldFollUpExamDate", caseManagement.getFldFollUpExamDate());
        params.addValue("fldFollUpExpectedDate", caseManagement.getFldFollUpExpectedDate());
        params.addValue("fldFollUpExpectedIn", caseManagement.getFldFollUpExpectedIn());
        params.addValue("fldFollUpInternetOutcome", caseManagement.getFldFollUpInternetOutcome());
        params.addValue("fldFollUpNotificationPlan", caseManagement.getFldFollUpNotificationPlan());
        params.addValue("fldFollUpProvDiagnosis", caseManagement.getFldFollUpProvDiagnosis());
        params.addValue("fldFollUpProvExmReason", caseManagement.getFldFollUpProvExmReason());
        params.addValue("initFollUp", caseManagement.getInitFollUp());
        params.addValue("initFollUpClinicCode", caseManagement.getInitFollUpClinicCode());
        params.addValue("initFollUpClosedDate", caseManagement.getInitFollUpClosedDate());
        params.addValue("initFollUpNotifiable", caseManagement.getInitFollUpNotifiable());
        params.addValue("internetFollUp", caseManagement.getInternetFollUp());
        params.addValue("oojAgency", caseManagement.getOojAgency());
        params.addValue("oojDueDate", caseManagement.getOojDueDate());
        params.addValue("oojNumber", caseManagement.getOojNumber());
        params.addValue("patIntvStatusCd", caseManagement.getPatIntvStatusCd());
        params.addValue("subjComplexion", caseManagement.getSubjComplexion());
        params.addValue("subjHair", caseManagement.getSubjHair());
        params.addValue("subjHeight", caseManagement.getSubjHeight());
        params.addValue("subjOthIdntfyngInfo", caseManagement.getSubjOthIdntfyngInfo());
        params.addValue("subjSizeBuild", caseManagement.getSubjSizeBuild());
        params.addValue("survClosedDate", caseManagement.getSurvClosedDate());
        params.addValue("survPatientFollUp", caseManagement.getSurvPatientFollUp());
        params.addValue("survProvDiagnosis", caseManagement.getSurvProvDiagnosis());
        params.addValue("survProvExmReason", caseManagement.getSurvProvExmReason());
        params.addValue("survProviderContact", caseManagement.getSurvProviderContact());
        params.addValue("actRefTypeCd", caseManagement.getActRefTypeCd());
        params.addValue("initiatingAgncy", caseManagement.getInitiatingAgncy());
        params.addValue("oojInitgAgncyOutcDueDate", caseManagement.getOojInitgAgncyOutcDueDate());
        params.addValue("oojInitgAgncyOutcSntDate", caseManagement.getOojInitgAgncyOutcSntDate());
        params.addValue("oojInitgAgncyRecdDate", caseManagement.getOojInitgAgncyRecdDate());
        params.addValue("caseReviewStatus", caseManagement.getCaseReviewStatus());
        params.addValue("survAssignedDate", caseManagement.getSurvAssignedDate());
        params.addValue("follUpAssignedDate", caseManagement.getFollUpAssignedDate());
        params.addValue("initFollUpAssignedDate", caseManagement.getInitFollUpAssignedDate());
        params.addValue("interviewAssignedDate", caseManagement.getInterviewAssignedDate());
        params.addValue("initInterviewAssignedDate", caseManagement.getInitInterviewAssignedDate());
        params.addValue("caseClosedDate", caseManagement.getCaseClosedDate());
        params.addValue("caseReviewStatusDate", caseManagement.getCaseReviewStatusDate());


        jdbcTemplateOdse.update(MERGE_CASE_MANAGEMENT, params);
    }

}
