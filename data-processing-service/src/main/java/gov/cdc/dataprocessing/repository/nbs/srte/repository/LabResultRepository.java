package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.LabResult;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public interface LabResultRepository  extends JpaRepository<LabResult, String> {
    @Query(value = "SELECT lr.defaultConditionCd FROM LabResult lr WHERE lr.labResultCd = :labResultCd AND lr.laboratoryId = :laboratoryId")
    Optional<List<String>> findDefaultConditionCdByLabResultCdAndLaboratoryId(@Param("labResultCd") String labResultCd, @Param("laboratoryId") String laboratoryId);

    @Query("SELECT lr FROM LabResult lr WHERE lr.labResultCd = :labResultCd AND lr.laboratoryId = :laboratoryId")
    Optional<List<LabResult>> findLabResultProgramAreaExclusion(@Param("labResultCd") String labResultCd, @Param("laboratoryId") String laboratoryId);

    @Query("SELECT lr.defaultProgAreaCd AS key FROM LabResult lr WHERE lr.laboratoryId = :laboratoryId AND lr.labResultCd = :labResultCd")
    Optional<List<String>> findLocalResultDefaultProgramAreaCd(@Param("laboratoryId") String laboratoryId, @Param("labResultCd") String labResultCd);


    /**
     *    public static final String CODED_RESULT_VALUES_SQL =
     *     "SELECT lab_result_cd \"key\" , " + "lab_result_desc_txt \"value\" FROM " +
     *     NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..lab_result where ORGANISM_NAME_IND = 'N' AND LABORATORY_ID = 'DEFAULT'";
     * */
    @Query("SELECT lr FROM LabResult lr WHERE lr.laboratoryId = 'DEFAULT' AND lr.organismNameInd = 'N'")
    Optional<List<LabResult>> findLabResultByDefaultLabAndOrgNameN();

}
