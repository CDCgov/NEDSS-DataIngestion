package gov.cdc.dataingestion.nbs.repository.implementation;

import gov.cdc.dataingestion.exception.EcrCdaXmlException;
import gov.cdc.dataingestion.nbs.repository.model.IEcrLookUpRepository;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.ConstantLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcAnswerLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.PhdcQuestionLookUpDto;
import gov.cdc.dataingestion.nbs.repository.model.dto.lookup.QuestionIdentifierMapDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Repository
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
public class EcrLookUpRepository implements IEcrLookUpRepository {
    @PersistenceContext(unitName = "ingest")
    private EntityManager entityManager;

    public ConstantLookUpDto fetchConstantLookUpByCriteriaWithColumn(String column, String value) throws EcrCdaXmlException {
        String queryString = loadLookUpSqlFromFile("constant_lookup.sql");
        queryString = queryString.replace("{LOOK_UP_COL}", column);

        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("LOOK_UP_VAL", value);

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

    public PhdcAnswerLookUpDto fetchPhdcAnswerByCriteriaForTranslationCode(String questionIdentifier, String ansFromCode) throws EcrCdaXmlException {
        String queryString = loadLookUpSqlFromFile("phdc_answer_translation_code.sql");
        Query query = entityManager.createNativeQuery(queryString);
        if (questionIdentifier != null && !questionIdentifier.isEmpty()) {
            query.setParameter("QUESTION_IDENTIFIER", questionIdentifier);
        } else {
            query.setParameter("QUESTION_IDENTIFIER", null);
        }
        if(ansFromCode != null && !ansFromCode.isEmpty()) {
            query.setParameter("ANSWER_FROM_CODE", ansFromCode);
        }
        else {
            query.setParameter("ANSWER_FROM_CODE", null);
        }

        PhdcAnswerLookUpDto model = new PhdcAnswerLookUpDto();
        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            var val = results.get(0);
            model.setAnsFromCode(nullToString(val[0]));
            model.setAnsFromCodeSystemCd(nullToString(val[1]));
            model.setAnsFromCodeSystemDescTxt(nullToString(val[2]));
            model.setAnsFromDisplayNm(nullToString(val[3]));
            model.setAnsToCode(nullToString(val[4]));
            model.setAnsToCodeSystemCd((nullToString(val[5])));
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

    public PhdcQuestionLookUpDto fetchPhdcQuestionByCriteria(String questionIdentifier) throws EcrCdaXmlException {
        String queryString = loadLookUpSqlFromFile("phdc_question_by_criteria.sql");
        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("QUES_IDENTIFIER", questionIdentifier);
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

    // TODO: Merge this with the other method
    public PhdcQuestionLookUpDto fetchPhdcQuestionByCriteriaWithColumn(String column, String value) throws EcrCdaXmlException {
        String queryString = loadLookUpSqlFromFile("phdc_question_by_column.sql");
        queryString = queryString.replace("{TAB_COLUMN}", column);

        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("COL_VALUE", value);

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

    public QuestionIdentifierMapDto fetchQuestionIdentifierMapByCriteriaByCriteria(String columNm, String value) throws EcrCdaXmlException {
        String queryString = loadLookUpSqlFromFile("question_identifier.sql");
        queryString = queryString.replace("{COLUMN_NM}", columNm);

        Query query = entityManager.createNativeQuery(queryString);
        query.setParameter("COLUMN_NM_VALUE", value);

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

    private String loadLookUpSqlFromFile(String filename) throws EcrCdaXmlException {
        try (InputStream is = getClass().getResourceAsStream("/queries/lookup/" + filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new EcrCdaXmlException("Failed to load SQL file: " + filename + " " +  e.getMessage());
        }
    }

    public static String nullToString(Object obj) {
        return obj != null ? String.valueOf(obj) : null;
    }

}
