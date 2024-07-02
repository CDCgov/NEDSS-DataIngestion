package gov.cdc.dataprocessing.repository.nbs.odse.repos.person;

import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.PersonRaceId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonRace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRaceRepository extends JpaRepository<PersonRace, PersonRaceId> {
    @Query("SELECT pn FROM PersonRace pn WHERE pn.personUid = :parentUid")
    Optional<List<PersonRace>> findByParentUid(@Param("parentUid") Long parentUid);

    @Transactional
//    void deletePersonRaceByUidAndCode(PersonRaceId id);
    @Modifying
    @Query("DELETE FROM PersonRace pn WHERE pn.personUid = :personUid AND pn.raceCd = :raceCd")
    void deletePersonRaceByUidAndCode (@Param("personUid") Long personUid, @Param("raceCd") String raceCd);
}