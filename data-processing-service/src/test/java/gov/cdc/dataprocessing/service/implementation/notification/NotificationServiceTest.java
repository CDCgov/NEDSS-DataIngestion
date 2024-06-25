package gov.cdc.dataprocessing.service.implementation.notification;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.*;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.notification.NotificationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.model.dto.phc.PublicHealthCaseDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.notification.Notification;
import gov.cdc.dataprocessing.repository.nbs.odse.model.observation.Observation;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.notification.NotificationRepository;
import gov.cdc.dataprocessing.service.interfaces.uid_generator.IUidService;
import gov.cdc.dataprocessing.utilities.component.act.ActRelationshipRepositoryUtil;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PrepareAssocModelHelper;
import gov.cdc.dataprocessing.utilities.component.generic_helper.PropertyUtil;
import gov.cdc.dataprocessing.utilities.component.notification.NotificationRepositoryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    PropertyUtil propertyUtil;

    @Mock
    PrepareAssocModelHelper prepareAssocModelHelper;

    @Mock
    NotificationRepositoryUtil notificationRepositoryUtil;

    @Mock
    ActRelationshipRepositoryUtil actRelationshipRepositoryUtil;

    @Mock
    IUidService iUidService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getNotificationById() {
        Long uid = 1L;
        Notification notification = getNotification();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        assertEquals(notification.getNotificationUid(), notificationService.getNotificationById(uid).getNotificationUid());
    }

    @Test
    void getNotificationByIdNull() {
        Long uid = 1L;
        when(notificationRepository.findById(uid)).thenReturn(Optional.empty());
        assertEquals(null, notificationService.getNotificationById(uid));
    }

    private Notification getNotification() {
        Notification notification = new Notification();
        notification.setNotificationUid(1L);
        return notification;
    }

    @Test
    void saveNotification() {
        NotificationContainer notificationContainer = getNotificationContainer();

        when(notificationRepository.save(Mockito.any())).thenReturn(null);

        Long result = notificationService.saveNotification(notificationContainer);
        assertEquals(1l, result);
    }

    private NotificationDto getNotificationDto() {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotificationUid(1L);
        notificationDto.setItNew(true);
        notificationDto.setItDirty(true);

        return notificationDto;
    }

    private NotificationDto getNotificationDtoDirty() {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setNotificationUid(1L);
        notificationDto.setItNew(false);
        notificationDto.setItDirty(true);

        return notificationDto;
    }

    private NotificationContainer getNotificationContainer() {
        NotificationContainer notificationContainer = new NotificationContainer();

        notificationContainer.setTheNotificationDT(getNotificationDto());
        notificationContainer.setItNew(true);

        return notificationContainer;
    }

    private NotificationContainer getNotificationContainerDirty() {
        NotificationContainer notificationContainer = new NotificationContainer();

        notificationContainer.setTheNotificationDT(getNotificationDtoDirty());
        notificationContainer.setItNew(false);
        notificationContainer.setItDirty(true);

        return notificationContainer;
    }

    private PublicHealthCaseContainer getPublicHealthContainer() {
        PublicHealthCaseContainer publicHealthCaseContainer = new PublicHealthCaseContainer();

        PublicHealthCaseDto publicHealthCase = new PublicHealthCaseDto();

        publicHealthCase.setProgAreaCd("test prog area");
        publicHealthCase.setJurisdictionCd("test jurisdiction");
        publicHealthCase.setSharedInd("test shared ind");

        publicHealthCaseContainer.setThePublicHealthCaseDto(publicHealthCase);

        return publicHealthCaseContainer;
    }

    private NotificationProxyContainer getNotificationProxy() {
        NotificationProxyContainer notificationProxyContainer = new NotificationProxyContainer();
        notificationProxyContainer.setTheNotificationContainer(getNotificationContainer());
        notificationProxyContainer.setThePublicHealthCaseContainer(getPublicHealthContainer());
        return  notificationProxyContainer;
    }

    private NotificationProxyContainer getNotificationProxyDirty() {
        NotificationProxyContainer notificationProxyContainer = new NotificationProxyContainer();
        notificationProxyContainer.setTheNotificationContainer(getNotificationContainerDirty());
        notificationProxyContainer.setThePublicHealthCaseContainer(getPublicHealthContainer());
        return  notificationProxyContainer;
    }

    private NotificationProxyContainer getNotificationProxyNull() {
        NotificationProxyContainer notificationProxyContainer = new NotificationProxyContainer();
        notificationProxyContainer.setTheNotificationContainer(null);
        notificationProxyContainer.setThePublicHealthCaseContainer(getPublicHealthContainer());
        return  notificationProxyContainer;
    }

    @Test
    void checkForExistingNotification() throws DataProcessingException {
        BaseContainer labResultProxyContainer = getLabProxyContainer();
        when(notificationRepository.getCountOfExistingNotifications(1L, NEDSSConstant.CLASS_CD_OBS)).thenReturn(1L);

        boolean result = notificationService.checkForExistingNotification(labResultProxyContainer);
        assertEquals(true, result);
    }

    @Test
    void checkForExistingNotificationFalse() throws DataProcessingException {
        BaseContainer labResultProxyContainer = getLabProxyContainer();
        when(notificationRepository.getCountOfExistingNotifications(1L, NEDSSConstant.CLASS_CD_OBS)).thenReturn(0L);

        boolean result = notificationService.checkForExistingNotification(labResultProxyContainer);
        assertEquals(false, result);
    }

    @Test
    void checkForExistingNotificationException() {
        BaseContainer baseContainer = new BaseContainer();

        assertThrows(DataProcessingException.class, () -> notificationService.checkForExistingNotification(baseContainer));
    }

    private BaseContainer getLabProxyContainer() {
        LabResultProxyContainer labResultProxyContainer = new LabResultProxyContainer();
        labResultProxyContainer.setLabClia("test lab clia");
        labResultProxyContainer.setManualLab(false);
        labResultProxyContainer.setAssociatedInvInd(true);
        labResultProxyContainer.setAssociatedNotificationInd(true);
        labResultProxyContainer.setSendingFacilityUid(1L);
        labResultProxyContainer.setCaseSupervisor("case supervisor");
        labResultProxyContainer.setConversionHasModified(true);
        labResultProxyContainer.setFieldSupervisor("test field supervisor");
        labResultProxyContainer.setTheObservationContainerCollection(getObservations());
        return labResultProxyContainer;
    }

    private Collection<ObservationContainer> getObservations() {
        Collection<ObservationContainer> obs = new ArrayList<>();
        ObservationContainer observationContainer = new ObservationContainer();
        Observation observation = new Observation();
        observation.setObservationUid(1L);
        observation.setObsDomainCdSt1(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD);
        observation.setCtrlCdDisplayForm(NEDSSConstant.LAB_REPORT);

        ObservationDto observationDto = new ObservationDto(observation);
        observationContainer.setTheObservationDto(observationDto);
        obs.add(observationContainer);
        return obs;
    }

    @Test
    void setNotificationProxyIsNull() {
        assertThrows(DataProcessingException.class, () -> notificationService.setNotificationProxy(null));
    }

    @Test void setNotificationProxy() throws DataProcessingException {
        when(propertyUtil.isHIVProgramArea(any())).thenReturn(true);

        when(prepareAssocModelHelper.prepareVO(any(), any(), any(), any(), any(), any())).thenReturn(getNotificationDto());

        when(notificationRepositoryUtil.setNotification(any())).thenReturn(1L);

        when(iUidService.setFalseToNewForNotification(any(), any(), any())).thenReturn(new ActRelationshipDto());

        doNothing().when(actRelationshipRepositoryUtil).storeActRelationship(any());

        assertEquals(1L, notificationService.setNotificationProxy(getNotificationProxy()));
    }

    @Test void setNotificationProxyDirty() throws DataProcessingException {
        when(propertyUtil.isHIVProgramArea(any())).thenReturn(true);

        when(prepareAssocModelHelper.prepareVO(any(), any(), any(), any(), any(), any())).thenReturn(getNotificationDto());

        when(notificationRepositoryUtil.setNotification(any())).thenReturn(1L);

        when(iUidService.setFalseToNewForNotification(any(), any(), any())).thenReturn(new ActRelationshipDto());

        doNothing().when(actRelationshipRepositoryUtil).storeActRelationship(any());

        assertEquals(1L, notificationService.setNotificationProxy(getNotificationProxyDirty()));
    }

    @Test void setNotificationProxyNull() {
        assertThrows(DataProcessingException.class, () -> notificationService.setNotificationProxy(getNotificationProxyNull()));
    }
}