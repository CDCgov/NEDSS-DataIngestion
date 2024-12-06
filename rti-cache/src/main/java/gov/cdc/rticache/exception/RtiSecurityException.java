package gov.cdc.rticache.exception;

import org.springframework.security.core.AuthenticationException;

public class RtiSecurityException extends AuthenticationException {
    public RtiSecurityException(String message) {
        super(message);
    }
}