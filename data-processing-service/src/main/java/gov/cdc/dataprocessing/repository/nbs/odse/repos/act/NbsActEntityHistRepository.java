package gov.cdc.dataprocessing.repository.nbs.odse.repos.act;

import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntityHist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NbsActEntityHistRepository extends JpaRepository<NbsActEntityHist, Long> {
}
