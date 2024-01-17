package gov.cdc.dataingestion.share;
import org.springframework.security.core.AuthenticationException;
public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String message) {
        super(message);
    }
}