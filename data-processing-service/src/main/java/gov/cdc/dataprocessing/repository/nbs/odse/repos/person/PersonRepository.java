package gov.cdc.dataprocessing.repository.nbs.odse.repos.person;

import gov.cdc.dataprocessing.repository.nbs.odse.model.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public interface PersonRepository  extends JpaRepository<Person, Long> {
    @Query("SELECT pn FROM Person pn WHERE pn.personParentUid = :parentUid")
    Optional<List<Person>> findByParentUid(@Param("parentUid") Long parentUid);

    @Transactional
    @Modifying
    @Query("UPDATE Person p SET p.edxInd = 'Y' WHERE p.personUid = :uid")
    Integer updateExistingPersonEdxIndByUid(@Param("uid") Long uid);

    /**
     *
     * String PATIENTPARENTUID_BY_UID = " SELECT p.person_parent_uid \"personParentUid\"
     * FROM person p with (nolock)  where p.person_uid = ? AND p.record_status_cd = 'ACTIVE' "
     * */
    @Query("SELECT pn.personParentUid FROM Person pn WHERE pn.personUid = :parentUid AND pn.recordStatusCd='ACTIVE' ")
    Optional<List<Long>> findPatientParentUidByUid(@Param("parentUid") Long parentUid);

    @Query("SELECT pn FROM Person pn WHERE pn.personUid = :personUid")
    Optional<List<Person>> findByPersonUid(@Param("personUid") Long personUid);

}