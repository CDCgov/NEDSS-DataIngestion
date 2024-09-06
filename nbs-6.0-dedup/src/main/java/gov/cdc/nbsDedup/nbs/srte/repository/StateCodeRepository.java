package gov.cdc.nbsDedup.nbs.srte.repository;

import gov.cdc.nbsDedup.nbs.srte.model.StateCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StateCodeRepository  extends JpaRepository<StateCode, String> {
    @Query("SELECT pn FROM StateCode pn WHERE pn.stateNm = :state_nm")
    Optional<StateCode> findStateCdByStateName( @Param("state_nm") String stateName);
}
