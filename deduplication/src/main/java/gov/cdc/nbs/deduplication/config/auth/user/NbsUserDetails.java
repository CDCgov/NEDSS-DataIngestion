package gov.cdc.nbs.deduplication.config.auth.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Set;

public class NbsUserDetails implements UserDetails {
  private final Long id;
  private final String username;
  private final String firstName;
  private final String lastName;
  private final Set<GrantedAuthority> authorities;
  private final boolean isEnabled;

  public NbsUserDetails(
      final long id,
      final String username,
      final String firstName,
      final String lastName,
      final Set<GrantedAuthority> authorities,
      boolean isEnabled) {
    this.id = id;
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.authorities = authorities;
    this.isEnabled = isEnabled;
  }

  public Long getId() {
    return this.id;
  }

  public String getUsername() {
    return this.username;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public Set<GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public boolean isEnabled() {
    return this.isEnabled;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return isEnabled;
  }

  @Override
  public boolean isAccountNonLocked() {
    return isAccountNonExpired();
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return isAccountNonExpired();
  }
}