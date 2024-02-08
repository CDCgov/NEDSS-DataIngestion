package gov.cdc.dataprocessing.repository.nbs.odse;

import gov.cdc.dataprocessing.repository.nbs.odse.model.EntityLocatorParticipation;
import gov.cdc.dataprocessing.repository.nbs.odse.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}