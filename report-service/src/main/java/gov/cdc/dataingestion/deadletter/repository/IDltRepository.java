package gov.cdc.dataingestion.deadletter.repository;

import gov.cdc.dataingestion.deadletter.repository.model.DeadLetterELRModel;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IDltRepository extends JpaRepository<DeadLetterELRModel, String> {

    @Query(value = "SELECT dlt FROM DeadLetterELRModel dlt WHERE dlt.dltStatus = 'ERROR' ")
    List<DeadLetterELRModel> findAllNewDlt(Sort sort);
}
