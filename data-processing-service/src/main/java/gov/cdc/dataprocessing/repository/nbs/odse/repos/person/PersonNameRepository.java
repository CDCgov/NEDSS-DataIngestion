package gov.cdc.dataprocessing.repository.nbs.odse.repos.person;

import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonName;
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
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740"})
public interface PersonNameRepository  extends JpaRepository<PersonName, Long> {
    @Query("SELECT pn FROM PersonName pn WHERE pn.personUid = :parentUid")
    Optional<List<PersonName>> findByParentUid(@Param("parentUid") Long parentUid);

    @Query(value = "SELECT * FROM Person_name WHERE person_uid = :parentUid ORDER BY person_name_seq desc", nativeQuery = true)
    List<PersonName> findBySeqIdByParentUid(@Param("parentUid") Long parentUid);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Person_name WHERE person_uid = :personUid AND person_name_seq = :personSeq",  nativeQuery = true)
    void deletePersonNameByIdAndSeq (@Param("personUid") Long personUid, @Param("personSeq") Integer personSeq);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Person_name SET status_cd = 'I' WHERE person_uid = :personUid AND person_name_seq = :personSeq",  nativeQuery = true)
    void updatePersonNameStatus (@Param("personUid") Long personUid, @Param("personSeq") Integer personSeq);
}