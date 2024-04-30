package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.NbsCaseAnswer;
import gov.cdc.dataprocessing.repository.nbs.odse.model.NbsDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NbsDocumentRepository  extends JpaRepository<NbsDocument, Long> {
}
