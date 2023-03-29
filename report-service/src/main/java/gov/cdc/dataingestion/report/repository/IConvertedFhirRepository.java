package gov.cdc.dataingestion.report.repository;

import gov.cdc.dataingestion.report.repository.model.HL7toFhirModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IConvertedFhirRepository extends JpaRepository<HL7toFhirModel, String> {
}
