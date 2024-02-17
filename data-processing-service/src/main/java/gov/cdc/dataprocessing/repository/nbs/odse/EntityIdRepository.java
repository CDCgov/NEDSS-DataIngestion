package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonEthnicGroup;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonRace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntityIdRepository extends JpaRepository<EntityId, Long> {
    @Query("SELECT pn FROM EntityId pn WHERE pn.entityUid = :parentUid")
    Optional<List<EntityId>> findByParentUid(@Param("parentUid") Long parentUid);
}