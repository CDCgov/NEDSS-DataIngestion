package gov.cdc.dataprocessing.service.implementation.stored_proc;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.model.container.model.ObservationContainer;
import gov.cdc.dataprocessing.model.dto.lab_result.EdxLabInformationDto;
import gov.cdc.dataprocessing.model.dto.observation.ObservationDto;
import gov.cdc.dataprocessing.repository.nbs.msgoute.repos.StoredProcRepository;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.time.TimeStampUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class MsgOutEStoredProcServiceTest {
    @Mock
    private StoredProcRepository storedProcRepository;
    @InjectMocks
    private MsgOutEStoredProcService msgOutEStoredProcService;
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
        Mockito.reset(storedProcRepository, authUtil);
    }

    @Test
    void callUpdateSpecimenCollDateSP_Test() {
        EdxLabInformationDto edxLabInformationDto = new EdxLabInformationDto();
        edxLabInformationDto.setNbsInterfaceUid(10L);
        var obsConn = new ObservationContainer();
        var obsDt = new ObservationDto();
        obsDt.setEffectiveFromTime(TimeStampUtil.getCurrentTimeStamp("UTC"));
        obsConn.setTheObservationDto(obsDt);
        edxLabInformationDto.setRootObservationContainer(obsConn);
        msgOutEStoredProcService.callUpdateSpecimenCollDateSP(edxLabInformationDto);

        verify(storedProcRepository, times(1)).updateSpecimenCollDateSP(
                any(), any()
        );
    }

}
