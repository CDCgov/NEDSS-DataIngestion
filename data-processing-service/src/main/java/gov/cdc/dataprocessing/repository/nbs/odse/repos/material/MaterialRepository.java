package gov.cdc.dataprocessing.repository.nbs.odse.repos.material;

import gov.cdc.dataprocessing.repository.nbs.odse.model.material.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
}
