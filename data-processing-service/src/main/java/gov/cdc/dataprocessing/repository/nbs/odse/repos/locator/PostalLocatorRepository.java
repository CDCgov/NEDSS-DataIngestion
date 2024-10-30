package gov.cdc.dataprocessing.repository.nbs.odse.repos.locator;

import gov.cdc.dataprocessing.repository.nbs.odse.model.locator.PostalLocator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public interface PostalLocatorRepository extends JpaRepository<PostalLocator, Long> {
    @Query(value = "SELECT x FROM PostalLocator x WHERE x.postalLocatorUid IN :uids", nativeQuery = false)
    Optional<List<PostalLocator>> findByPostalLocatorUids(@Param("uids") List<Long> uids);

    @Modifying
    @Query(value = "DELETE FROM PostalLocator x WHERE x.postalLocatorUid = :postalId", nativeQuery = false)
    void deletePostalLocatorById(@Param("postalId") Long postalId);


    @Modifying
    @Query(value = "UPDATE PostalLocator x SET x.recordStatusCd = :status WHERE x.postalLocatorUid = :postalId", nativeQuery = false)
    void updatePostalStatus(@Param("postalId") Long postalId, @Param("status") String status);

}
