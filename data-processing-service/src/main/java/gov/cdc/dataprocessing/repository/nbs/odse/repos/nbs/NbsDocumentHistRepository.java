package gov.cdc.dataprocessing.repository.nbs.odse.repos.nbs;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsDocumentHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NbsDocumentHistRepository extends JpaRepository<NbsDocumentHist, Long> {
}
