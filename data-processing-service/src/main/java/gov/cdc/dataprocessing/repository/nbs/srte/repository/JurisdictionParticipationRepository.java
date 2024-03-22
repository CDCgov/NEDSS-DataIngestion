package gov.cdc.dataprocessing.repository.nbs.srte.repository;

import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.act.ActRelationship;
import gov.cdc.dataprocessing.repository.nbs.srte.model.JurisdictionParticipation;
import gov.cdc.dataprocessing.repository.nbs.srte.model.id_class.JurisdictionParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface JurisdictionParticipationRepository extends JpaRepository<JurisdictionParticipation, JurisdictionParticipationId> {


    /**
     * 		public static final String JURISDICTION_SELECT_SQL = "Select b.jurisdiction_cd \"key\", 1 \"value\" " +
     * 			"from " + NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..jurisdiction_code a, " +
     * 			NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..jurisdiction_participation b " +
     * 			"where a.code = b.jurisdiction_cd " +
     * 			"and (b.fips_cd = ?) " +
     * 			"and (b.type_cd = ?) ";
     * */
    @Query(value = "SELECT b.jurisdiction_cd " +
            "FROM Jurisdiction_Participation b " +
            "JOIN Jurisdiction_Code a  " +
            "ON b.jurisdiction_cd = a.code " +
            "WHERE b.fips_cd = :fipsCd " +
            "AND b.type_cd = :typeCd ",  nativeQuery = true)
    Optional<Collection<String>> findJurisdiction(@Param("fipsCd") String flipsCode, @Param("typeCd") String typeCode);


    /**
     * public static final String JURISDICTION_CITY_SELECT_SQL =
     *                         "select a.jurisdiction_cd \"key\", 1 \"value\" " +
     *                         "from " + NEDSSConstants.SYSTEM_REFERENCE_TABLE +
     *                         "..jurisdiction_participation a, " +
     *                         NEDSSConstants.SYSTEM_REFERENCE_TABLE + "..city_code_value b " +
     *                         "where a.fips_cd= b.code " +
     *                         "and a.type_cd = ? " +
     *                         "and substring(b.code_desc_txt, 0, { fn LENGTH(b.code_desc_txt) }- 3) =  ? " +
     *                         "and b.parent_is_cd = ? ";
     * */
    @Query(value = "SELECT a.jurisdiction_cd " +
            "FROM Jurisdiction_Participation a  " +
            "JOIN City_Code_Value b " +
            "ON a.fips_cd = b.code " +
            "WHERE a.type_cd = :typeCd " +
            "AND SUBSTRING(b.code_desc_txt, 1, LEN(b.code_desc_txt) - 3) = :substring " +
            "AND b.parent_is_cd = :parentIsCd ", nativeQuery = true)
    Optional<Collection<String>> findJurisdictionForCity(@Param("typeCd") String typeCd,
                                                   @Param("substring") String substring,
                                                   @Param("parentIsCd") String parentIsCd);


}
