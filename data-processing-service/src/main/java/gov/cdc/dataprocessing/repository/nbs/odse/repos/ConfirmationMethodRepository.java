package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.ClinicalDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.model.ConfirmationMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationMethodRepository extends JpaRepository<ConfirmationMethod, Long> {
}
