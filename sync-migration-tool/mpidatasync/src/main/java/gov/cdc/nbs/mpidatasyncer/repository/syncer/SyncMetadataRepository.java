package gov.cdc.nbs.mpidatasyncer.repository.syncer;

import gov.cdc.nbs.mpidatasyncer.entity.syncer.SyncMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SyncMetadataRepository extends JpaRepository<SyncMetadata, Long> {
  Optional<SyncMetadata> findFirstByOrderByIdAsc();
}
