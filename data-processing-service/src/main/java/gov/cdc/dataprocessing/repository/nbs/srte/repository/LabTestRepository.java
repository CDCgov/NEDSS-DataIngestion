package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.LOINCCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.LabResult;
import gov.cdc.dataprocessing.repository.nbs.srte.model.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, String> {
    @Query(value = "SELECT lr.defaultConditionCd FROM LabTest lr WHERE lr.laboratoryId = :laboratoryId AND lr.labTestCd = :labTestCd")
    Optional<List<String>> findDefaultConditionForLabTest(@Param("laboratoryId") String laboratoryId, @Param("labTestCd") String labTestCd);


    @Query("SELECT lc FROM LabTest lc WHERE lc.labTestCd = :labTestCd AND lc.laboratoryId = :reportingLabCLIA")
    Optional<List<LabTest>> findLabTestForExclusion(@Param("labTestCd") String labTestCd, @Param("reportingLabCLIA") String reportingLabCLIA);


    @Query("SELECT cc.progAreaCd AS key FROM ConditionCode cc WHERE cc.conditionCd = (SELECT lt.defaultConditionCd FROM LabTest lt WHERE lt.laboratoryId = :laboratoryId AND lt.labTestCd = :labTestCd)")
    Optional<List<String>> findLocalTestDefaultConditionProgramAreaCd(@Param("laboratoryId") String laboratoryId, @Param("labTestCd") String labTestCd);

    @Query("SELECT lt.defaultProgAreaCd AS key FROM LabTest lt WHERE lt.laboratoryId = :laboratoryId AND lt.labTestCd = :labTestCd")
    Optional<List<String>>  findLocalTestDefaultProgramAreaCd(@Param("laboratoryId") String laboratoryId, @Param("labTestCd") String labTestCd);

    @Query("SELECT lt FROM LabTest lt WHERE lt.laboratoryId = :laboratoryId AND lt.labTestCd = :labTestCd")
    Optional<List<LabTest>>  findLabTestByLabIdAndLabTestCode(@Param("laboratoryId") String laboratoryId, @Param("labTestCd") String labTestCd);
}
