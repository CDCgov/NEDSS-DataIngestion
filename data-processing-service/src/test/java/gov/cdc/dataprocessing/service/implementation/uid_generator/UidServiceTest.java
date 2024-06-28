package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.entity.RoleDto;
import gov.cdc.dataprocessing.model.dto.nbs.NbsActEntityDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

class UidServiceTest {
    @InjectMocks
    private UidService uidService;
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
        Mockito.reset(authUtil);
    }

    @Test
    void setFalseToNewForObservation_Test() {
        var proxyVO = new LabResultProxyContainer();

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setActUid(-1L);
        pat.setSubjectEntityUid(-1L);
        patCol.add(pat);
        proxyVO.setTheParticipationDtoCollection(patCol);

        var actCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        act.setTargetActUid(-1L);
        act.setSourceActUid(-1L);
        actCol.add(act);
        proxyVO.setTheActRelationshipDtoCollection(actCol);

        var roleCol = new ArrayList<RoleDto>();
        var role = new RoleDto();
        role.setSubjectEntityUid(-1L);
        role.setScopingEntityUid(-1L);
        roleCol.add(role);
        proxyVO.setTheRoleDtoCollection(roleCol);

        Long falseUid = -1L;
        Long actualUid = 1L;

        uidService.setFalseToNewForObservation(proxyVO, falseUid, actualUid);
    }

    @Test
    void setFalseToNewPersonAndOrganization_Test() {
        var proxyVO = new LabResultProxyContainer();

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setActUid(-1L);
        pat.setSubjectEntityUid(-1L);
        patCol.add(pat);
        proxyVO.setTheParticipationDtoCollection(patCol);

        var actCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        act.setTargetActUid(-1L);
        act.setSourceActUid(-1L);
        actCol.add(act);
        proxyVO.setTheActRelationshipDtoCollection(actCol);

        var roleCol = new ArrayList<RoleDto>();
        var role = new RoleDto();
        role.setSubjectEntityUid(-1L);
        role.setScopingEntityUid(-1L);
        roleCol.add(role);
        proxyVO.setTheRoleDtoCollection(roleCol);

        Long falseUid = -1L;
        Long actualUid = 1L;

        uidService.setFalseToNewPersonAndOrganization(proxyVO, falseUid, actualUid);
    }

    @Test
    void setFalseToNewForPageAct_Test() throws DataProcessingException {
        var proxyVO = new PageActProxyContainer();

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setActUid(-1L);
        pat.setSubjectEntityUid(-1L);
        patCol.add(pat);
        proxyVO.setTheParticipationDtoCollection(patCol);

        var phcConn = new PublicHealthCaseContainer();
        var actCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        act.setTargetActUid(-1L);
        act.setSourceActUid(-1L);
        actCol.add(act);
        phcConn.setTheActRelationshipDTCollection(actCol);
        proxyVO.setPublicHealthCaseContainer(phcConn);

        var actEntityCol = new ArrayList<NbsActEntityDto>();
        var actEntity = new NbsActEntityDto();
        actEntity.setEntityUid(-1L);
        actEntityCol.add(actEntity);
        var base = new BasePamContainer();
        base.setActEntityDTCollection(actEntityCol);
        proxyVO.setPageVO(base);


        Long falseUid = -1L;
        Long actualUid = 1L;

        uidService.setFalseToNewForPageAct(proxyVO, falseUid, actualUid);
    }

    @Test
    void setFalseToNewForPam_Test() throws DataProcessingException {
        var proxyVO = new PamProxyContainer();

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        pat.setActUid(-1L);
        pat.setSubjectEntityUid(-1L);
        patCol.add(pat);
        proxyVO.setTheParticipationDTCollection(patCol);

        var phcConn = new PublicHealthCaseContainer();
        var actCol = new ArrayList<ActRelationshipDto>();
        var act = new ActRelationshipDto();
        act.setTargetActUid(-1L);
        act.setSourceActUid(-1L);
        actCol.add(act);
        phcConn.setTheActRelationshipDTCollection(actCol);
        proxyVO.setPublicHealthCaseContainer(phcConn);

        var actEntityCol = new ArrayList<NbsActEntityDto>();
        var actEntity = new NbsActEntityDto();
        actEntity.setEntityUid(-1L);
        actEntityCol.add(actEntity);
        var base = new BasePamContainer();
        base.setActEntityDTCollection(actEntityCol);
        proxyVO.setPamVO(base);


        Long falseUid = -1L;
        Long actualUid = 1L;

        uidService.setFalseToNewForPam(proxyVO, falseUid, actualUid);
    }


    @Test
    void setFalseToNewForNotification_Test() {
        var proxyVO = new NotificationProxyContainer();


        var actCol = new ArrayList<Object>();
        var act = new ActRelationshipDto();
        act.setTargetActUid(-1L);
        act.setSourceActUid(-1L);
        actCol.add(act);
        proxyVO.setTheActRelationshipDTCollection(actCol);



        Long falseUid = -1L;
        Long actualUid = 1L;

        uidService.setFalseToNewForObservation(proxyVO, falseUid, actualUid);
    }
}
