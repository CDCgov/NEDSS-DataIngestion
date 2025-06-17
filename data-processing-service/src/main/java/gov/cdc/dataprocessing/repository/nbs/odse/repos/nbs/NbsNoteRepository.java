package gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository


public interface NbsNoteRepository extends JpaRepository<NbsNote, Long> {
}
