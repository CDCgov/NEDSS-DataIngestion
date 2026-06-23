package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueCoded;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ObsValueCodedRepository extends JpaRepository<ObsValueCoded, Long> {
  @Query("SELECT data FROM ObsValueCoded data WHERE data.observationUid = :uid")
  Collection<ObsValueCoded> findRecordsById(@Param("uid") Long uid);
}
