package gov.cdc.dataingestion.validation.repository;

import gov.cdc.dataingestion.validation.model.RawERLModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawELRRepository extends JpaRepository<RawERLModel, String> {
}
