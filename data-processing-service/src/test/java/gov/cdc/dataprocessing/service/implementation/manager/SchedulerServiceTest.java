package gov.cdc.dataprocessing.service.implementation.manager;

import gov.cdc.dataprocessing.cache.OdseCache;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.implementation.uid_generator.UidPoolManager;
import gov.cdc.dataprocessing.service.interfaces.auth_user.IAuthUserService;
import gov.cdc.dataprocessing.service.interfaces.lookup_data.ILookupService;
import gov.cdc.dataprocessing.service.model.auth_user.AuthUserProfileInfo;
import gov.cdc.dataprocessing.utilities.auth.AuthUtil;
import gov.cdc.dataprocessing.utilities.component.sql.QueryHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchedulerServiceTest {

    @Mock
    private IAuthUserService authUserService;

    @Mock
    private QueryHelper queryHelper;

    @Mock
    private ILookupService lookupService;

    @Mock
    private UidPoolManager uidPoolManager;

    @InjectMocks
    private SchedulerService schedulerService;

    private static final String TEST_USER = "test-user";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inject @Value field manually for unit test
        try {
            var field = SchedulerService.class.getDeclaredField("nbsUser");
            field.setAccessible(true);
            field.set(schedulerService, TEST_USER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPopulateAuthUser_setsGlobalAuthUser() throws DataProcessingException {
        AuthUserProfileInfo mockProfile = new AuthUserProfileInfo();
        when(authUserService.getAuthUserInfo(TEST_USER)).thenReturn(mockProfile);

        schedulerService.populateAuthUser();

        verify(authUserService).getAuthUserInfo(TEST_USER);
    }

    @Test
    void testPopulateHashPAJList_setsCacheValues() throws DataProcessingException {
        String ownerHash = "hashed-owner-values";
        String guestHash = "hashed-guest-values";

        when(queryHelper.getHashedPAJList(false)).thenReturn(ownerHash);
        when(queryHelper.getHashedPAJList(true)).thenReturn(guestHash);

        schedulerService.populateHashPAJList();

        assertEquals(ownerHash, OdseCache.OWNER_LIST_HASHED_PA_J);
        assertEquals(guestHash, OdseCache.GUEST_LIST_HASHED_PA_J);
        verify(queryHelper).getHashedPAJList(false);
        verify(queryHelper).getHashedPAJList(true);
    }

    @Test
    void testPopulateDMBQuestionMap_setsCacheMap() {
        TreeMap<Object, Object> mockTreeMap = new TreeMap<>();
        mockTreeMap.put("key1", "value1");

        when(lookupService.getDMBQuestionMapAfterPublish()).thenReturn(mockTreeMap);

        schedulerService.populateDMBQuestionMap();

        assertEquals(mockTreeMap, OdseCache.DMB_QUESTION_MAP);
        verify(lookupService).getDMBQuestionMapAfterPublish();
    }

    @Test
    void testRefillUid_callsPeriodicRefill() {
        schedulerService.refillUid();
        verify(uidPoolManager).periodicRefill();
    }
}
