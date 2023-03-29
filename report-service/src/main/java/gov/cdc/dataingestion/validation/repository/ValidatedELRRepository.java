package gov.cdc.dataingestion.validation.repository;

import gov.cdc.dataingestion.validation.model.RawERLModel;
import gov.cdc.dataingestion.validation.model.ValidatedELRModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidatedELRRepository extends JpaRepository<ValidatedELRModel, String> {
}
