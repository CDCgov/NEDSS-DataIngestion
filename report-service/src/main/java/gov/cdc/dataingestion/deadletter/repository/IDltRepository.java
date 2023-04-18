package gov.cdc.dataingestion.deadletter.repository;

import gov.cdc.dataingestion.deadletter.repository.model.DeadLetterELRModel;
import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IDltRepository extends JpaRepository<DeadLetterELRModel, String> {
}
