package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.BaseConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCodeWithPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository


public interface ConditionCodeRepository extends JpaRepository<BaseConditionCode, String> {
    @Query("SELECT cc.progAreaCd AS key FROM ConditionCode cc " +
            "WHERE cc.conditionCd = (SELECT lr.defaultConditionCd FROM LabResult lr WHERE lr.laboratoryId = :laboratoryId AND lr.labResultCd = :labResultCd)")
    Optional<List<String>> findConditionCodeByLabResultLabIdAndCd(
            @Param("laboratoryId") String laboratoryId,
            @Param("labResultCd") String labResultCd
    );

    @Query(value = "SELECT c.* FROM Condition_code c where c.coinfection_grp_cd is not null", nativeQuery = true)
    Optional<List<ConditionCode>> findCoInfectionConditionCode();


    @Query(value = "SELECT cc.* FROM Condition_code cc", nativeQuery = true)
    Optional<List<ConditionCode>> findAllConditionCode();


    /**
     *    public static final String PROGRAMAREACONDITIONSSQL =
     *        "SELECT c.condition_cd \"conditionCd\", " +
     *        " c.condition_short_nm \"conditionShortNm\",
     *        c.prog_area_cd \"stateProgAreaCode\", " +
     *        "p.prog_area_desc_txt \"stateProgAreaCdDesc\",
     *        c. investigation_form_cd \"investigationFormCd\"
     *        FROM " +
     *        NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..Condition_code c INNER JOIN " +
     *        NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..Program_area_code p ON c.prog_area_cd = p.prog_area_cd " +
     *        " and c.indent_level_nbr = ? and c.prog_area_cd IN ";
     *
     * */
    @Query(value = "SELECT c.*, " +
            "c.prog_area_cd AS stateProgAreaCode, " +
            "p.prog_area_desc_txt AS stateProgAreaCdDesc " +
            "FROM Condition_code c " +
            "INNER JOIN Program_area_code p " +
            "ON c.prog_area_cd = p.prog_area_cd " +
            "AND c.indent_level_nbr = :indentLevel " +
            "AND c.prog_area_cd IN :progAreaCodes", nativeQuery = true)
    Optional<List<ConditionCodeWithPA>> findProgramAreaConditionCode(@Param("indentLevel") Integer indentLevel, @Param("progAreaCodes") List<String> progAreaCodes);



    /**
     *    public static final String PROGRAMAREACONDITIONSSQLWOINDENT =
     *        "SELECT c.condition_cd \"conditionCd\", " + " c.condition_short_nm \"conditionShortNm\", c.prog_area_cd \"stateProgAreaCode\", " + "p.prog_area_desc_txt \"stateProgAreaCdDesc\", c. investigation_form_cd \"investigationFormCd\" FROM " +
     *        NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..Condition_code c INNER JOIN " +
     *        NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..Program_area_code p ON c.prog_area_cd = p.prog_area_cd " +
     *        " and c.condition_cd = ?";
     * */
    @Query(value = "SELECT c.*, " +
            "c.prog_area_cd AS stateProgAreaCode, " +
            "p.prog_area_desc_txt AS stateProgAreaCdDesc " +
            "FROM Condition_code c " +
            "INNER JOIN Program_area_code p " +
            "ON c.prog_area_cd = p.prog_area_cd " +
            "AND c.condition_cd = :condition_cd ", nativeQuery = true)
    Optional<List<ConditionCodeWithPA>> findProgramAreaConditionCodeByConditionCode(@Param("condition_cd") String condition_cd); // NOSONAR


    @Query(value = "SELECT cc.prog_area_cd  FROM Condition_code cc " +
            "WHERE cc.condition_cd =" +
            " (SELECT lt.default_condition_cd FROM Lab_test lt WHERE lt.laboratory_id = :laboratoryId AND lt.lab_test_cd = :labTestCd)", nativeQuery = true)
    Optional<List<String>> findLocalTestDefaultConditionProgramAreaCd(@Param("laboratoryId") String laboratoryId, @Param("labTestCd") String labTestCd);

}
