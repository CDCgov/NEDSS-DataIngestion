package gov.cdc.dataprocessing.repository.nbs.odse.repos.edx;

import gov.cdc.dataprocessing.repository.nbs.odse.model.edx.EdxEventProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EdxEventProcessRepository extends JpaRepository<EdxEventProcess, Long> {
}
