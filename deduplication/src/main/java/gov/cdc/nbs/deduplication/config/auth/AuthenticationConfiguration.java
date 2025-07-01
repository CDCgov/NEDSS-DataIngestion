package gov.cdc.nbs.deduplication.config.auth;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableConfigurationProperties({ AuthenticationConfiguration.PathSettings.class })
public class AuthenticationConfiguration {

  public interface AuthenticationConfigurer {
    @SuppressWarnings("java:S112")
    HttpSecurity configure(final HttpSecurity http) throws Exception;
  }

  @ConfigurationProperties(prefix = "nbs.security.paths")
  record PathSettings(List<String> ignored) {
  }

  @Bean
  IgnoredPaths configuredIgnoredPaths(final PathSettings settings) {
    return new IgnoredPaths(settings.ignored());
  }

}
