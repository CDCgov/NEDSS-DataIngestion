package gov.cdc.nbs.mpidatasyncer.repository.mpi;

import gov.cdc.nbs.mpidatasyncer.entity.mpi.MPIPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MpiPatientRepository extends JpaRepository<MPIPatient, Long> {
  Optional<MPIPatient> findByExternalPatientId(String externalPatientId);
  Optional<MPIPatient> findByExternalPatientIdAndExternalPersonId(String externalPatientId,String externalPersonId);
}
