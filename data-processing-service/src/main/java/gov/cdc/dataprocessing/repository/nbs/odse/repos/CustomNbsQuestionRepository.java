package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model.QuestionRequiredNnd;
import java.util.Collection;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomNbsQuestionRepository {
  Collection<QuestionRequiredNnd> retrieveQuestionRequiredNnd(String formCd);
}
