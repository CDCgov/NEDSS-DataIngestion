package gov.cdc.dataprocessing.repository.nbs.odse.repos.organization;

import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.OrganizationHistId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationHist;
import org.springframework.data.jpa.repository.JpaRepository;



public interface OrganizationHistRepository extends JpaRepository<OrganizationHist, OrganizationHistId> {
}
