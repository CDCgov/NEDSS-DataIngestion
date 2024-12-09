package gov.cdc.srtedataservice.repository.nbs.srte.repository;

import gov.cdc.srtedataservice.repository.nbs.srte.model.CodeValueGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

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