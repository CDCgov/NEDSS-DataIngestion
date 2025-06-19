package gov.cdc.dataprocessing.utilities.component.observation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

class ObservationUtilTest {
    @InjectMocks
    private ObservationUtil observationUtil;
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
    void getUid_Test() {
        ArrayList<ParticipationDto> participationDtoCollection = new ArrayList<>();
        ArrayList<ActRelationshipDto> actRelationshipDtoCollection = new ArrayList<>();
        String uidListType = "TEST";
        String uidClassCd = "TEST";
        String uidTypeCd = "TEST";
        String uidActClassCd = "TEST";
        String uidRecordStatusCd = "TEST";

        var pat = new ParticipationDto();
        pat.setSubjectClassCd("TEST");
        pat.setTypeCd("TEST");
        pat.setActClassCd("TEST");
        pat.setRecordStatusCd("TEST");
        pat.setSubjectEntityUid(10L);
        participationDtoCollection.add(pat);
        var act = new ActRelationshipDto();
        actRelationshipDtoCollection.add(act);

        var res = observationUtil.getUid(participationDtoCollection, actRelationshipDtoCollection, uidListType, uidClassCd,
                uidTypeCd, uidActClassCd, uidRecordStatusCd);

        assertNotNull(res);

    }

    @Test
    void getUid_Test_2()  {
        ArrayList<ParticipationDto> participationDtoCollection = null;
        ArrayList<ActRelationshipDto> actRelationshipDtoCollection = new ArrayList<>();
        String uidListType = NEDSSConstant.ACT_UID_LIST_TYPE;
        String uidClassCd = "TEST";
        String uidTypeCd = "TEST";
        String uidActClassCd = "TEST";
        String uidRecordStatusCd = "TEST";

        var pat = new ActRelationshipDto();
        pat.setSourceClassCd("TEST");
        pat.setTypeCd("TEST");
        pat.setTargetClassCd("TEST");
        pat.setRecordStatusCd("TEST");
        pat.setTargetActUid(10L);
        actRelationshipDtoCollection.add(pat);
        actRelationshipDtoCollection.add(pat);

        var res = observationUtil.getUid(participationDtoCollection, actRelationshipDtoCollection, uidListType, uidClassCd,
                uidTypeCd, uidActClassCd, uidRecordStatusCd);

        assertNotNull(res);

    }

    @Test
    void getUid_Test_3()  {
        ArrayList<ParticipationDto> participationDtoCollection = null;
        ArrayList<ActRelationshipDto> actRelationshipDtoCollection = new ArrayList<>();
        String uidListType = NEDSSConstant.SOURCE_ACT_UID_LIST_TYPE;
        String uidClassCd = "TEST";
        String uidTypeCd = "TEST";
        String uidActClassCd = "TEST";
        String uidRecordStatusCd = "TEST";

        var pat = new ActRelationshipDto();
        pat.setSourceClassCd("TEST");
        pat.setTypeCd("TEST");
        pat.setTargetClassCd("TEST");
        pat.setRecordStatusCd("TEST");
        pat.setSourceActUid(10L);
        actRelationshipDtoCollection.add(pat);
        actRelationshipDtoCollection.add(pat);

        var res = observationUtil.getUid(participationDtoCollection, actRelationshipDtoCollection, uidListType, uidClassCd,
                uidTypeCd, uidActClassCd, uidRecordStatusCd);

        assertNotNull(res);

    }



    @Test
    void getRootObservationDto_Test() throws DataProcessingException {
        LabResultProxyContainer baseContainer = new LabResultProxyContainer();
        var obsCol = new ArrayList<ObservationContainer>();
        var obs = new ObservationContainer();
        obs.getTheObservationDto().setCtrlCdDisplayForm(NEDSSConstant.LAB_CTRLCD_DISPLAY);
        obsCol.add(obs);
        baseContainer.setTheObservationContainerCollection(obsCol);

        var res = observationUtil.getRootObservationContainer(baseContainer);
        assertNotNull(res);

    }

    @Test
    void getRootObservationDto_Test_2() throws DataProcessingException {
        LabResultProxyContainer baseContainer = new LabResultProxyContainer();
        var obsCol = new ArrayList<ObservationContainer>();
        var obs = new ObservationContainer();
        obs.getTheObservationDto().setObsDomainCdSt1(NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD);
        obsCol.add(obs);
        baseContainer.setTheObservationContainerCollection(obsCol);
        var res = observationUtil.getRootObservationContainer(baseContainer);
        assertNotNull(res);

    }

    @Test
    void getRootObservationDto_Test_3() throws DataProcessingException {
        LabResultProxyContainer baseContainer = new LabResultProxyContainer();
        var obsCol = new ArrayList<ObservationContainer>();
        var obs = new ObservationContainer();
        obs.getTheObservationDto().setCtrlCdDisplayForm(NEDSSConstant.MOB_CTRLCD_DISPLAY);
        obsCol.add(obs);
        baseContainer.setTheObservationContainerCollection(obsCol);
        var res = observationUtil.getRootObservationContainer(baseContainer);
        assertNotNull(res);

    }

