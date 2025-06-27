package gov.cdc.nbs.deduplication.config.auth.user;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class NbsUserDetailsService implements UserDetailsService {

  private final JdbcClient client;

  public NbsUserDetailsService(@Qualifier("nbsJdbcClient") final JdbcClient client) {
    this.client = client;
  }

  static final String SELECT_USER_INFORMATION = """
      SELECT
        nedss_entry_id AS 'identifier',
        user_first_nm AS 'first',
        user_last_nm AS 'last',
        USER_ID AS 'username',
        CASE record_status_cd
          WHEN 'ACTIVE' THEN 1
          ELSE 0
        END AS 'enabled'
      FROM
        Auth_user
      WHERE
        USER_ID = :userName;
      """;

  static final String SELECT_GRANTED_AUTHORITIES = """
      SELECT DISTINCT
        operationType.bus_op_nm + '-' + objectType.bus_obj_nm
      FROM
        auth_user authUser
        JOIN auth_user_role role ON role.auth_user_uid = authUser.auth_user_uid
        JOIN auth_perm_set permissionSet ON role.auth_perm_set_uid = permissionSet.auth_perm_set_uid
        JOIN auth_bus_obj_rt objectRight ON objectRight.auth_perm_set_uid = permissionSet.auth_perm_set_uid
        JOIN auth_bus_obj_type objectType ON objectRight.auth_bus_obj_type_uid = objectType.auth_bus_obj_type_uid
        JOIN auth_bus_op_rt operationRight ON operationRight.auth_bus_obj_rt_uid = objectRight.auth_bus_obj_rt_uid
        JOIN auth_bus_op_type operationType ON operationType.auth_bus_op_type_uid = operationRight.auth_bus_op_type_uid
      WHERE
        authUser.nedss_entry_id = :identifier
        AND NOT (
          role.role_guest_ind = 'T'
          AND isNull(operationRight.bus_op_guest_rt, 'F') = 'F'
        );
        """;

  public PreAuthenticatedAuthenticationToken authenticateByUsername(final String username) {
    NbsUserDetails userDetails = loadUserByUsername(username);
    return new PreAuthenticatedAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities());
  }

  @Override
  public NbsUserDetails loadUserByUsername(final String username) {
    return findNbsUser(username)
        .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
  }

  private Optional<NbsUserDetails> findNbsUser(final String userName) {
    return client.sql(SELECT_USER_INFORMATION)
        .param("userName", userName)
        .query(UserInformation.class)
        .optional()
        .map(this::asUserDetails);
  }

  private Set<GrantedAuthority> findGrantedAuthorities(final long identifier) {
    return client.sql(SELECT_GRANTED_AUTHORITIES)
        .param("identifier", identifier)
        .query((rs, rowNum) -> (GrantedAuthority) new SimpleGrantedAuthority(rs.getString(1)))
        .set();
  }

  private NbsUserDetails asUserDetails(final UserInformation userInformation) {
    Set<GrantedAuthority> authorities = findGrantedAuthorities(userInformation.identifier());

    return new NbsUserDetails(
        userInformation.identifier(),
        userInformation.username(),
        userInformation.first(),
        userInformation.last(),
        authorities,
        userInformation.enabled());
  }

  record UserInformation(
      long identifier,
      String first,
      String last,
      String username,
      boolean enabled) {
  }
}
