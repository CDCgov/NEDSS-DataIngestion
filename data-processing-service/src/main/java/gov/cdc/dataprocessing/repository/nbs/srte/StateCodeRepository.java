package gov.cdc.dataprocessing.repository.nbs.srte;

import gov.cdc.dataprocessing.repository.nbs.srte.model.JurisdictionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StateCodeRepository  extends JpaRepository<StateCode, String> {
    @Query(value = "SELECT * as key FROM StateCode WHERE state_nm = ?1", nativeQuery = true)
    Optional<StateCode> findStateCdByStateName(String stateName);
}
