package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192"})
public interface CodeValueGeneralRepository extends JpaRepository<CodeValueGeneral, String> {

    Optional<CodeValueGeneral> findByCodeSetNmAndCode(String codeSetNm, String code);

    /**
     *    public static final String CODEQUERYSQL_FOR_DESCRIPTION_TXT =
     *          "Select code \"key\", " +
     *          "code_desc_txt \"value\"
     *          from " +
     *          "code_Value_General
     *          where upper(code_set_nm) = ? ";
     * */
    @Query(value = "SELECT * FROM Code_value_general WHERE UPPER(code_set_nm) = ?1", nativeQuery = true)
    Optional<List<CodeValueGeneral>> findCodeDescriptionsByCodeSetNm(String codeSetNm);

    /**
     * public static final String CODEQUERYSQL =
     *          "Select code \"key\", " +
     *          "code_short_desc_txt \"value\",
     *          concept_code \"altValue\" ,
     *          status_cd \"statusCd\",
     *          effective_to_time \"effectiveToTime\"
     *          from " +
     *          "..code_Value_General
     *          where upper(code_set_nm) = ?
     *          order by concept_order_nbr, code_short_desc_txt ";
     * */
    @Query(value = "SELECT * FROM Code_value_general WHERE UPPER(code_set_nm) = ?1 ORDER BY concept_order_nbr, code_short_desc_txt", nativeQuery = true)
    Optional<List<CodeValueGeneral>> findCodeValuesByCodeSetNm(String codeSetNm);


    /**
     * 	private static String SELECT_SRTCODE_INFO_SQL =
     * 	    "SELECT concept_preferred_nm \"codedValueDescription\",
     * 	    code_system_cd \"codedValueCodingSystem\",
     * 	    concept_code \"codedValue\"
     * 	    FROM "+
     * 			NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..";
     * 		WHERE code_set_nm = ? AND code = ?
     * */
    @Query(value = "SELECT * FROM Code_value_general WHERE code_set_nm = ?1 AND code = ?2", nativeQuery = true)
    Optional<List<CodeValueGeneral>> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code);
}