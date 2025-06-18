package gov.cdc.dataprocessing.repository.nbs.odse.repos.locator;

import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository


public interface PostalLocatorRepository extends JpaRepository<PostalLocator, Long> {
    @Query(value = "SELECT x FROM PostalLocator x WHERE x.postalLocatorUid IN :uids", nativeQuery = false)
    Optional<List<PostalLocator>> findByPostalLocatorUids(@Param("uids") List<Long> uids);


}
