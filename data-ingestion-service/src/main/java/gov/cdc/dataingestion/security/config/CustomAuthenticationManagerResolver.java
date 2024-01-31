package gov.cdc.dataingestion.security.config;

import gov.cdc.dataingestion.share.CustomAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.Enumeration;

@Component
public class CustomAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {
    @Value("${auth.introspect-uri}")
    String introspectionUri;
    @Override
    public AuthenticationManager resolve(HttpServletRequest request){
        String clientId = request.getHeader("clientid");
        String clientSecret = request.getHeader("clientsecret");
        if(introspectionUri ==null || introspectionUri.isEmpty()){
            throw new CustomAuthenticationException("Introspection URI is required");
        }
        if(clientId ==null || clientId.isEmpty() || clientSecret ==null || clientSecret.isEmpty()){
            throw new CustomAuthenticationException("Client ID and Client Secret are required");
        }
        OpaqueTokenIntrospector opaquetokenintrospector;
        opaquetokenintrospector =  new NimbusOpaqueTokenIntrospector(
                introspectionUri,
                clientId,
                clientSecret);
        return new OpaqueTokenAuthenticationProvider(opaquetokenintrospector)::authenticate;
    }
}