package gov.cdc.nbsDedup.nbs.srte.repository;

import gov.cdc.nbsDedup.nbs.srte.model.StateCountyCodeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StateCountyCodeValueRepository extends JpaRepository<StateCountyCodeValue, String> {
    @Query(value = "SELECT * FROM State_county_code_value  WHERE INDENT_LEVEL_NBR='2' ", nativeQuery = true)
    Optional<List<StateCountyCodeValue>> findByIndentLevelNbr();
    @Query(value = "SELECT * FROM State_county_code_value WHERE INDENT_LEVEL_NBR = '2' AND parent_is_cd = ?1 ORDER BY code_desc_txt", nativeQuery = true)
    Optional<List<StateCountyCodeValue>> findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(String parentIsCd);
}
