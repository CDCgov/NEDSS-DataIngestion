package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201"})
public interface ElrXrefRepository extends JpaRepository<ElrXref, String> {

    /**
     *   private static final String ELR_CODE_X_REF_SQL = "SELECT to_code FROM "+NEDSSConstants.SYSTEM_REFERENCE_TABLE+"..elr_xref WHERE from_code_set_nm = ? "+
     *   "and from_code = ? and to_code_set_nm = ?";
     * */
    @Query(value = "SELECT * FROM elr_xref WHERE from_code_set_nm = ?1 " +
            "and from_code = ?2 and to_code_set_nm = ?3", nativeQuery = true)
    Optional<ElrXref> findToCodeByConditions(String fromCodeSetNm, String fromCode, String toCodeSetNm);
}