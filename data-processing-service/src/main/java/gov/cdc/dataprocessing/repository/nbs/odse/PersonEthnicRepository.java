package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonEthnicRepository extends JpaRepository<PersonEthnicGroup, Long> {
    @Query("SELECT pn FROM PersonEthnicGroup pn WHERE pn.personUid = :parentUid")
    Optional<List<PersonEthnicGroup>> findByParentUid(@Param("parentUid") Long parentUid);
}