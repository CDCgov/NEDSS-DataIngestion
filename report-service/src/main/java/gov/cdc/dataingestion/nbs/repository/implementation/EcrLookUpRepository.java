package gov.cdc.dataingestion.nbs.repository.implementation;

import gov.cdc.dataingestion.nbs.repository.model.IEcrLookUpRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.ConstantLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcAnswerLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class EcrLookUpRepository implements IEcrLookUpRepository {
    @PersistenceContext(unitName = "ingest")
    private EntityManager entityManager;

    public ConstantLookUpDto FetchConstantLookUpByCriteria() {
        return new ConstantLookUpDto();
    }

    @Override
    public ConstantLookUpDto FetchConstantLookUpByCriteriaWithColumn(String column, String value) {
        return null;
    }

    public PhdcAnswerLookUpDto FetchPhdcAnswerByCriteria() {
        return new PhdcAnswerLookUpDto();
    }

    public PhdcAnswerLookUpDto FetchPhdcAnswerByCriteriaForTranslationCode(String questionIdentifier, String ansFromCode) {
        return null;
    }

    public PhdcQuestionLookUpDto FetchPhdcQuestionByCriteria(String questionIdentifier) {
        return null;
    }

    @Override
    public PhdcQuestionLookUpDto FetchPhdcQuestionByCriteriaWithColumn(String column, String value) {
        return null;
    }

    public QuestionIdentifierMapDto FetchQuestionIdentifierMapDto() {
        return new QuestionIdentifierMapDto();
    }

    public QuestionIdentifierMapDto FetchQuestionIdentifierMapByCriteriaByCriteria(String columNm, String value) {
        return null;
    }
}
