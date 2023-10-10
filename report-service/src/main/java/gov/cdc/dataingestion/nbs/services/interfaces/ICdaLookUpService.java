package gov.cdc.dataingestion.nbs.services.interfaces;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.ConstantLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcAnswerLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;

public interface ICdaLookUpService {
    ConstantLookUpDto fetchConstantLookUpByCriteriaWithColumn(String column, String value) throws EcrCdaXmlException;
    PhdcAnswerLookUpDto fetchPhdcAnswerByCriteriaForTranslationCode(String questionIdentifier, String ansFromCode) throws EcrCdaXmlException;

    PhdcQuestionLookUpDto fetchPhdcQuestionByCriteria(String questionIdentifier) throws EcrCdaXmlException;

    PhdcQuestionLookUpDto fetchPhdcQuestionByCriteriaWithColumn(String column, String value) throws EcrCdaXmlException;

    QuestionIdentifierMapDto fetchQuestionIdentifierMapByCriteriaByCriteria(String columNm, String value) throws EcrCdaXmlException;

}
