package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.LOINCCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.SnomedCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LOINCCodeRepository  extends JpaRepository<LOINCCode, String> {

    @Query(value = "select * from LOINC_code where time_aspect = 'Pt' and system_cd = '^Patient'", nativeQuery = true)
    Optional<List<LOINCCode>> findLoincCodes();

    @Query("SELECT DISTINCT lcc.conditionCd FROM LOINCCode lc INNER JOIN LOINCCodeCondition lcc ON lc.loincCode = lcc.loincCd WHERE lc.loincCode = :loincCd")
    Optional<List<String>> findConditionForLoincCode(@Param("loincCd") String loincCd);

    @Query("SELECT lc FROM LOINCCode lc WHERE lc.loincCode = :loincCd")
    Optional<List<LOINCCode>> findLoinCCodeExclusion(@Param("loincCd") String loincCd);

}