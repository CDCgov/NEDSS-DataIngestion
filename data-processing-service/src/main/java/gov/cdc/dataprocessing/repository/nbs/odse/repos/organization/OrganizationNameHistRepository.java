package gov.cdc.dataprocessing.repository.nbs.odse.repos.organization;

import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationNameHist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationNameHistRepository extends JpaRepository<OrganizationNameHist, Long> {
}
