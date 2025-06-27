package gov.cdc.nbs.deduplication.config.auth.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.MappedQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import gov.cdc.nbs.deduplication.config.auth.user.NbsUserDetailsService.UserInformation;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class NbsUserDetailsServiceTest {

  @Mock
  private JdbcClient client;

  @InjectMocks
  private NbsUserDetailsService userDetailsService;

  @Test
  void should_authenticate() {
    // Mock db info query
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    MappedQuerySpec<UserInformation> querySpec = Mockito.mock(MappedQuerySpec.class);

    when(client.sql(NbsUserDetailsService.SELECT_USER_INFORMATION)).thenReturn(statementSpec);
    when(statementSpec.param("userName", "user")).thenReturn(statementSpec);
    when(statementSpec.query(UserInformation.class)).thenReturn(querySpec);
    when(querySpec.optional())
        .thenReturn(
            Optional.of(
                new UserInformation(
                    13,
                    "John",
                    "Bobby",
                    "user",
                    true)));

    // Mock db authorities query
    StatementSpec authorityStatementSpec = Mockito.mock(StatementSpec.class);
    MappedQuerySpec<GrantedAuthority> authorityQuerySpec = Mockito.mock(MappedQuerySpec.class);
    when(client.sql(NbsUserDetailsService.SELECT_GRANTED_AUTHORITIES)).thenReturn(authorityStatementSpec);
    when(authorityStatementSpec.param("identifier", 13L)).thenReturn(authorityStatementSpec);
    when(authorityStatementSpec.query(Mockito.any(RowMapper.class)))
        .thenReturn(authorityQuerySpec);
    when(authorityQuerySpec.set()).thenReturn(Set.of(new SimpleGrantedAuthority("FIND-PATIENT")));

    // Act
    PreAuthenticatedAuthenticationToken token = userDetailsService.authenticateByUsername("user");

    // Verify
    assertThat(token).isNotNull();
    assertThat(token.getAuthorities()).containsExactly(new SimpleGrantedAuthority("FIND-PATIENT"));
    NbsUserDetails userDetails = (NbsUserDetails) token.getPrincipal();
    assertThat(userDetails.getId()).isEqualTo(13L);
    assertThat(userDetails.getFirstName()).isEqualTo("John");
    assertThat(userDetails.getLastName()).isEqualTo("Bobby");
    assertThat(userDetails.getUsername()).isEqualTo("user");
    assertThat(userDetails.isEnabled()).isTrue();

    assertThat(userDetails.getId()).isEqualTo(13L);
    assertThat(userDetails.isAccountNonExpired()).isTrue();
    assertThat(userDetails.isAccountNonLocked()).isTrue();
    assertThat(userDetails.isCredentialsNonExpired()).isTrue();
  }

  @Test
  void should_not_authenticate() {
    // Mock db info query
    StatementSpec statementSpec = Mockito.mock(StatementSpec.class);
    MappedQuerySpec<UserInformation> querySpec = Mockito.mock(MappedQuerySpec.class);

    when(client.sql(NbsUserDetailsService.SELECT_USER_INFORMATION)).thenReturn(statementSpec);
    when(statementSpec.param("userName", "user")).thenReturn(statementSpec);
    when(statementSpec.query(UserInformation.class)).thenReturn(querySpec);
    when(querySpec.optional()).thenReturn(Optional.empty());

    UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
        () -> userDetailsService.authenticateByUsername("user"));
    assertThat(ex.getMessage()).isEqualTo("Username not found");
  }

}
