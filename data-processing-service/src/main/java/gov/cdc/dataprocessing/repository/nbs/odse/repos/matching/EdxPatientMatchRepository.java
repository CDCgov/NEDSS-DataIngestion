package gov.cdc.dataprocessing.repository.nbs.odse.repos.matching;

import gov.cdc.dataprocessing.repository.nbs.odse.model.matching.EdxPatientMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
public interface EdxPatientMatchRepository extends JpaRepository<EdxPatientMatch, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM EdxPatientMatch e WHERE e.patientUid = ?1 AND e.matchString NOT LIKE 'LR^%'", nativeQuery = false)
    void deleteByPatientUidAndMatchStringNotLike(Long patientUid);
}