package gov.cdc.dataprocessing.exception;

import org.springframework.security.core.AuthenticationException;

public class DataProcessingSecurityException extends AuthenticationException {
    public DataProcessingSecurityException(String message) {
        super(message);
    }
}