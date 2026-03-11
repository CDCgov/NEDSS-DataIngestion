package gov.cdc.dataprocessing.repository.nbs.odse.repos.act;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActIdRepository extends JpaRepository<ActId, Long> {
  @Query("SELECT data FROM ActId data WHERE data.actUid = :uid")
  Optional<Collection<ActId>> findRecordsById(@Param("uid") Long uid);
}
