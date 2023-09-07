package gov.cdc.dataingestion.nbs.repository.implementation;

import gov.cdc.dataingestion.nbs.repository.EcrMsgQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EcrMsgQueryRepositoryImpl implements EcrMsgQueryRepository {
    @PersistenceContext(unitName = "nbs")
    private EntityManager entityManager;

    public void FetchMsgContainerForApplicableEcr() {
        String queryString = "SELECT TOP(1) MSG_CONTAINER_UID FROM MSG_CONTAINER";
        Query query = entityManager.createNativeQuery(queryString);
        List<Object[]> results = query.getResultList();
        var test = results;
    }
}
