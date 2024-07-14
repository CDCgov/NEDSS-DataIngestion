package gov.cdc.dataprocessing.repository.nbs.odse.repos.organization;

import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

}