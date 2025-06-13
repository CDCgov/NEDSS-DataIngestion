package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObsValueTxt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository


public interface ObsValueTxtRepository extends JpaRepository<ObsValueTxt, Long> {
    @Query("SELECT data FROM ObsValueTxt data WHERE data.observationUid = :uid")
    Collection<ObsValueTxt> findRecordsById(@Param("uid") Long uid);
}
