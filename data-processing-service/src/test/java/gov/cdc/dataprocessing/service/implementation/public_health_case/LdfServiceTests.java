package gov.cdc.dataprocessing.service.implementation.public_health_case;

import gov.cdc.dataprocessing.constant.elr.NEDSSConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.generic_helper.StateDefinedFieldDataDto;
import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUser;
import gov.cdc.dataprocessing.repository.nbs.odse.repos.CustomRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class LdfServiceTests {
    @Mock
    private CustomRepository customRepository;
    @InjectMocks
    private LdfService ldfService;
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
        Mockito.reset(customRepository , authUtil);
    }

    @Test
    void getLDFCollection_Success() throws DataProcessingException {
        long  busObjUid = 10L;
        String condCode = "COND";

        var list = new ArrayList<StateDefinedFieldDataDto>();
        when(customRepository.getLdfCollection(eq(busObjUid), eq(condCode), any())).thenReturn(
                list
        );
        var test = ldfService.getLDFCollection(busObjUid, condCode);
        assertNotNull(test);

    }

    @Test
    void getLDFCollection_Exception()  {
        long  busObjUid = 10L;
        String condCode = "COND";

        when(customRepository.getLdfCollection(any(), any(), any())).thenThrow(
                new RuntimeException("TEST")
        );
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            ldfService.getLDFCollection(busObjUid, condCode);
        });

        assertNotNull(thrown);
        assertEquals("TEST", thrown.getMessage());
    }

}
