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
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @SuppressWarnings("java:S2699")
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

    @SuppressWarnings("java:S2699")
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

    @SuppressWarnings("java:S2699")
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

    @SuppressWarnings("java:S2699")
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


    @SuppressWarnings("java:S2699")
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



    @Test
    void testSetFalseToNewForObservation_ParticipationCollNull() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(null);
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(new ArrayList<>());

        uidService.setFalseToNewForObservation(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewForObservation_ParticipationCollEmpty() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(Collections.emptyList());
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(new ArrayList<>());

        uidService.setFalseToNewForObservation(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewForObservation_ActRelationShipCollNull() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(null);
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(new ArrayList<>());

        uidService.setFalseToNewForObservation(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewForObservation_ActRelationShipCollEmpty() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(Collections.emptyList());
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(new ArrayList<>());

        uidService.setFalseToNewForObservation(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewForObservation_RoleCollNull() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(null);

        uidService.setFalseToNewForObservation(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewForObservation_RoleCollEmpty() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(Collections.emptyList());

        uidService.setFalseToNewForObservation(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewForObservation_FalseUidNull() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        Collection<ParticipationDto> participationColl = new ArrayList<>();
        participationColl.add(new ParticipationDto());
        Collection<ActRelationshipDto> actRelationShipColl = new ArrayList<>();
        actRelationShipColl.add(new ActRelationshipDto());
        Collection<RoleDto> roleColl = new ArrayList<>();
        roleColl.add(new RoleDto());

        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(participationColl);
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(actRelationShipColl);
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(roleColl);

        uidService.setFalseToNewForObservation(proxyVO, null, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewPersonAndOrganization_ParticipationCollNull() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(null);
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(new ArrayList<>());

        uidService.setFalseToNewPersonAndOrganization(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewPersonAndOrganization_ParticipationCollEmpty() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(Collections.emptyList());
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(new ArrayList<>());

        uidService.setFalseToNewPersonAndOrganization(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewPersonAndOrganization_ActRelationShipCollNull() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(null);
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(new ArrayList<>());

        uidService.setFalseToNewPersonAndOrganization(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewPersonAndOrganization_ActRelationShipCollEmpty() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(Collections.emptyList());
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(new ArrayList<>());

        uidService.setFalseToNewPersonAndOrganization(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewPersonAndOrganization_RoleCollNull() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(null);

        uidService.setFalseToNewPersonAndOrganization(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }

    @Test
    void testSetFalseToNewPersonAndOrganization_RoleCollEmpty() {
        LabResultProxyContainer proxyVO = mock(LabResultProxyContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheActRelationshipDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getTheRoleDtoCollection()).thenReturn(Collections.emptyList());

        uidService.setFalseToNewPersonAndOrganization(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getTheActRelationshipDtoCollection();
        verify(proxyVO).getTheRoleDtoCollection();
    }


    @Test
    void testSetFalseToNewForPageAct_ParticipationCollNull() throws DataProcessingException {
        PageActProxyContainer proxyVO = mock(PageActProxyContainer.class);
        PublicHealthCaseContainer publicHealthCaseContainer = mock(PublicHealthCaseContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(null);
        when(proxyVO.getPublicHealthCaseContainer()).thenReturn(publicHealthCaseContainer);
        when(publicHealthCaseContainer.getTheActRelationshipDTCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getPageVO()).thenReturn(new PageContainer());

        uidService.setFalseToNewForPageAct(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getPublicHealthCaseContainer();
        verify(publicHealthCaseContainer).getTheActRelationshipDTCollection();
        verify(proxyVO).getPageVO();
    }

    @Test
    void testSetFalseToNewForPageAct_ParticipationCollEmpty() throws DataProcessingException {
        PageActProxyContainer proxyVO = mock(PageActProxyContainer.class);
        PublicHealthCaseContainer publicHealthCaseContainer = mock(PublicHealthCaseContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(Collections.emptyList());
        when(proxyVO.getPublicHealthCaseContainer()).thenReturn(publicHealthCaseContainer);
        when(publicHealthCaseContainer.getTheActRelationshipDTCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getPageVO()).thenReturn(new PageContainer());

        uidService.setFalseToNewForPageAct(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getPublicHealthCaseContainer();
        verify(publicHealthCaseContainer).getTheActRelationshipDTCollection();
        verify(proxyVO).getPageVO();
    }

    @Test
    void testSetFalseToNewForPageAct_ActRelationShipCollNull() throws DataProcessingException {
        PageActProxyContainer proxyVO = mock(PageActProxyContainer.class);
        PublicHealthCaseContainer publicHealthCaseContainer = mock(PublicHealthCaseContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getPublicHealthCaseContainer()).thenReturn(publicHealthCaseContainer);
        when(publicHealthCaseContainer.getTheActRelationshipDTCollection()).thenReturn(null);
        when(proxyVO.getPageVO()).thenReturn(new PageContainer());

        uidService.setFalseToNewForPageAct(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getPublicHealthCaseContainer();
        verify(publicHealthCaseContainer).getTheActRelationshipDTCollection();
        verify(proxyVO).getPageVO();
    }

    @Test
    void testSetFalseToNewForPageAct_ActRelationShipCollEmpty() throws DataProcessingException {
        PageActProxyContainer proxyVO = mock(PageActProxyContainer.class);
        PublicHealthCaseContainer publicHealthCaseContainer = mock(PublicHealthCaseContainer.class);
        when(proxyVO.getTheParticipationDtoCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getPublicHealthCaseContainer()).thenReturn(publicHealthCaseContainer);
        when(publicHealthCaseContainer.getTheActRelationshipDTCollection()).thenReturn(Collections.emptyList());
        when(proxyVO.getPageVO()).thenReturn(new PageContainer());

        uidService.setFalseToNewForPageAct(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDtoCollection();
        verify(proxyVO).getPublicHealthCaseContainer();
        verify(publicHealthCaseContainer).getTheActRelationshipDTCollection();
        verify(proxyVO).getPageVO();
    }

  

    // Tests for setFalseToNewForPam

    @Test
    void testSetFalseToNewForPam_ParticipationCollNull() throws DataProcessingException {
        PamProxyContainer proxyVO = mock(PamProxyContainer.class);
        PublicHealthCaseContainer publicHealthCaseContainer = mock(PublicHealthCaseContainer.class);
        when(proxyVO.getTheParticipationDTCollection()).thenReturn(null);
        when(proxyVO.getPublicHealthCaseContainer()).thenReturn(publicHealthCaseContainer);
        when(publicHealthCaseContainer.getTheActRelationshipDTCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getPamVO()).thenReturn(new PageContainer());

        uidService.setFalseToNewForPam(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDTCollection();
        verify(proxyVO).getPublicHealthCaseContainer();
        verify(publicHealthCaseContainer).getTheActRelationshipDTCollection();
        verify(proxyVO).getPamVO();
    }

    @Test
    void testSetFalseToNewForPam_ParticipationCollEmpty() throws DataProcessingException {
        PamProxyContainer proxyVO = mock(PamProxyContainer.class);
        PublicHealthCaseContainer publicHealthCaseContainer = mock(PublicHealthCaseContainer.class);
        when(proxyVO.getTheParticipationDTCollection()).thenReturn(Collections.emptyList());
        when(proxyVO.getPublicHealthCaseContainer()).thenReturn(publicHealthCaseContainer);
        when(publicHealthCaseContainer.getTheActRelationshipDTCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getPamVO()).thenReturn(new PageContainer());

        uidService.setFalseToNewForPam(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDTCollection();
        verify(proxyVO).getPublicHealthCaseContainer();
        verify(publicHealthCaseContainer).getTheActRelationshipDTCollection();
        verify(proxyVO).getPamVO();
    }

    @Test
    void testSetFalseToNewForPam_ActRelationShipCollNull() throws DataProcessingException {
        PamProxyContainer proxyVO = mock(PamProxyContainer.class);
        PublicHealthCaseContainer publicHealthCaseContainer = mock(PublicHealthCaseContainer.class);
        when(proxyVO.getTheParticipationDTCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getPublicHealthCaseContainer()).thenReturn(publicHealthCaseContainer);
        when(publicHealthCaseContainer.getTheActRelationshipDTCollection()).thenReturn(null);
        when(proxyVO.getPamVO()).thenReturn(new PageContainer());

        uidService.setFalseToNewForPam(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDTCollection();
        verify(proxyVO).getPublicHealthCaseContainer();
        verify(publicHealthCaseContainer).getTheActRelationshipDTCollection();
        verify(proxyVO).getPamVO();
    }

    @Test
    void testSetFalseToNewForPam_ActRelationShipCollEmpty() throws DataProcessingException {
        PamProxyContainer proxyVO = mock(PamProxyContainer.class);
        PublicHealthCaseContainer publicHealthCaseContainer = mock(PublicHealthCaseContainer.class);
        when(proxyVO.getTheParticipationDTCollection()).thenReturn(new ArrayList<>());
        when(proxyVO.getPublicHealthCaseContainer()).thenReturn(publicHealthCaseContainer);
        when(publicHealthCaseContainer.getTheActRelationshipDTCollection()).thenReturn(Collections.emptyList());
        when(proxyVO.getPamVO()).thenReturn(new PageContainer());

        uidService.setFalseToNewForPam(proxyVO, 1L, 2L);

        verify(proxyVO).getTheParticipationDTCollection();
        verify(proxyVO).getPublicHealthCaseContainer();
        verify(publicHealthCaseContainer).getTheActRelationshipDTCollection();
        verify(proxyVO).getPamVO();
    }


    @Test
    void testSetFalseToNewForNotification_ActRelationShipCollNull() throws DataProcessingException {
        NotificationProxyContainer notificationProxyVO = mock(NotificationProxyContainer.class);
        when(notificationProxyVO.getTheActRelationshipDTCollection()).thenReturn(null);

        ActRelationshipDto result = uidService.setFalseToNewForNotification(notificationProxyVO, 1L, 2L);

        assertNull(result);
        verify(notificationProxyVO).getTheActRelationshipDTCollection();
    }

    @Test
    void testSetFalseToNewForNotification_ActRelationShipCollEmpty() throws DataProcessingException {
        NotificationProxyContainer notificationProxyVO = mock(NotificationProxyContainer.class);
        when(notificationProxyVO.getTheActRelationshipDTCollection()).thenReturn(Collections.emptyList());

        ActRelationshipDto result = uidService.setFalseToNewForNotification(notificationProxyVO, 1L, 2L);

        assertNull(result);
        verify(notificationProxyVO).getTheActRelationshipDTCollection();
    }

    @Test
    void testSetFalseToNewForNotification_ActRelationShipCollContainsElements() throws DataProcessingException {
        NotificationProxyContainer notificationProxyVO = mock(NotificationProxyContainer.class);
        ActRelationshipDto actRelationshipDto = new ActRelationshipDto();
        Collection<Object> actRelationShipColl = new ArrayList<>();
        actRelationShipColl.add(actRelationshipDto);

        when(notificationProxyVO.getTheActRelationshipDTCollection()).thenReturn(actRelationShipColl);

        ActRelationshipDto result = uidService.setFalseToNewForNotification(notificationProxyVO, 1L, 2L);

        assertNotNull(result);
        assertEquals(2L, result.getSourceActUid());
        verify(notificationProxyVO).getTheActRelationshipDTCollection();
    }

    @Test
    void testSetFalseToNewForNotification_ActRelationShipCollMultipleElements() throws DataProcessingException {
        NotificationProxyContainer notificationProxyVO = mock(NotificationProxyContainer.class);
        ActRelationshipDto actRelationshipDto1 = new ActRelationshipDto();
        ActRelationshipDto actRelationshipDto2 = new ActRelationshipDto();
        Collection<Object> actRelationShipColl = new ArrayList<>();
        actRelationShipColl.add(actRelationshipDto1);
        actRelationShipColl.add(actRelationshipDto2);

        when(notificationProxyVO.getTheActRelationshipDTCollection()).thenReturn(actRelationShipColl);

        ActRelationshipDto result = uidService.setFalseToNewForNotification(notificationProxyVO, 1L, 2L);

        assertNotNull(result);
        assertEquals(2L, actRelationshipDto1.getSourceActUid());
        assertEquals(2L, actRelationshipDto2.getSourceActUid());
        verify(notificationProxyVO).getTheActRelationshipDTCollection();
    }
}
