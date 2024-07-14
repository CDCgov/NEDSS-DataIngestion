package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface ElrXrefRepository extends JpaRepository<ElrXref, String> {

    /**
     * private static final String ELR_CODE_X_REF_SQL = "SELECT to_code FROM "+NEDSSConstants.SYSTEM_REFERENCE_TABLE+"..elr_xref WHERE from_code_set_nm = ? "+
     * "and from_code = ? and to_code_set_nm = ?";
     */
    @Query(value = "SELECT * FROM elr_xref WHERE from_code_set_nm = ?1 " +
            "and from_code = ?2 and to_code_set_nm = ?3", nativeQuery = true)
    Optional<ElrXref> findToCodeByConditions(String fromCodeSetNm, String fromCode, String toCodeSetNm);
}