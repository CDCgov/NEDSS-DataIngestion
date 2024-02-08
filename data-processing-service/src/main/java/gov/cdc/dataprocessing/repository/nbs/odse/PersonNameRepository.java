package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.Person;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonNameRepository  extends JpaRepository<PersonName, Long> {
}