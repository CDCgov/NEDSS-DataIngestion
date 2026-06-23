package gov.cdc.dataprocessing.repository.nbs.odse.repos.material;

import gov.cdc.dataprocessing.repository.nbs.odse.model.material.ManufacturedMaterial;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManufacturedMaterialRepository extends JpaRepository<ManufacturedMaterial, Long> {
  @Query("SELECT data FROM ManufacturedMaterial data WHERE data.materialUid = :parentUid")
  Optional<List<ManufacturedMaterial>> findByParentUid(@Param("parentUid") Long parentUid);
}
