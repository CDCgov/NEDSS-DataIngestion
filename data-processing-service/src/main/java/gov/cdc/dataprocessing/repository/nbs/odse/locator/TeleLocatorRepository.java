package gov.cdc.dataprocessing.repository.nbs.odse.locator;

import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.TeleLocator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeleLocatorRepository  extends JpaRepository<TeleLocator, Long> {
    @Query(value = "SELECT x FROM TeleLocator x WHERE x.teleLocatorUid IN :uids", nativeQuery = false)
    Optional<List<TeleLocator>> findByTeleLocatorUids(@Param("uids") List<Long> uids);
}
