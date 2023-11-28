package gov.cdc.dataingestion.report.repository;

import gov.cdc.dataingestion.report.repository.model.RawERLModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRawELRRepository extends JpaRepository<RawERLModel, String> {
}