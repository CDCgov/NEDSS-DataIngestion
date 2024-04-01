package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.LOINCCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabResultRepository  extends JpaRepository<LabResult, String> {
    @Query(value = "SELECT lr.defaultConditionCd FROM LabResult lr WHERE lr.labResultCd = :labResultCd AND lr.laboratoryId = :laboratoryId")
    Optional<List<String>> findDefaultConditionCdByLabResultCdAndLaboratoryId(@Param("labResultCd") String labResultCd, @Param("laboratoryId") String laboratoryId);

    @Query("SELECT lr FROM LabResult lr WHERE lr.labResultCd = :labResultCd AND lr.laboratoryId = :laboratoryId")
    Optional<List<LabResult>> findLabResultProgramAreaExclusion(@Param("labResultCd") String labResultCd, @Param("laboratoryId") String laboratoryId);

    @Query("SELECT lr.defaultProgAreaCd AS key FROM LabResult lr WHERE lr.laboratoryId = :laboratoryId AND lr.labResultCd = :labResultCd")
    Optional<List<String>> findLocalResultDefaultProgramAreaCd(@Param("laboratoryId") String laboratoryId, @Param("labResultCd") String labResultCd);
}
