package gov.cdc.dataingestion.nbs.repository.model;

import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.ConstantLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcAnswerLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;

public interface IEcrLookUpRepository {
    ConstantLookUpDto FetchConstantLookUpByCriteria();
    ConstantLookUpDto FetchConstantLookUpByCriteriaWithColumn(String column, String value);
    PhdcAnswerLookUpDto FetchPhdcAnswerByCriteria();
    PhdcAnswerLookUpDto FetchPhdcAnswerByCriteriaForTranslationCode(String questionIdentifier, String ansFromCode);

    PhdcQuestionLookUpDto FetchPhdcQuestionByCriteria(String questionIdentifier);

    PhdcQuestionLookUpDto FetchPhdcQuestionByCriteriaWithColumn(String column, String value);

    QuestionIdentifierMapDto FetchQuestionIdentifierMapDto();

    QuestionIdentifierMapDto FetchQuestionIdentifierMapByCriteriaByCriteria(String columNm, String value);

}
