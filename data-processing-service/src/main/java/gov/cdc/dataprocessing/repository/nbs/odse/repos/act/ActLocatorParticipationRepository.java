package gov.cdc.dataprocessing.repository.nbs.odse.repos.act;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ActLocatorParticipationRepository extends JpaRepository<ActLocatorParticipation, Long> {
    @Query("SELECT data FROM ActLocatorParticipation data WHERE data.actUid = :uid")
    Collection<ActLocatorParticipation> findRecordsById(@Param("uid") Long uid);
}