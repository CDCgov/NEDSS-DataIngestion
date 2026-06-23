package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StateCodeRepository extends JpaRepository<StateCode, String> {
  @Query("SELECT pn FROM StateCode pn WHERE pn.stateNm = :state_nm")
  Optional<StateCode> findStateCdByStateName(@Param("state_nm") String stateName);
}
