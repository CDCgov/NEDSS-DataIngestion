package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCountyCodeValue;
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
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107"})
public interface StateCountyCodeValueRepository extends JpaRepository<StateCountyCodeValue, String> {
    @Query(value = "SELECT * FROM State_county_code_value  WHERE INDENT_LEVEL_NBR='2' ", nativeQuery = true)
    Optional<List<StateCountyCodeValue>> findByIndentLevelNbr();
    @Query(value = "SELECT * FROM State_county_code_value WHERE INDENT_LEVEL_NBR = '2' AND parent_is_cd = ?1 ORDER BY code_desc_txt", nativeQuery = true)
    Optional<List<StateCountyCodeValue>> findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(String parentIsCd);
}