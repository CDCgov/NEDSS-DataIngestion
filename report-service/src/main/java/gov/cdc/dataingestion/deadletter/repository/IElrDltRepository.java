package gov.cdc.dataingestion.deadletter.repository;

import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterELRModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IElrDltRepository extends JpaRepository<ElrDeadLetterELRModel, String> {

    @Query(value = "SELECT dlt FROM ElrDeadLetterELRModel dlt WHERE dlt.dltStatus = 'ERROR' ")
    List<ElrDeadLetterELRModel> findAllNewDlt(Sort sort);
}
