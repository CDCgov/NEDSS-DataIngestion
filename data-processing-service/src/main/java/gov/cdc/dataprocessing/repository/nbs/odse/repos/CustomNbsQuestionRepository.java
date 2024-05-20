package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model.QuestionRequiredNnd;

import java.util.Collection;

public interface CustomNbsQuestionRepository {
    Collection<QuestionRequiredNnd> retrieveQuestionRequiredNnd(String formCd);
}
