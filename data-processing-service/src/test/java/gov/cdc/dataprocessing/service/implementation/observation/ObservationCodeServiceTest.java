package gov.cdc.dataprocessing.service.implementation.observation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.OrganizationContainer;
import gov.cdc.dataprocessing.model.dto.entity.EntityIdDto;
import gov.cdc.dataprocessing.model.dto.organization.OrganizationDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.stored_proc.ObservationMatchStoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.observation.ObservationRepository;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ISrteCodeObsService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.test_data.TestData;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.observation.ObservationUtil;
import gov.cdc.dataprocessing.utilities.component.organization.OrganizationRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class ObservationCodeServiceTest {
    @Mock
    private ISrteCodeObsService srteCodeObsService;
    @Mock
    private OrganizationRepositoryUtil organizationRepositoryUtil;
    @Mock
    private ObservationUtil observationUtil;
    @InjectMocks
    private ObservationCodeService observationCodeService;
    @Mock
    AuthUtil authUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(srteCodeObsService, organizationRepositoryUtil, observationUtil, authUtil);
    }

    @Test
    void deriveTheConditionCodeList_Success() {

    }

    @Test
    void getReportingLabCLIA_Success() throws DataProcessingException {
        TestData.createLabResultContainer();
        LabResultProxyContainer labResultProxyContainer = TestData.labResultProxyContainer;

        when(observationUtil.getUid(labResultProxyContainer.getTheParticipationDtoCollection(),
                null,
                NEDSSConstant.ENTITY_UID_LIST_TYPE,
                NEDSSConstant.ORGANIZATION,
                NEDSSConstant.PAR111_TYP_CD,
                NEDSSConstant.PART_ACT_CLASS_CD,
                NEDSSConstant.RECORD_STATUS_ACTIVE)).thenReturn(10L);

        var orgCon = new OrganizationContainer();
        var orgDto = new OrganizationDto();
        orgDto.setOrganizationUid(10L);

        var entityIdCol = new ArrayList<EntityIdDto>();
        var entityId = new EntityIdDto();
        entityId.setAssigningAuthorityCd(NEDSSConstant.REPORTING_LAB_CLIA);
        entityId.setTypeCd(NEDSSConstant.REPORTING_LAB_FI_TYPE);
        entityId.setRootExtensionTxt("TEST");
        entityIdCol.add(entityId);
        orgCon.setTheEntityIdDtoCollection(entityIdCol);
        orgCon.setTheOrganizationDto(orgDto);
        when(organizationRepositoryUtil.loadObject(10L, null)).thenReturn(orgCon);


        var test = observationCodeService.getReportingLabCLIA(labResultProxyContainer);

        assertNotNull(test);
        assertEquals("TEST", test);

    }
}
