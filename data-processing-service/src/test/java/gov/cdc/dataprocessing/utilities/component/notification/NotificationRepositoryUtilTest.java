package gov.cdc.dataprocessing.utilities.component.notification;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.NotificationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActIdDto;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.act.ActivityLocatorParticipationDto;
import gov.cdc.dataprocessing.model.dto.notification.UpdatedNotificationDto;
import gov.cdc.dataprocessing.model.dto.participation.ParticipationDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidGeneratorDto;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.notification.NotificationRepository;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IOdseIdGeneratorWCacheService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActIdRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActLocatorParticipationRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.act.ActRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.entity.EntityHelper;
import gov.cdc.dataprocessing.utilities.component.participation.ParticipationRepositoryUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

class NotificationRepositoryUtilTest {
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private ActIdRepositoryUtil actIdRepositoryUtil;
    @Mock
    private ActLocatorParticipationRepositoryUtil actLocatorParticipationRepositoryUtil;
    @Mock
    private ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;
    @Mock
    private ParticipationRepositoryUtil participationRepositoryUtil;
    @Mock
    private EntityHelper entityHelper;
    @Mock
    private  ActRepositoryUtil actRepositoryUtil;
    @Mock
    private IOdseIdGeneratorWCacheService odseIdGeneratorService;
    @InjectMocks
    private NotificationRepositoryUtil notificationRepositoryUtil;
    @Mock
    AuthUtil authUtil;

    @Mock
    UidPoolManager uidPoolManager;

    @BeforeEach
    void setUp() throws DataProcessingException {
        MockitoAnnotations.openMocks(this);
        AuthUserProfileInfo userInfo = new AuthUserProfileInfo();
        AuthUser user = new AuthUser();
        user.setAuthUserUid(1L);
        user.setUserType(NEDSSConstant.SEC_USERTYPE_EXTERNAL);
        userInfo.setAuthUser(user);

        var model = new LocalUidModel();
        LocalUidGeneratorDto dto = new LocalUidGeneratorDto();
        dto.setClassNameCd("TEST");
        dto.setTypeCd("TEST");
        dto.setUidPrefixCd("TEST");
        dto.setUidSuffixCd("TEST");
        dto.setSeedValueNbr(1L);
        dto.setCounter(3);
        dto.setUsedCounter(2);
        model.setClassTypeUid(dto);
        model.setGaTypeUid(dto);
        model.setPrimaryClassName("TEST");
        when(uidPoolManager.getNextUid(any(), anyBoolean())).thenReturn(model);

        authUtil.setGlobalAuthUser(userInfo);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(notificationRepository, actIdRepositoryUtil,
                actLocatorParticipationRepositoryUtil, actRelationshipRepositoryUtil,
                participationRepositoryUtil, entityHelper,
                actRepositoryUtil, odseIdGeneratorService, authUtil);
    }

    @Test
    void getNotificationContainer_Test(){
        Long uid = 10L;
        var noti = new Notification();
        when(notificationRepository.findById(uid)).thenReturn(Optional.of(noti));

        var actIdCol = new ArrayList<ActIdDto>();
        var actId = new ActIdDto();
        actIdCol.add(actId);
        when(actIdRepositoryUtil.getActIdCollection(uid)).thenReturn(actIdCol);

        var actLocCol = new ArrayList<ActivityLocatorParticipationDto>();
        var actLoc = new ActivityLocatorParticipationDto();
        actLocCol.add(actLoc);
        when(actLocatorParticipationRepositoryUtil.getActLocatorParticipationCollection(uid)).thenReturn(actLocCol);

        var actReCol = new ArrayList<ActRelationshipDto>();
        var actRe = new ActRelationshipDto();
        actReCol.add(actRe);
        when(actRelationshipRepositoryUtil.getActRelationshipCollectionFromSourceId(uid)).thenReturn(actReCol);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        when(participationRepositoryUtil.getParticipationCollection(uid)).thenReturn(patCol);

        var res = notificationRepositoryUtil.getNotificationContainer(uid);
        assertNotNull(res);

    }

    @Test
    void getNotificationContainer_Test_2(){
        Long uid = 10L;
        when(notificationRepository.findById(uid)).thenReturn(Optional.empty());

        var res = notificationRepositoryUtil.getNotificationContainer(uid);
        assertNull(res);

    }

    @Test
    void setNotification_Test() throws DataProcessingException {
        NotificationContainer notificationContainer = new NotificationContainer();


        var actLocCol = new ArrayList<ActivityLocatorParticipationDto>();
        var actLoc = new ActivityLocatorParticipationDto();
        actLocCol.add(actLoc);
        notificationContainer.setTheActivityLocatorParticipationDTCollection(actLocCol);


        var actReCol = new ArrayList<ActRelationshipDto>();
        var actRe = new ActRelationshipDto();
        actReCol.add(actRe);
        notificationContainer.setTheActRelationshipDTCollection(actReCol);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        notificationContainer.setTheParticipationDTCollection(patCol);

        when(entityHelper.iterateALPDTActivityLocatorParticipation(any())).thenReturn(actLocCol);
        when(entityHelper.iterateARDTActRelationship(any())).thenReturn(actReCol);
        when(entityHelper.iteratePDTForParticipation(any())).thenReturn(patCol);

        notificationContainer.setItNew(true);

        var local = new LocalUidModel();
        local.setGaTypeUid(new LocalUidGeneratorDto());
        local.setClassTypeUid(new LocalUidGeneratorDto());
        local.getClassTypeUid().setSeedValueNbr(10L);
        local.getGaTypeUid().setSeedValueNbr(10L);
        when(odseIdGeneratorService.getValidLocalUid(any(), anyBoolean())).thenReturn(local);


        var res = notificationRepositoryUtil.setNotification(notificationContainer);

        assertNotNull(res);

    }

    @Test
    void setNotification_Test_2() throws DataProcessingException {
        NotificationContainer notificationContainer = new NotificationContainer();


        var actLocCol = new ArrayList<ActivityLocatorParticipationDto>();
        var actLoc = new ActivityLocatorParticipationDto();
        actLocCol.add(actLoc);
        notificationContainer.setTheActivityLocatorParticipationDTCollection(actLocCol);


        var actReCol = new ArrayList<ActRelationshipDto>();
        var actRe = new ActRelationshipDto();
        actReCol.add(actRe);
        notificationContainer.setTheActRelationshipDTCollection(actReCol);

        var patCol = new ArrayList<ParticipationDto>();
        var pat = new ParticipationDto();
        patCol.add(pat);
        notificationContainer.setTheParticipationDTCollection(patCol);

        when(entityHelper.iterateALPDTActivityLocatorParticipation(any())).thenReturn(actLocCol);
        when(entityHelper.iterateARDTActRelationship(any())).thenReturn(actReCol);
        when(entityHelper.iteratePDTForParticipation(any())).thenReturn(patCol);

        notificationContainer.setItNew(false);

        var updateNo = new UpdatedNotificationDto();
        updateNo.setNotificationUid(10L);
        notificationContainer.setTheUpdatedNotificationDto(updateNo);

        notificationContainer.getTheNotificationDT().setNotificationUid(10L);

        when(notificationRepository.findById(any())).thenReturn(Optional.of(new Notification(notificationContainer.getTheNotificationDT())));


        var res = notificationRepositoryUtil.setNotification(notificationContainer);

        assertNotNull(res);

    }
}
