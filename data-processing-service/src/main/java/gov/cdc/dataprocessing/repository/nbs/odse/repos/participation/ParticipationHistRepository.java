package gov.cdc.dataprocessing.repository.nbs.odse.repos.participation;

import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.Participation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.participation.ParticipationHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationHistRepository  extends JpaRepository<ParticipationHist, Long> {
}
