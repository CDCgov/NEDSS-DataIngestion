package gov.cdc.nbs.mpidatasyncer.repository.mpi;

import gov.cdc.nbs.mpidatasyncer.entity.mpi.MPIPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MpiPersonRepository extends JpaRepository<MPIPerson, Long> {
}
