package gov.cdc.nbsDedup.nbs.odse.repos.entity;

import gov.cdc.nbsDedup.nbs.odse.model.entity.EntityODSE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends JpaRepository<EntityODSE, Long> {
}
