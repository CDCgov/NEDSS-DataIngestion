package gov.cdc.dataingestion.nbs.repository.model;

import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.ConstantLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcAnswerLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import org.springframework.stereotype.Repository;

@Repository
public interface IEcrLookUpRepository {
    ConstantLookUpDto fetchConstantLookUpByCriteriaWithColumn(String column, String value);
    PhdcAnswerLookUpDto fetchPhdcAnswerByCriteriaForTranslationCode(String questionIdentifier, String ansFromCode);

    PhdcQuestionLookUpDto fetchPhdcQuestionByCriteria(String questionIdentifier);

    PhdcQuestionLookUpDto fetchPhdcQuestionByCriteriaWithColumn(String column, String value);

    QuestionIdentifierMapDto fetchQuestionIdentifierMapByCriteriaByCriteria(String columNm, String value);



}
