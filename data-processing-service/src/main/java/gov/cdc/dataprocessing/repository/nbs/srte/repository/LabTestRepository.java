package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.LabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
public interface LabTestRepository extends JpaRepository<LabTest, String> {
    @Query(value = "SELECT lr.defaultConditionCd FROM LabTest lr WHERE lr.laboratoryId = :laboratoryId AND lr.labTestCd = :labTestCd")
    Optional<List<String>> findDefaultConditionForLabTest(@Param("laboratoryId") String laboratoryId, @Param("labTestCd") String labTestCd);


    @Query("SELECT lc FROM LabTest lc WHERE lc.labTestCd = :labTestCd AND lc.laboratoryId = :reportingLabCLIA")
    Optional<List<LabTest>> findLabTestForExclusion(@Param("labTestCd") String labTestCd, @Param("reportingLabCLIA") String reportingLabCLIA);


    @Query("SELECT lt.defaultProgAreaCd AS key FROM LabTest lt WHERE lt.laboratoryId = :laboratoryId AND lt.labTestCd = :labTestCd")
    Optional<List<String>>  findLocalTestDefaultProgramAreaCd(@Param("laboratoryId") String laboratoryId, @Param("labTestCd") String labTestCd);

    @Query("SELECT lt FROM LabTest lt WHERE lt.laboratoryId = :laboratoryId AND lt.labTestCd = :labTestCd")
    Optional<List<LabTest>>  findLabTestByLabIdAndLabTestCode(@Param("laboratoryId") String laboratoryId, @Param("labTestCd") String labTestCd);
}
