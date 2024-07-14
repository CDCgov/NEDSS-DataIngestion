package gov.cdc.dataprocessing.service.implementation.page_and_pam;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.PageActProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.CaseManagementDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.interfaces.public_health_case.IInvestigationService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.page_and_pam.PageRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PageServiceTests {
    @Mock
    AuthUtil authUtil;
    @Mock
    private IInvestigationService investigationService;
    @Mock
    private PageRepositoryUtil pageRepositoryUtil;
    @InjectMocks
    private PageService pageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        AuthUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(investigationService, pageRepositoryUtil, authUtil);
    }


    @Test
    void setPageProxyWithAutoAssoc_Success() throws DataProcessingException {
        String typeCd = NEDSSConstant.CASE;
        PageActProxyContainer pageProxyVO = new PageActProxyContainer();
        Long observationUid = 10L;
        String observationTypeCd = NEDSSConstant.LAB_DISPALY_FORM;
        String processingDecision = "CODE";

        var phcCon = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        var caseDt = new CaseManagementDto();
        caseDt.setInitFollUp("Y");
        phcCon.setTheCaseManagementDto(caseDt);
        phcCon.setThePublicHealthCaseDto(phcDt);
        pageProxyVO.setPublicHealthCaseContainer(phcCon);

        when(pageRepositoryUtil.setPageActProxyVO(any())).thenReturn(11L);

        var test = pageService.setPageProxyWithAutoAssoc(typeCd, pageProxyVO, observationUid, observationTypeCd, processingDecision);

        assertNotNull(test);
        assertEquals(11L, test);

    }


    @Test
    void setPageProxyWithAutoAssoc_Success_2() throws DataProcessingException {
        String typeCd = NEDSSConstant.CASE;
        PageActProxyContainer pageProxyVO = new PageActProxyContainer();
        Long observationUid = 10L;
        String observationTypeCd = NEDSSConstant.LAB_DISPALY_FORM;
        String processingDecision = "CODE";

        var phcCon = new PublicHealthCaseContainer();
        var phcDt = new PublicHealthCaseDto();
        phcCon.setTheCaseManagementDto(null);
        phcCon.setThePublicHealthCaseDto(phcDt);
        pageProxyVO.setPublicHealthCaseContainer(phcCon);

        when(pageRepositoryUtil.setPageActProxyVO(any())).thenReturn(11L);

        var test = pageService.setPageProxyWithAutoAssoc(typeCd, pageProxyVO, observationUid, observationTypeCd, processingDecision);

        assertNotNull(test);
        assertEquals(11L, test);

    }
}
