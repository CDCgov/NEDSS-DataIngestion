package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObservationReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139"})
public interface ObservationReasonRepository extends JpaRepository<ObservationReason, Long> {
    /**
     *   public static final String SELECT_OBSERVATION_REASONS =
     *       "SELECT observation_uid \"observationUid\", reason_cd \"reasonCd\", "+
     *       " reason_desc_txt \"reasonDescTxt\" FROM " +
     *       DataTables.OBSERVATION_REASON_TABLE +
     *       " WITH (NOLOCK) WHERE observation_uid = ?";
     *
     * */
    @Query("SELECT data FROM ObservationReason data WHERE data.observationUid = :uid")
    Collection<ObservationReason> findRecordsById(@Param("uid") Long uid);
}
