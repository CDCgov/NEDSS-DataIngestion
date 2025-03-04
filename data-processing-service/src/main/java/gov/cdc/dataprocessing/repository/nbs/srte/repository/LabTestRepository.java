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
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
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
