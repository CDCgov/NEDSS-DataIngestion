package gov.cdc.dataprocessing.service.implementation.uid_generator;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.model.generic_helper.LocalUidGenerator;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.locator.LocalUidGeneratorRepository;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class OdseIdGeneratorServiceTest {
    @Mock
    private LocalUidGeneratorRepository localUidGeneratorRepository;
    @InjectMocks
    private OdseIdGeneratorService odseIdGeneratorService;
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
        Mockito.reset(localUidGeneratorRepository, authUtil);
    }

    @Test
    void getLocalIdAndUpdateSeed_Test() throws DataProcessingException {


        LocalUidGenerator localUidOpt = new LocalUidGenerator();
        localUidOpt.setSeedValueNbr(1L);
        localUidOpt.setUidSuffixCd("TEST");
        localUidOpt.setUidPrefixCd("TEST");
        localUidOpt.setTypeCd("TYPE");
        localUidOpt.setClassNameCd("CLASS");
        when(localUidGeneratorRepository.findById(LocalIdClass.OBSERVATION.name())).thenReturn(Optional.of(localUidOpt));

        var res = odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.OBSERVATION);

        assertEquals(1L, res.getSeedValueNbr());
    }

    @Test
    void getLocalIdAndUpdateSeed_Exception() {


        LocalUidGenerator localUidOpt = new LocalUidGenerator();
        localUidOpt.setSeedValueNbr(1L);
        localUidOpt.setUidSuffixCd("TEST");
        localUidOpt.setUidPrefixCd("TEST");
        localUidOpt.setTypeCd("TYPE");
        localUidOpt.setClassNameCd("CLASS");
        when(localUidGeneratorRepository.findById(LocalIdClass.OBSERVATION.name()))
                .thenThrow(new RuntimeException("TEST"));

        DataProcessingException thrown = assertThrows(DataProcessingException.class, () -> {
            odseIdGeneratorService.getLocalIdAndUpdateSeed(LocalIdClass.OBSERVATION);
        });
        assertNotNull(thrown);
    }

}
