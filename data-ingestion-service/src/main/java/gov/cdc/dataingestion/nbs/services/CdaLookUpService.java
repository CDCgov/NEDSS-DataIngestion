package gov.cdc.dataingestion.nbs.services;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.model.IEcrLookUpRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.ConstantLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcAnswerLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import gov.cdc.dataingestion.nbs.services.interfaces.ICdaLookUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class CdaLookUpService  implements ICdaLookUpService {
    private IEcrLookUpRepository ecrLookUpRepository;

    @Autowired
    public CdaLookUpService(IEcrLookUpRepository ecrLookUpRepository) {
        this.ecrLookUpRepository = ecrLookUpRepository;
    }


    public ConstantLookUpDto fetchConstantLookUpByCriteriaWithColumn(String column, String value) throws EcrCdaXmlException {
        return this.ecrLookUpRepository.fetchConstantLookUpByCriteriaWithColumn(column, value);
    }

    public PhdcAnswerLookUpDto fetchPhdcAnswerByCriteriaForTranslationCode(String questionIdentifier, String ansFromCode) throws EcrCdaXmlException {
        return this.ecrLookUpRepository.fetchPhdcAnswerByCriteriaForTranslationCode(questionIdentifier, ansFromCode);
    }

    public PhdcQuestionLookUpDto fetchPhdcQuestionByCriteria(String questionIdentifier) throws EcrCdaXmlException {
        return this.ecrLookUpRepository.fetchPhdcQuestionByCriteria(questionIdentifier);
    }

    public PhdcQuestionLookUpDto fetchPhdcQuestionByCriteriaWithColumn(String column, String value) throws EcrCdaXmlException {
        return this.ecrLookUpRepository.fetchPhdcQuestionByCriteriaWithColumn(column, value);
    }

    public QuestionIdentifierMapDto fetchQuestionIdentifierMapByCriteriaByCriteria(String columNm, String value) throws EcrCdaXmlException {
        return this.ecrLookUpRepository.fetchQuestionIdentifierMapByCriteriaByCriteria(columNm, value);
    }

}
