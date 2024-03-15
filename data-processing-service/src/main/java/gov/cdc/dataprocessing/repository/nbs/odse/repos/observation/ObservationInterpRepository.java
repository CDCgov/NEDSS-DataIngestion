package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObservationInterp;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObservationReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ObservationInterpRepository extends JpaRepository<ObservationInterp, Long> {
    @Query("SELECT data FROM ObservationInterp data WHERE data.observationUid = :uid")
    Collection<ObservationInterp> findRecordsById(@Param("uid") Long uid);
}
