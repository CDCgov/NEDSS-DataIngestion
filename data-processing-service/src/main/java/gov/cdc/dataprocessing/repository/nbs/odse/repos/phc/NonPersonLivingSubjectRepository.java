package gov.cdc.dataprocessing.repository.nbs.odse.repos.phc;

import gov.cdc.dataprocessing.repository.nbs.odse.model.phc.NonPersonLivingSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository


public interface NonPersonLivingSubjectRepository  extends JpaRepository<NonPersonLivingSubject, Long> {
}
