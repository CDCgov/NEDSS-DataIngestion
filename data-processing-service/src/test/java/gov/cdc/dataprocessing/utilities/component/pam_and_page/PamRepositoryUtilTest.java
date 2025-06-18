package gov.cdc.dataprocessing.utilities.component.pam_and_page;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsActJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.jdbc_template.NbsCaseAnswerJdbcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsActEntity;
import gov.cdc.dataprocessing.repository.nbs.odse.model.nbs.NbsCaseAnswer;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.page_and_pam.PamRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
class PamRepositoryUtilTest {

    @Mock
    private NbsActJdbcRepository nbsActEntityRepository;

    @Mock
    private NbsCaseAnswerJdbcRepository nbsCaseAnswerRepository;

    @InjectMocks
    private PamRepositoryUtil pamRepositoryUtil;
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
        Mockito.reset(nbsActEntityRepository, nbsCaseAnswerRepository, authUtil);
    }

    @Test
    void getPamHistory_Test()  {
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto publicHealthCaseDto = new PublicHealthCaseDto();
        publicHealthCaseDto.setPublicHealthCaseUid(1L);
        publicHealthCaseContainer.setThePublicHealthCaseDto(publicHealthCaseDto);

        var actEnCol = new ArrayList<NbsActEntity>();
        var actEn = new NbsActEntity();
        actEnCol.add(actEn);
        when(nbsActEntityRepository.getNbsActEntitiesByActUid(1L)).thenReturn(actEnCol);

        var caseCol = new ArrayList<NbsCaseAnswer>();
        var cas = new NbsCaseAnswer();
        caseCol.add(cas);
        when(nbsCaseAnswerRepository.getNbsCaseAnswerByActUid(1L)).thenReturn(caseCol);

        var res = pamRepositoryUtil.getPamHistory(publicHealthCaseContainer);

        assertNotNull(res);
    }


    @Test
    void getPamHistory_Test_Exp_1()  {
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto publicHealthCaseDto = new PublicHealthCaseDto();
        publicHealthCaseDto.setPublicHealthCaseUid(1L);
        publicHealthCaseContainer.setThePublicHealthCaseDto(publicHealthCaseDto);

        when(nbsActEntityRepository.getNbsActEntitiesByActUid(1L)).thenThrow(new RuntimeException("TEST"));

        var caseCol = new ArrayList<NbsCaseAnswer>();
        var cas = new NbsCaseAnswer();
        caseCol.add(cas);
        when(nbsCaseAnswerRepository.getNbsCaseAnswerByActUid(1L)).thenReturn(caseCol);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            pamRepositoryUtil.getPamHistory(publicHealthCaseContainer);
        });

        assertNotNull(thrown);
    }

    @Test
    void getPamHistory_Test_Exp_2() {
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();
        PublicHealthCaseDto publicHealthCaseDto = new PublicHealthCaseDto();
        publicHealthCaseDto.setPublicHealthCaseUid(1L);
        publicHealthCaseContainer.setThePublicHealthCaseDto(publicHealthCaseDto);

        var actEnCol = new ArrayList<NbsActEntity>();
        var actEn = new NbsActEntity();
        actEnCol.add(actEn);
        when(nbsActEntityRepository.getNbsActEntitiesByActUid(1L)).thenReturn(actEnCol);

        when(nbsCaseAnswerRepository.getNbsCaseAnswerByActUid(1L)).thenThrow(new RuntimeException("TEST"));


        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            pamRepositoryUtil.getPamHistory(publicHealthCaseContainer);
        });

        assertNotNull(thrown);
    }


}