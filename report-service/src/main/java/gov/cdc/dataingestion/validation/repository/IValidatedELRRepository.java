package gov.cdc.dataingestion.validation.repository;

import gov.cdc.dataingestion.validation.repository.model.ValidatedELRModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IValidatedELRRepository extends JpaRepository<ValidatedELRModel, String> {
}
