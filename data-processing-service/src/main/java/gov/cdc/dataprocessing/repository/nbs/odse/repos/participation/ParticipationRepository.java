package gov.cdc.dataprocessing.repository.nbs.odse.repos.participation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.ParticipationId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, ParticipationId> {
    Optional<List<Participation>> findBySubjectEntityUidAndActUid(Long subjectEntityUid,Long actUid);
}