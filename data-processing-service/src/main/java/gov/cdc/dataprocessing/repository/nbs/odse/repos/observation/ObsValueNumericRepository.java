package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueNumeric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ObsValueNumericRepository extends JpaRepository<ObsValueNumeric, Long> {
    @Query("SELECT data FROM ObsValueNumeric data WHERE data.observationUid = :uid")
    Collection<ObsValueNumeric> findRecordsById(@Param("uid") Long uid);
}
