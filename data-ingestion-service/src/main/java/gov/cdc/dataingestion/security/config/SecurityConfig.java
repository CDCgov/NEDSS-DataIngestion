package gov.cdc.dataingestion.security.config;

import gov.cdc.dataingestion.share.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${auth.introspect-uri}")
    String introspectionUri;
    @Value("${auth.client-id}")
    String clientId;
    @Value("${auth.client-secret}")
    String clientSecret;
    private static final String[] AUTH_WHITELIST = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/actuator/**",
            "/actuator/prometheus",
            "/actuator/prometheus/**",
            "/swagger-ui/**",
            "/token",
            "/test/token"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests(auth -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated());

        http.oauth2ResourceServer(oauth2 -> oauth2
                .opaqueToken(opaque -> opaque
                        .introspectionUri(this.introspectionUri)
                        .introspectionClientCredentials(this.clientId, this.clientSecret)
                ));
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.oauth2ResourceServer().authenticationEntryPoint(new CustomAuthenticationEntryPoint()).and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        return http.build();
    }
}