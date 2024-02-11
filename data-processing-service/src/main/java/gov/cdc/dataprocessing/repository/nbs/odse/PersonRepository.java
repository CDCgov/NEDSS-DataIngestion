package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository  extends JpaRepository<Person, Long> {
    @Query("SELECT pn FROM Person pn WHERE pn.personParentUid = :parentUid")
    Optional<List<Person>> findByParentUid(@Param("parentUid") Long parentUid);
}