package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.NbsDocument;
import gov.cdc.dataprocessing.repository.nbs.odse.model.NbsDocumentHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NbsDocumentHistRepository extends JpaRepository<NbsDocumentHist, Long> {
}
