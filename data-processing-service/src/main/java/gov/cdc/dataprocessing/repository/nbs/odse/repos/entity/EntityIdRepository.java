package gov.cdc.dataprocessing.repository.nbs.odse.repos.entity;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityIdRepository extends JpaRepository<EntityId, Long> {
  @Query("SELECT pn FROM EntityId pn WHERE pn.entityUid = :parentUid")
  Optional<List<EntityId>> findByParentUid(@Param("parentUid") Long parentUid);

  @Query("SELECT MAX(pn.entityIdSeq) FROM EntityId pn WHERE pn.entityUid = :parentUid")
  Optional<Integer> findMaxEntityId(@Param("parentUid") Long parentUid);

  @Query(
      "SELECT eid FROM EntityId eid WHERE eid.entityUid = :entityUid AND eid.recordStatusCode ='ACTIVE'")
  Optional<List<EntityId>> findByEntityUid(@Param("entityUid") Long entityUid);
}
