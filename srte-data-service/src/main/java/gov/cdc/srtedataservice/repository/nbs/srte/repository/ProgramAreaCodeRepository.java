package gov.cdc.srtedataservice.repository.nbs.srte.repository;

import gov.cdc.srtedataservice.repository.nbs.srte.model.ProgramAreaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ProgramAreaCodeRepository extends JpaRepository<ProgramAreaCode, String> {
}
