package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonName;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonRace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PersonRaceRepository extends JpaRepository<PersonRace, BigInteger> {
}