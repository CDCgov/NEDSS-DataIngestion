package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConditionCodeRepository extends JpaRepository<ConditionCode, String> {
    @Query("SELECT cc.progAreaCd AS key FROM ConditionCode cc WHERE cc.conditionCd = (SELECT lr.defaultConditionCd FROM LabResult lr WHERE lr.laboratoryId = :laboratoryId AND lr.labResultCd = :labResultCd)")
    Optional<List<String>> findConditionCodeByLabResultLabIdAndCd(
            @Param("laboratoryId") String laboratoryId,
            @Param("labResultCd") String labResultCd
    );

}
