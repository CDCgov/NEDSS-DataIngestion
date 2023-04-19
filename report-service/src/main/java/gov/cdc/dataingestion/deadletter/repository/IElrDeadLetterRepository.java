package gov.cdc.dataingestion.deadletter.repository;

import gov.cdc.dataingestion.deadletter.repository.model.ElrDeadLetterModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IElrDeadLetterRepository extends JpaRepository<ElrDeadLetterModel, String> {

    @Query(value = "SELECT dlt FROM ElrDeadLetterModel dlt WHERE dlt.dltStatus = 'ERROR' ")
    List<ElrDeadLetterModel> findAllNewDlt(Sort sort);
}
