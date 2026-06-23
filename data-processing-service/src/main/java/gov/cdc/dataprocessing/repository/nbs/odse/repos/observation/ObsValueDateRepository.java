package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueDate;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ObsValueDateRepository extends JpaRepository<ObsValueDate, Long> {
  @Query("SELECT data FROM ObsValueDate data WHERE data.observationUid = :uid")
  Collection<ObsValueDate> findRecordsById(@Param("uid") Long uid);
}
