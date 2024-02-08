package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.EntityId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.EntityLocatorParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface EntityLocatorParticipationRepository extends JpaRepository<EntityLocatorParticipation, BigInteger> {
}