package gov.cdc.dataprocessing.repository.nbs.odse.repos.act;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActLocatorParticipation;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActLocatorParticipationRepository
    extends JpaRepository<ActLocatorParticipation, Long> {
  @Query("SELECT data FROM ActLocatorParticipation data WHERE data.actUid = :uid")
  Collection<ActLocatorParticipation> findRecordsById(@Param("uid") Long uid);
}
