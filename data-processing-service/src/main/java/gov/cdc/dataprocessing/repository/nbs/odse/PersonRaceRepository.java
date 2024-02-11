package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonRace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRaceRepository extends JpaRepository<PersonRace, Long> {
    @Query("SELECT pn FROM PersonRace pn WHERE pn.personUid = :parentUid")
    Optional<List<PersonRace>> findByParentUid(@Param("parentUid") Long parentUid);
}