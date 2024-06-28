package gov.cdc.dataprocessing.repository.nbs.odse.repos.participation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ParticipationHistId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationHistRepository  extends JpaRepository<ParticipationHist, ParticipationHistId> {
    @Query("SELECT data.versionCtrlNbr FROM ParticipationHist data WHERE data.subjectEntityUid = ?1 AND data.actUid = ?2 AND data.typeCd = ?3")
    Optional<List<Integer>> findVerNumberByKey(Long subjectEntityUid, Long actUid, String typeCd);
}
