package gov.cdc.dataprocessing.repository.nbs.odse.repos.person;

import gov.cdc.dataprocessing.repository.nbs.odse.model.person.PersonEthnicGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonEthnicRepository extends JpaRepository<PersonEthnicGroup, Long> {
  @Query("SELECT pn FROM PersonEthnicGroup pn WHERE pn.personUid = :parentUid")
  Optional<List<PersonEthnicGroup>> findByParentUid(@Param("parentUid") Long parentUid);
}
