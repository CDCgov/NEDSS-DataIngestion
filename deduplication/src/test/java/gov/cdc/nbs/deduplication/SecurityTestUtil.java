package gov.cdc.nbs.deduplication;

import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

public class SecurityTestUtil {
  public static void mockSecurityContext() {
    mockSecurityContext(100L);
  }

  public static void mockSecurityContext(long userId) {
    NbsUserDetails mockUserDetails = mock(NbsUserDetails.class);
    when(mockUserDetails.getId()).thenReturn(userId);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(mockUserDetails);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);
  }
}
