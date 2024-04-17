package gov.cdc.dataprocessing.repository.nbs.odse.repos.observation;

import gov.cdc.dataprocessing.constant.ComplexQueries;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.ObservationBase;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation_Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ObservationRepository  extends JpaRepository<ObservationBase, Long> {

    @Query(value = ComplexQueries.RETRIEVE_OBSERVATION_QUESTION_SQL, nativeQuery = true)
    Optional<Collection<Observation_Question>> retrieveObservationQuestion(Long targetActUid);
}
