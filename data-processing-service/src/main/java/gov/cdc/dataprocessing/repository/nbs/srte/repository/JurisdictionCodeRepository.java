package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.JurisdictionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository


public interface JurisdictionCodeRepository extends JpaRepository<JurisdictionCode, String> {

    /**
     *    public static final String JURISDICTION_CODED_VALUES_SQL =
     *        "SELECT code \"key\" , " + " code_desc_txt \"value\" , export_ind \"altValue\" FROM " +
     *        NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..Jurisdiction_code order by code_desc_txt";
     * */
    @Query(value = "SELECT * FROM JurisdictionCode ORDER BY code_desc_txt", nativeQuery = true)
    Optional<List<JurisdictionCode>> findJurisdictionCodeValues();
}