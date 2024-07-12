package gov.cdc.dataprocessing.utilities.component.observation;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BaseContainer;
import gov.cdc.dataprocessing.model.container.model.LabResultProxyContainer;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.act.ActRelationshipDto;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void getUid_Test() throws DataProcessingException {
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
    void getUid_Test_2() throws DataProcessingException {
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
    void getUid_Test_3() throws DataProcessingException {
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

}
