package gov.cdc.dataingestion.nbs.repository.implementation;

import gov.cdc.dataingestion.nbs.repository.model.IEcrLookUpRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.ConstantLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcAnswerLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EcrLookUpRepository implements IEcrLookUpRepository {
    @PersistenceContext(unitName = "ingest")
    private EntityManager entityManager;

    public ConstantLookUpDto fetchConstantLookUpByCriteriaWithColumn(String column, String value) {
        String queryString = "SELECT * FROM ecr_constant_lookup WHERE " + column + " = '"+value +"'";
        Query query = entityManager.createNativeQuery(queryString);
        ConstantLookUpDto model = new ConstantLookUpDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            model.setId(nullToString(val[0]));
            model.setSubjectArea((nullToString(val[1])));
            model.setQuestionIdentifier(nullToString(val[2]));
            model.setQuestionDisplayName((nullToString(val[3])));
            model.setSampleValue((nullToString(val[4])));
            model.setUsage(nullToString(val[5]));
            return model;

        }

        return null;
    }

    public PhdcAnswerLookUpDto fetchPhdcAnswerByCriteriaForTranslationCode(String questionIdentifier, String ansFromCode) {
        String queryString =
        "SELECT [ANS_FROM_CODE], " +
                "[ANS_FROM_CODE_SYSTEM_CD], " +
                "[ANS_FROM_CODE_SYSTEM_DESC_TXT], " +
                "[ANS_FROM_DISPLAY_NM], " +
                "[ANS_TO_CODE], " +
                "[ANS_TO_CODE_SYSTEM_CD], " +
                "[ANS_TO_CODE_SYSTEM_DESC_TXT], " +
                "[ANS_TO_DISPLAY_NM], " +
                "[CODE_TRANSLATION_REQUIRED], " +
                "[DOC_TYPE_CD], " +
                "[DOC_TYPE_VERSION_TXT], " +
                "[QUES_CODE_SYSTEM_CD], " +
                "[QUESTION_IDENTIFIER], " +
                "[SENDING_SYSTEM_CD] " +
                "FROM [ecr_phdc_answer_lookup] " +
                "WHERE [QUESTION_IDENTIFIER] = '"+ ansFromCode +"'";
        Query query = entityManager.createNativeQuery(queryString);
        PhdcAnswerLookUpDto model = new PhdcAnswerLookUpDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            model.setAnsToCode(nullToString(val[0]));
            model.setAnsToCodeSystemCd((nullToString(val[1])));
            model.setAnsFromCodeSystemDescTxt(nullToString(val[2]));
            model.setAnsFromDisplayNm((nullToString(val[3])));
            model.setAnsToCode((nullToString(val[4])));
            model.setAnsToCodeSystemCd(nullToString(val[5]));
            model.setAnsToCodeSystemDescTxt(nullToString(val[6]));
            model.setAnsToDisplayNm((nullToString(val[7])));
            model.setCodeTranslationRequired(nullToString(val[8]));
            model.setDocTypeCd((nullToString(val[9])));
            model.setDocTypeVersionTxt((nullToString(val[10])));
            model.setQuesCodeSystemCd(nullToString(val[11]));
            model.setQuestionIdentifier(nullToString(val[12]));
            model.setSendingSystemCd(nullToString(val[13]));
            return model;

        }
        return null;
    }

    public PhdcQuestionLookUpDto fetchPhdcQuestionByCriteria(String questionIdentifier) {
        String queryString =
                "SELECT [DOC_TYPE_CD], " +
                        "[DOC_TYPE_VERSION_TXT], " +
                        "[QUES_CODE_SYSTEM_CD], " +
                        "[QUES_CODE_SYSTEM_DESC_TXT], " +
                        "[DATA_TYPE], " +
                        "[QUESTION_IDENTIFIER], " +
                        "[QUES_DISPLAY_NAME], " +
                        "[SECTION_NM], " +
                        "[SENDING_SYSTEM_CD] " +
                        "FROM [ecr_phdc_question_lookup] " +
                        "WHERE [QUESTION_IDENTIFIER] = '"+ questionIdentifier +"'";
        Query query = entityManager.createNativeQuery(queryString);
        PhdcQuestionLookUpDto model = new PhdcQuestionLookUpDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            model.setDataType(nullToString(val[0]));
            model.setDocTypeVersionTxt((nullToString(val[1])));
            model.setQuesCodeSystemCd(nullToString(val[2]));
            model.setQuesCodeSystemDescTxt((nullToString(val[3])));
            model.setDataType((nullToString(val[4])));
            model.setQuestionIdentifier(nullToString(val[5]));
            model.setQuesDisplayName(nullToString(val[6]));
            model.setSectionNm((nullToString(val[7])));
            model.setSendingSystemCd(nullToString(val[8]));
            return model;

        }

        return null;
    }

    public PhdcQuestionLookUpDto fetchPhdcQuestionByCriteriaWithColumn(String column, String value) {
        String queryString =
                "SELECT [DOC_TYPE_CD], " +
                        "[DOC_TYPE_VERSION_TXT], " +
                        "[QUES_CODE_SYSTEM_CD], " +
                        "[QUES_CODE_SYSTEM_DESC_TXT], " +
                        "[DATA_TYPE], " +
                        "[QUESTION_IDENTIFIER], " +
                        "[QUES_DISPLAY_NAME], " +
                        "[SECTION_NM], " +
                        "[SENDING_SYSTEM_CD] " +
                        "FROM [ecr_phdc_question_lookup] " +
                        "WHERE "+column +" = '"+ value +"'";
        Query query = entityManager.createNativeQuery(queryString);
        PhdcQuestionLookUpDto model = new PhdcQuestionLookUpDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            model.setDataType(nullToString(val[0]));
            model.setDocTypeVersionTxt((nullToString(val[1])));
            model.setQuesCodeSystemCd(nullToString(val[2]));
            model.setQuesCodeSystemDescTxt((nullToString(val[3])));
            model.setDataType((nullToString(val[4])));
            model.setQuestionIdentifier(nullToString(val[5]));
            model.setQuesDisplayName(nullToString(val[6]));
            model.setSectionNm((nullToString(val[7])));
            model.setSendingSystemCd(nullToString(val[8]));
            return model;

        }

        return null;
    }

    public QuestionIdentifierMapDto fetchQuestionIdentifierMapByCriteriaByCriteria(String columNm, String value) {
        String queryString =
                "SELECT [COLUMN_NM], " +
                        "[QUESTION_IDENTIFIER], " +
                        "[DYNAMIC_QUESTION_IDENTIFIER] " +
                        "FROM [ecr_question_identifier_map] " +
                        "WHERE "+columNm +" = '"+ value +"'";
        Query query = entityManager.createNativeQuery(queryString);
        QuestionIdentifierMapDto model = new QuestionIdentifierMapDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            model.setColumnNm(nullToString(val[0]));
            model.setQuestionIdentifier((nullToString(val[1])));
            model.setDynamicQuestionIdentifier(nullToString(val[2]));
            return model;

        }

        return null;
    }

    public static String nullToString(Object obj) {
        return obj != null ? String.valueOf(obj) : null;
    }

}
