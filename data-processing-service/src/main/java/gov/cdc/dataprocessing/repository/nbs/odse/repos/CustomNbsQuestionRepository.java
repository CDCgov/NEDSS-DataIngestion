package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model.QuestionRequiredNnd;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CustomNbsQuestionRepository {
    Collection<QuestionRequiredNnd> retrieveQuestionRequiredNnd(String formCd);
}
