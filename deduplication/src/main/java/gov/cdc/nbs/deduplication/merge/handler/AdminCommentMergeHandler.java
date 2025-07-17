package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.merge.model.PatientMergeAudit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;

@Component
@Order(2)
public class AdminCommentMergeHandler implements SectionMergeHandler {

  private final NamedParameterJdbcTemplate nbsTemplate;

  public AdminCommentMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  // Merge modifications have been applied to the Administrative Comments
  @Override
  @Transactional(transactionManager = "nbsTransactionManager", propagation = Propagation.MANDATORY)
  public void handleMerge(String matchId, PatientMergeRequest request, PatientMergeAudit patientMergeAudit) {
    String survivorId = request.survivingRecord();
    String adminCommentsSourcePersonUid = request.adminComments();
    updateAdministrativeComments(survivorId, adminCommentsSourcePersonUid);
  }

  private void updateAdministrativeComments(String survivorId, String adminCommentsSourcePersonUid) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("survivorId", survivorId);
    parameters.addValue("adminSourcePersonUid", adminCommentsSourcePersonUid);
    nbsTemplate.update(QueryConstants.UPDATE_PERSON_ADMIN_COMMENT_FROM_SOURCE, parameters);
  }
}
