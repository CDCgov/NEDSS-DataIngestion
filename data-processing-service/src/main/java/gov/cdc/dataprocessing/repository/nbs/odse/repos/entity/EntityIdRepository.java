package gov.cdc.dataprocessing.repository.nbs.odse.repos.entity;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.EntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public interface EntityIdRepository extends JpaRepository<EntityId, Long> {
    @Query("SELECT pn FROM EntityId pn WHERE pn.entityUid = :parentUid")
    Optional<List<EntityId>> findByParentUid(@Param("parentUid") Long parentUid);

    @Query("SELECT MAX(pn.entityIdSeq) FROM EntityId pn WHERE pn.entityUid = :parentUid")
    Optional<Integer> findMaxEntityId(@Param("parentUid") Long parentUid);
    @Query("SELECT eid FROM EntityId eid WHERE eid.entityUid = :entityUid AND eid.recordStatusCode ='ACTIVE'")
    Optional<List<EntityId>> findByEntityUid(@Param("entityUid") Long entityUid);


    @Transactional
    @Modifying
    @Query("DELETE FROM EntityId pn WHERE pn.entityUid = :entityUid AND pn.entityIdSeq = :entityIdSeq")
    void deleteEntityIdAndSeq (@Param("entityUid") Long entityUid, @Param("entityIdSeq") Integer entityIdSeq);
}