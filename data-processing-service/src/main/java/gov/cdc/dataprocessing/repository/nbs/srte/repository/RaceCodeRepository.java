package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.RaceCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public interface RaceCodeRepository extends JpaRepository<RaceCode, String> {

    /**
     *    public static final String RACESQL = "select  code \"key\", " +
     *                                         " code_short_desc_txt \"value\"
     *                                         from " +
     *                                         NEDSSConstants.SYSTEM_REFERENCE_TABLE +
     *                                         "..race_code " ;
     * */
    @Query(value = "SELECT * FROM race_code WHERE status_cd = 'A'", nativeQuery = true)
    Optional<List<RaceCode>> findAllActiveRaceCodes();
}