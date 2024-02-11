package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonNameRepository  extends JpaRepository<PersonName, Long> {
    @Query("SELECT pn FROM PersonName pn WHERE pn.personUid = :parentUid")
    Optional<List<PersonName>> findByParentUid(@Param("parentUid") Long parentUid);
}