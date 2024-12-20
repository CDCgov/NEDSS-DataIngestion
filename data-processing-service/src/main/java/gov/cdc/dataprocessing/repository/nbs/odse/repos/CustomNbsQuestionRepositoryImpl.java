package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.custom_model.QuestionRequiredNnd;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class CustomNbsQuestionRepositoryImpl implements CustomNbsQuestionRepository {
    @PersistenceContext(unitName = "odse")
    private EntityManager entityManager;


    private static final String NND_UI_META_DATA_BY_FORM_CODE = "SELECT " +
            "distinct num.nbs_question_uid  nbsQuestionUid ," +
            "nnd.question_identifier questionIdentifier ,"+
            "num.question_label  questionLabel ,"+
            "num.data_location  dataLocation  "+
            "FROM NND_Metadata nnd INNER JOIN NBS_UI_Metadata num " +
            "ON nnd.nbs_ui_metadata_uid = num.nbs_ui_metadata_uid " +
            "WHERE " +
            "(nnd.question_required_nnd = 'R') and (num.standard_nnd_ind_cd is null or num.standard_nnd_ind_cd='F')"+
            "AND (nnd.investigation_form_cd = :investigationFormCode)";

    public CustomNbsQuestionRepositoryImpl() {
        // For Unit Test
    }

    public  Collection<QuestionRequiredNnd> retrieveQuestionRequiredNnd(String formCd) {
        Query query = entityManager.createNativeQuery(NND_UI_META_DATA_BY_FORM_CODE);
        query.setParameter("investigationFormCode", formCd);
        Collection<QuestionRequiredNnd> dataCollection = new ArrayList<>();

        List<Object[]> results = query.getResultList();
        if (results != null && !results.isEmpty()) {
            for(var result : results) {
                QuestionRequiredNnd container = new QuestionRequiredNnd();
                container.setNbsQuestionUid((Long) result[0]);
                container.setQuestionIdentifier((String) result[1]);
                container.setQuestionLabel((String) result[2]);
                container.setDataLocation((String) result[3]);
                dataCollection.add(container);
            }
        }

        return dataCollection;
    }
}
