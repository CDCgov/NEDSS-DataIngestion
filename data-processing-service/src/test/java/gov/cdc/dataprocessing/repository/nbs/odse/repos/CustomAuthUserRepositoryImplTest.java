package gov.cdc.dataprocessing.repository.nbs.odse.repos;

import gov.cdc.dataprocessing.repository.nbs.odse.model.auth.AuthUserRealizedRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
class CustomAuthUserRepositoryImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private CustomAuthUserRepositoryImpl customAuthUserRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAuthUserRealizedRole() {
        String userId = "testUser";

        List<Object[]> mockResultList = new ArrayList<>();
        mockResultList.add(new Object[]{
                "permSetNm", 1L, "authRoleNm", "progAreaCd", "jurisdictionCd", 2L, 3L,
                'N', 'Y', 1, new Timestamp(System.currentTimeMillis()), 4L,
                new Timestamp(System.currentTimeMillis()), 5L, "recordStatusCd",
                new Timestamp(System.currentTimeMillis())
        });

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(mockResultList);

        var result = customAuthUserRepository.getAuthUserRealizedRole(userId);

        assertEquals(1, result.size());

        AuthUserRealizedRole realizedRole = result.iterator().next();
        assertEquals("permSetNm", realizedRole.getPermSetNm());
        assertEquals(1L, realizedRole.getAuthUserRoleUid());
        assertEquals("authRoleNm", realizedRole.getAuthRoleNm());
        assertEquals("progAreaCd", realizedRole.getProgAreaCd());
        assertEquals("jurisdictionCd", realizedRole.getJurisdictionCd());
        assertEquals(2L, realizedRole.getAuthUserUid());
        assertEquals(3L, realizedRole.getAuthPermSetUid());
        assertEquals("N", realizedRole.getRoleGuestInd());
        assertEquals("Y", realizedRole.getReadOnlyInd());
        assertEquals(1, realizedRole.getDispSeqNbr());
        assertEquals(4L, realizedRole.getAddUserId());
        assertEquals(5L, realizedRole.getLastChgUserId());
        assertEquals("recordStatusCd", realizedRole.getRecordStatusCd());
    }
}