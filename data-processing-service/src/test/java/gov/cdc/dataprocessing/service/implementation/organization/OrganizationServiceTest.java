package gov.cdc.dataprocessing.service.implementation.organization;

import gov.cdc.dataprocessing.constant.elr.EdxELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingConsumerException;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.log.EDXActivityDetailLogDto;
import gov.cdc.dataprocessing.service.interfaces.organization.IOrganizationMatchingService;
import gov.cdc.dataprocessing.service.interfaces.other.IUidService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class OrganizationServiceTest {
    @Mock
    private IOrganizationMatchingService iOrganizationMatchingServiceMock;

    @Mock
    private IUidService uidServiceMock;

    @InjectMocks
    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @AfterEach
    void tearDown() {
        Mockito.reset(iOrganizationMatchingServiceMock);
        Mockito.reset(uidServiceMock);
    }

    @Test
    void processingOrganization_with_role_sf() throws DataProcessingConsumerException {
        LabResultProxyContainer labResultProxyContainer= new LabResultProxyContainer();
        Collection<OrganizationContainer> theOrganizationContainerCollection= new ArrayList<>();
        OrganizationContainer organizationContainer= new OrganizationContainer();
        //set role
        organizationContainer.setRole(EdxELRConstant.ELR_SENDING_FACILITY_CD);
        labResultProxyContainer.setSendingFacilityUid(123L);

        theOrganizationContainerCollection.add(organizationContainer);
        labResultProxyContainer.setTheOrganizationContainerCollection(theOrganizationContainerCollection);

        OrganizationContainer organizationContainerResult = organizationService.processingOrganization(labResultProxyContainer);
        assertNull(organizationContainerResult);
    }

    @Test
    void processingOrganization_with_role_op() throws DataProcessingConsumerException, DataProcessingException {
        LabResultProxyContainer labResultProxyContainer= new LabResultProxyContainer();
        Collection<OrganizationContainer> theOrganizationContainerCollection= new ArrayList<>();
        OrganizationContainer organizationContainer= new OrganizationContainer();
        //set role
        organizationContainer.setRole(EdxELRConstant.ELR_OP_CD);

        theOrganizationContainerCollection.add(organizationContainer);
        labResultProxyContainer.setTheOrganizationContainerCollection(theOrganizationContainerCollection);

        EDXActivityDetailLogDto eDXActivityDetailLogDto= new EDXActivityDetailLogDto();
        eDXActivityDetailLogDto.setRecordId("123");
        when(iOrganizationMatchingServiceMock.getMatchingOrganization(organizationContainer)).thenReturn(eDXActivityDetailLogDto);

        OrganizationContainer organizationContainerResult = organizationService.processingOrganization(labResultProxyContainer);
        assertNotNull(organizationContainerResult);
    }
    @Test
    void processingOrganization_with_role_null() throws DataProcessingConsumerException, DataProcessingException {
        LabResultProxyContainer labResultProxyContainer= new LabResultProxyContainer();
        Collection<OrganizationContainer> theOrganizationContainerCollection= new ArrayList<>();
        OrganizationContainer organizationContainer= new OrganizationContainer();
        //set role
        organizationContainer.setRole(null);

        theOrganizationContainerCollection.add(organizationContainer);
        labResultProxyContainer.setTheOrganizationContainerCollection(theOrganizationContainerCollection);

        EDXActivityDetailLogDto eDXActivityDetailLogDto= new EDXActivityDetailLogDto();
        eDXActivityDetailLogDto.setRecordId("123");
        when(iOrganizationMatchingServiceMock.getMatchingOrganization(organizationContainer)).thenReturn(eDXActivityDetailLogDto);

        OrganizationContainer organizationContainerResult = organizationService.processingOrganization(labResultProxyContainer);
        assertNull(organizationContainerResult);
    }
}