    @Test
    void getRootObservationDto_2_Test() throws DataProcessingException {
        LabResultProxyContainer baseContainer = new LabResultProxyContainer();
        var obsCol = new ArrayList<ObservationContainer>();
        var obs = new ObservationContainer();
        obs.getTheObservationDto().setCtrlCdDisplayForm(NEDSSConstant.LAB_CTRLCD_DISPLAY);
        obsCol.add(obs);
        baseContainer.setTheObservationContainerCollection(obsCol);

        var res = observationUtil.getRootObservationDto(baseContainer);
        assertNotNull(res);


    }

    @Test
    void getRootObservationDto_Test_4()   {
        BaseContainer baseContainer = new BaseContainer();
        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            observationUtil.getRootObservationContainer(baseContainer);
        });
        assertNotNull(thrown);

    }

    @Test
    void testGetRootObservationContainerFromObsCollection_returnsNull_whenCollectionIsNull() {
        ObservationContainer result = observationUtil.getRootObservationContainerFromObsCollection(null, true);
        assertNull(result);
    }

    @Test
    void testGetRootObservationContainerFromObsCollection_returnsNull_whenNoMatch() {
        ObservationDto dto = new ObservationDto();
        dto.setCtrlCdDisplayForm("XYZ"); // Not matching any expected constant
        dto.setObsDomainCdSt1("ABC");    // Not ORDERED_TEST_OBS_DOMAIN_CD

        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(dto);

        Collection<ObservationContainer> obsColl = Collections.singletonList(container);

        ObservationContainer result = observationUtil.getRootObservationContainerFromObsCollection(obsColl, true);
        assertNull(result);
    }


    private ObservationContainer createObservation(String ctrlCd, String obsDomain) {
        ObservationDto dto = new ObservationDto();
        dto.setCtrlCdDisplayForm(ctrlCd);
        dto.setObsDomainCdSt1(obsDomain);

        ObservationContainer container = new ObservationContainer();
        container.setTheObservationDto(dto);
        return container;
    }

    @Test
    void testReturnsContainerWhenCtrlCdIsLabReport() {
        ObservationContainer container = createObservation(NEDSSConstant.LAB_CTRLCD_DISPLAY, null);
        ObservationContainer result = observationUtil.getRootObservationContainerFromObsCollection(
                Collections.singletonList(container), false
        );
        assertEquals(container, result);
    }

    @Test
    void testReturnsContainerWhenObsDomainIsOrderedTestAndIsLabReportTrue() {
        ObservationContainer container = createObservation(null, NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD);
        ObservationContainer result = observationUtil.getRootObservationContainerFromObsCollection(
                Collections.singletonList(container), true
        );
        assertEquals(container, result);
    }

    @Test
    void testSkipsContainerWhenObsDomainIsOrderedTestButIsLabReportFalse() {
        ObservationContainer container = createObservation(null, NEDSSConstant.ORDERED_TEST_OBS_DOMAIN_CD);
        ObservationContainer result = observationUtil.getRootObservationContainerFromObsCollection(
                Collections.singletonList(container), false
        );
        assertNull(result);
    }

    @Test
    void testReturnsContainerWhenCtrlCdIsMorbidityReport() {
        ObservationContainer container = createObservation(NEDSSConstant.MOB_CTRLCD_DISPLAY, null);
        ObservationContainer result = observationUtil.getRootObservationContainerFromObsCollection(
                Collections.singletonList(container), false
        );
        assertEquals(container, result);
    }

    @Test
    void testReturnsNullWhenNoConditionsMatch() {
        ObservationContainer container = createObservation("OTHER", "OTHER_DOMAIN");
        ObservationContainer result = observationUtil.getRootObservationContainerFromObsCollection(
                Collections.singletonList(container), true
        );
        assertNull(result);
    }

    @Test
    void testGetRootObservationDto_returnsNullWhenRootContainerIsNull() throws DataProcessingException {
        BaseContainer proxyVO = new BaseContainer();

        ObservationUtil spyUtil = Mockito.spy(observationUtil);
        doReturn(null).when(spyUtil).getRootObservationContainer(proxyVO);

        ObservationDto result = spyUtil.getRootObservationDto(proxyVO);
        assertNull(result);
    }

    @Test
    void testGetUid_returnsUidWhenAllParticipationConditionsMatch() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd("CLS");
        dto.setTypeCd("TYPE");
        dto.setActClassCd("ACT");
        dto.setRecordStatusCd("ACTIVE");
        dto.setSubjectEntityUid(123L);

        Collection<ParticipationDto> partList = Collections.singletonList(dto);
        Long result = observationUtil.getUid(partList, null, "ignored", "CLS", "TYPE", "ACT", "ACTIVE");

        assertEquals(123L, result);
    }

    @SuppressWarnings("java:S5976")
    @Test
    void testGetUid_returnsNullWhenSubjectClassCdIsNull() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd(null); // will fail
        dto.setTypeCd("TYPE");
        dto.setActClassCd("ACT");
        dto.setRecordStatusCd("ACTIVE");
        dto.setSubjectEntityUid(123L);

        Long result = observationUtil.getUid(Collections.singletonList(dto), null, "ignored", "CLS", "TYPE", "ACT", "ACTIVE");
        assertNull(result);
    }

    @Test
    void testGetUid_returnsNullWhenTypeCdIsDifferent() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd("CLS");
        dto.setTypeCd("WRONG"); // mismatch
        dto.setActClassCd("ACT");
        dto.setRecordStatusCd("ACTIVE");
        dto.setSubjectEntityUid(123L);

        Long result = observationUtil.getUid(Collections.singletonList(dto), null, "ignored", "CLS", "TYPE", "ACT", "ACTIVE");
        assertNull(result);
    }

    @Test
    void testGetUid_returnsNullWhenRecordStatusCdIsNull() {
        ParticipationDto dto = new ParticipationDto();
        dto.setSubjectClassCd("CLS");
        dto.setTypeCd("TYPE");
        dto.setActClassCd("ACT");
        dto.setRecordStatusCd(null); // will fail
        dto.setSubjectEntityUid(123L);

        Long result = observationUtil.getUid(Collections.singletonList(dto), null, "ignored", "CLS", "TYPE", "ACT", "ACTIVE");
        assertNull(result);
    }

    @Test
    void testGetUid_returnsLastMatchingUidWhenMultipleValid() {
        ParticipationDto dto1 = new ParticipationDto();
        dto1.setSubjectClassCd("CLS");
        dto1.setTypeCd("TYPE");
        dto1.setActClassCd("ACT");
        dto1.setRecordStatusCd("ACTIVE");
        dto1.setSubjectEntityUid(111L);

        ParticipationDto dto2 = new ParticipationDto();
        dto2.setSubjectClassCd("CLS");
        dto2.setTypeCd("TYPE");
        dto2.setActClassCd("ACT");
        dto2.setRecordStatusCd("ACTIVE");
        dto2.setSubjectEntityUid(222L);

        Long result = observationUtil.getUid(
                List.of(dto1, dto2), null, "ignored", "CLS", "TYPE", "ACT", "ACTIVE");

        assertEquals(222L, result); // Last match wins
    }

    private ActRelationshipDto createActRel(String sourceCls, String typeCd, String targetCls, String statusCd,
                                            Long targetUid, Long sourceUid) {
        ActRelationshipDto dto = new ActRelationshipDto();
        dto.setSourceClassCd(sourceCls);
        dto.setTypeCd(typeCd);
        dto.setTargetClassCd(targetCls);
        dto.setRecordStatusCd(statusCd);
        dto.setTargetActUid(targetUid);
        dto.setSourceActUid(sourceUid);
        return dto;
    }

    @Test
    void testGetUid_returnsTargetUidWhenAllActRelConditionsMatch() {
        ActRelationshipDto dto = createActRel("SRC", "TYPE", "TRG", "ACTIVE", 123L, 888L);

        Long result = observationUtil.getUid(null, Collections.singletonList(dto),
                NEDSSConstant.ACT_UID_LIST_TYPE, "SRC", "TYPE", "TRG", "ACTIVE");

        assertEquals(123L, result);
    }

    @Test
    void testGetUid_returnsSourceUidWhenAllActRelConditionsMatchWithSourceType() {
        ActRelationshipDto dto = createActRel("SRC", "TYPE", "TRG", "ACTIVE", 123L, 888L);

        Long result = observationUtil.getUid(null, Collections.singletonList(dto),
                NEDSSConstant.SOURCE_ACT_UID_LIST_TYPE, "SRC", "TYPE", "TRG", "ACTIVE");

        assertEquals(888L, result);
    }

    @Test
    void testGetUid_returnsNullWhenRecordStatusIsNull() {
        ActRelationshipDto dto = createActRel("SRC", "TYPE", "TRG", null, 123L, 888L);

        Long result = observationUtil.getUid(null, Collections.singletonList(dto),
                NEDSSConstant.ACT_UID_LIST_TYPE, "SRC", "TYPE", "TRG", "ACTIVE");

        assertNull(result);
    }

    @Test
    void testGetUid_returnsNullWhenSourceClassDoesNotMatch() {
        ActRelationshipDto dto = createActRel("WRONG", "TYPE", "TRG", "ACTIVE", 123L, 888L);

        Long result = observationUtil.getUid(null, Collections.singletonList(dto),
                NEDSSConstant.ACT_UID_LIST_TYPE, "SRC", "TYPE", "TRG", "ACTIVE");

        assertNull(result);
    }

    @Test
    void testGetUid_returnsLastMatchingUidWhenMultipleActRelMatch() {
        ActRelationshipDto dto1 = createActRel("SRC", "TYPE", "TRG", "ACTIVE", 111L, 222L);
        ActRelationshipDto dto2 = createActRel("SRC", "TYPE", "TRG", "ACTIVE", 999L, 888L);

        Long result = observationUtil.getUid(null, List.of(dto1, dto2),
                NEDSSConstant.ACT_UID_LIST_TYPE, "SRC", "TYPE", "TRG", "ACTIVE");

        assertEquals(999L, result); // Last matching UID should be returned
    }
}
