package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonEthnicGroup;
import gov.cdc.dataprocessing.repository.nbs.odse.model.PersonName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface PersonEthnicRepository extends JpaRepository<PersonEthnicGroup, BigInteger> {
}