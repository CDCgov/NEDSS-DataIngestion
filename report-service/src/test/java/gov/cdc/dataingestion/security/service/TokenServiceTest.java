package gov.cdc.dataingestion.security.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import static org.mockito.Mockito.*;

public class TokenServiceTest {

    private  JwtEncoder mockJwtencoder;

   private TokenService tokenService;

   @BeforeEach
    public void SetUp()
   {
       mockJwtencoder = mock(JwtEncoder.class);
       tokenService = new TokenService(mockJwtencoder);
   }

   @Test
    public  void GenerateTokenTest()
   {
       Authentication mockAuthentication = mock(Authentication.class);
       when(mockAuthentication.getName()).thenReturn("nbsuser");
       List<SimpleGrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority("PERM_FOO_READ"), new SimpleGrantedAuthority("ROLE_USER"));
       doReturn(grantedAuthorities).when(mockAuthentication).getAuthorities();

       var mockJwt = mock(Jwt.class);
       when(mockJwt.getTokenValue()).thenReturn("encodedToken");
       when(mockJwtencoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

      var encodedToken = tokenService.generateToken(mockAuthentication);
       Assert.assertEquals("encodedToken",encodedToken);
   }

}
