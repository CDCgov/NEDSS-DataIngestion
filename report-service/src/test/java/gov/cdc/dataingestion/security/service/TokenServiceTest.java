package gov.cdc.dataingestion.security.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    JwtEncoder jwtEncoderMock;
    Jwt jwtMock;
    TokenService tokenService;
    Authentication authentication;
    JwtDecoder jwtDecoderMock;
    String expectedToken = "testToken";

    @BeforeEach
    void setUp() {
        jwtEncoderMock = mock(JwtEncoder.class);
        jwtMock = mock(Jwt.class);
        tokenService = new TokenService(jwtEncoderMock);
        jwtDecoderMock = mock(JwtDecoder.class);
        authentication = new UsernamePasswordAuthenticationToken("username", "password");
        when(jwtMock.getTokenValue()).thenReturn("testToken");
        when(jwtEncoderMock.encode(any())).thenReturn(jwtMock);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(jwtMock);
        Mockito.reset(jwtEncoderMock);
        Mockito.reset(jwtDecoderMock);
    }

    @Test
    void testGenerateToken() {
        String generatedToken = tokenService.generateToken(authentication);

        assertNotNull(generatedToken);
        assertEquals(expectedToken, generatedToken);
        verify(jwtEncoderMock, times(1)).encode(any());
        verify(jwtMock, times(1)).getTokenValue();
    }

    @Test
    void testGenerateTokenClaims() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        String generatedToken = tokenService.generateToken(authentication);

        assertNotNull(generatedToken);
        verify(jwtEncoderMock, times(1)).encode(any());
        verify(jwtMock, times(1)).getTokenValue();

        when(jwtDecoderMock.decode(anyString())).thenReturn(jwtMock);
        when(jwtMock.getClaim("sub")).thenReturn("username");
        when(jwtMock.getClaim("scope")).thenReturn("USER");
        when(jwtMock.getClaim("issuer")).thenReturn("self");

        Jwt decodedToken = jwtDecoderMock.decode(generatedToken);
        assertEquals("username", decodedToken.getClaim("sub"));
        assertEquals("self", decodedToken.getClaim("issuer"));
    }

    @Test
    void testVerifyTokenExpiry() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        String generatedToken = tokenService.generateToken(authentication);

        assertNotNull(generatedToken);
        verify(jwtEncoderMock, times(1)).encode(any());
        verify(jwtMock, times(1)).getTokenValue();

        when(jwtDecoderMock.decode(anyString())).thenReturn(jwtMock);
        when(jwtMock.getExpiresAt()).thenReturn(Instant.now().minus(1, ChronoUnit.HOURS));

        Jwt decodedToken = jwtDecoderMock.decode(generatedToken);
        Instant currentTime = Instant.now();

        assertTrue(decodedToken.getExpiresAt().isBefore(currentTime));
    }
}