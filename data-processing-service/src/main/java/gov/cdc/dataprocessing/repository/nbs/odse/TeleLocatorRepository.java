package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.Role;
import gov.cdc.dataprocessing.repository.nbs.odse.model.TeleLocator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeleLocatorRepository  extends JpaRepository<TeleLocator, Long> {
    @Query(value = "SELECT x FROM TeleLocator x WHERE x.teleLocatorUid IN :uids", nativeQuery = false)
    List<TeleLocator> findByTeleLocatorUids(@Param("uids") List<Long> uids);
}
