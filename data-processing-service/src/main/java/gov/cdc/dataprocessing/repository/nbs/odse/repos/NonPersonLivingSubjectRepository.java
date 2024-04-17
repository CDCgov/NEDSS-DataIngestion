package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.NonPersonLivingSubject;
import gov.cdc.dataprocessing.repository.nbs.odse.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NonPersonLivingSubjectRepository  extends JpaRepository<NonPersonLivingSubject, Long> {
}
