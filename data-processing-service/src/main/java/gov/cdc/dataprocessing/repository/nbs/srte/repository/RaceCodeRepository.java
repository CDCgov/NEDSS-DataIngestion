package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.RaceCode;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RaceCodeRepository extends JpaRepository<RaceCode, String> {

  /**
   * public static final String RACESQL = "select code \"key\", " + " code_short_desc_txt \"value\"
   * from " + NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..race_code " ;
   */
  @Query(value = "SELECT * FROM race_code WHERE status_cd = 'A'", nativeQuery = true)
  Optional<List<RaceCode>> findAllActiveRaceCodes();
}
