package gov.cdc.dataingestion.conversion.repository;

import gov.cdc.dataingestion.conversion.repository.model.HL7ToFHIRModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IHL7ToFHIRRepository extends JpaRepository<HL7ToFHIRModel, String> {
}
