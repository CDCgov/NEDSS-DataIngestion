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
public class CustomNbsQuestionRepositoryImpl implements CustomNbsQuestionRepository {
    @PersistenceContext(unitName = "odse")
    private EntityManager entityManager;


    private String NND_UI_META_DATA_BY_FORM_CODE = "SELECT " +
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
