package gov.cdc.dataingestion.share.repository;

import gov.cdc.dataingestion.share.repository.model.ObxIdStdLookup;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IObxIdStdLookupRepository extends JpaRepository<ObxIdStdLookup, Long> {
  Optional<ObxIdStdLookup> findByObxValueTypeId(String obxValueTypeId);
}
