package gov.cdc.dataprocessing.repository.nbs.odse.repos.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.PatientEncounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientEncounterRepository  extends JpaRepository<PatientEncounter, Long> {
}
