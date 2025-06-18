package gov.cdc.dataprocessing.repository.nbs.odse.repos.organization;

import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.OrganizationNameId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository


public interface OrganizationNameRepository extends JpaRepository<OrganizationName, OrganizationNameId> {
    Optional<List<OrganizationName>> findByOrganizationUid(long organizationUID);
}
