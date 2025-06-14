package gov.cdc.nbs.deduplication.merge.handler;

import gov.cdc.nbs.deduplication.constants.QueryConstants;
import gov.cdc.nbs.deduplication.merge.model.PatientMergeRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class AdminCommentMergeHandler implements SectionMergeHandler {

  private final NamedParameterJdbcTemplate nbsTemplate;

  public AdminCommentMergeHandler(@Qualifier("nbsNamedTemplate") NamedParameterJdbcTemplate nbsTemplate) {
    this.nbsTemplate = nbsTemplate;
  }

  //Merge modifications have been applied to the Administrative Comments
  @Override
  public void handleMerge(String matchId, PatientMergeRequest request) {
    String survivorId = request.survivingRecord();
    String adminCommentsSourcePersonUid = request.adminCommentsSource();
    updateAdministrativeComments(survivorId, adminCommentsSourcePersonUid);
  }


  private void updateAdministrativeComments(String survivorId, String adminCommentsSourcePersonUid) {
    MapSqlParameterSource parameters = new MapSqlParameterSource();
    parameters.addValue("survivorId", survivorId);
    parameters.addValue("adminSourcePersonUid", adminCommentsSourcePersonUid);
    nbsTemplate.update(QueryConstants.UPDATE_PERSON_ADMIN_COMMENT_FROM_SOURCE, parameters);
  }
}
