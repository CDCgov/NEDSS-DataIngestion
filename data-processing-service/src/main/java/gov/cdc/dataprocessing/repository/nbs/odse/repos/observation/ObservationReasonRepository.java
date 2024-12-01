package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObservationReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
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
