package gov.cdc.dataprocessing.repository.nbs.odse.repos.organization;

import gov.cdc.dataprocessing.repository.nbs.odse.model.id_class.OrganizationNameHistId;
import gov.cdc.dataprocessing.repository.nbs.odse.model.organization.OrganizationNameHist;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809"})
public interface OrganizationNameHistRepository extends JpaRepository<OrganizationNameHist, OrganizationNameHistId> {
}
