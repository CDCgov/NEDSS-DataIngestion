package gov.cdc.dataprocessing.service.implementation.organization;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.matching.EdxEntityMatchDto;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.matching.EdxEntityMatchRepository;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.patient.EdxPatientMatchRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationMatchingServiceTest {
    @Mock
    private EdxPatientMatchRepositoryUtil edxPatientMatchRepositoryUtilMock;
    @Mock
    private EdxEntityMatchRepository edxEntityMatchRepositoryMock;
    @Mock
    private OrganizationRepositoryUtil organizationRepositoryUtilMock;

    @InjectMocks
    private OrganizationMatchingService organizationMatchingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(edxPatientMatchRepositoryUtilMock);
        Mockito.reset(organizationRepositoryUtilMock);
        Mockito.reset(organizationRepositoryUtilMock);
    }

    @Test
    void getMatchingOrganization() throws DataProcessingException {
        OrganizationContainer organizationContainer= new OrganizationContainer();
        organizationContainer.setLocalIdentifier("123");

        EdxEntityMatchDto edxEntityMatchDto = new EdxEntityMatchDto();

        //call test method
        organizationMatchingService.getMatchingOrganization(organizationContainer);
    }

    @Test
    void nameAddressStreetOne() {
    }
}