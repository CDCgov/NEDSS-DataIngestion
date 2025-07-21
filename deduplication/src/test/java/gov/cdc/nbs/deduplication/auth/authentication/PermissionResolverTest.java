package gov.cdc.nbs.deduplication.auth.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.MappedQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class PermissionResolverTest {

  @Mock
  private JdbcClient jdbcClient;

  @InjectMocks
  private PermissionResolver resolver;

  @Test
  void should_resolve_permissions() {
    // Mock
    mockCurrentUser("test-user");
    StatementSpec statement = Mockito.mock(StatementSpec.class);
    when(jdbcClient.sql(PermissionResolver.QUERY)).thenReturn(statement);
    when(statement.param("username", "test-user")).thenReturn(statement);
    when(statement.param("operation", "view")).thenReturn(statement);
    when(statement.param("object", "investigation")).thenReturn(statement);
    MappedQuerySpec<Long> qs = Mockito.mock(MappedQuerySpec.class);
    when(statement.query(Long.class)).thenReturn(qs);
    when(qs.list()).thenReturn(List.of(1l, 2l, 3l));

    // Act
    List<Long> oids = resolver.resolve("view", "investigation");

    // Verify
    assertThat(oids).containsExactly(1l, 2l, 3l);
  }

  private void mockCurrentUser(String username) {
    Authentication auth = Mockito.mock(Authentication.class);
    when(auth.getName()).thenReturn(username);

    SecurityContextHolder.getContext().setAuthentication(auth);
  }
}
