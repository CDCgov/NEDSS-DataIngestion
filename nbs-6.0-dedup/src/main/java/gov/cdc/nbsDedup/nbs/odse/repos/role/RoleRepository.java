package gov.cdc.nbsDedup.nbs.odse.repos.role;


import gov.cdc.nbsDedup.nbs.odse.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
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
