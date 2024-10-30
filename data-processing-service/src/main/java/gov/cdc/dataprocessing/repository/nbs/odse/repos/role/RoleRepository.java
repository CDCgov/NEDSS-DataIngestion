package gov.cdc.dataprocessing.repository.nbs.odse.repos.role;

import gov.cdc.dataprocessing.repository.nbs.odse.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("SELECT pn FROM Role pn WHERE pn.subjectEntityUid = :parentUid")
    Optional<List<Role>> findByParentUid(@Param("parentUid") Long parentUid);

    @Query("SELECT data FROM Role data WHERE data.scopingEntityUid = :entityUid AND data.scopingClassCode = 'PAT' AND data.subjectClassCode IN ('PROV', 'CON')")
    Optional<Collection<Role>> findRoleScopedToPatient(@Param("entityUid") Long entityUid);

    /**
     * String SEARCH_BY_PK = "SELECT count(*) from Role with (NOLOCK) where subject_entity_uid = ? and cd = ? and role_seq = ?"
     * */
    @Query("SELECT pn FROM Role pn WHERE pn.subjectEntityUid = :entityUid AND pn.code = :code AND pn.roleSeq = :seq")
    Optional<Long> countByPk(Long entityUid, String code, Long seq);

    /**
     *   private static final String SELECT_BY_PK = "SELECT max(role_seq) from ROLE with (NOLOCK) where subject_entity_uid = ? and cd = ?";
     * */
    @Query(value = "SELECT max(p.roleSeq) FROM Role p WHERE p.subjectEntityUid = :subjectEntityUid AND p.code = :code")
    Optional<Integer> loadCountBySubjectCdComb(@Param("subjectEntityUid") Long subjectEntityUid, @Param("code") String code);


    /**
     * String SELECT_BY_SUBJECT_SCOPING_CD = "SELECT max(role_seq) from ROLE with (NOLOCK)  where subject_entity_uid = ? and cd = ? and scoping_entity_uid=?"
     * */
    @Query(value = "SELECT max(p.roleSeq) FROM Role p WHERE p.subjectEntityUid = :subjectEntityUid AND p.code = :code AND p.scopingEntityUid = :scopingEntityUid")
    Optional<Integer> loadCountBySubjectScpingCdComb(@Param("subjectEntityUid") Long subjectEntityUid, @Param("code") String code, @Param("scopingEntityUid") Long scopingEntityUid);



    /**
     * String DELETE_BY_PK = "DELETE from Role where subject_entity_uid = ? and cd = ? and role_seq = ?"
     * */
    @Modifying
    @Query("DELETE FROM Role data WHERE data.subjectEntityUid = ?1 AND data.code = ?2 AND data.roleSeq = ?3")
    void deleteRoleByPk(Long subjectEntityUid, String code, Long roleSeq);


    @Query("SELECT rl FROM Role rl WHERE rl.subjectEntityUid = :subjectEntityUid AND rl.statusCode='A'")
    Optional<List<Role>> findBySubjectEntityUid(@Param("subjectEntityUid") Long subjectEntityUid);
}