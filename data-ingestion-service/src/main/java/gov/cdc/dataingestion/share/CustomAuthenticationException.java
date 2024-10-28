package gov.cdc.dataingestion.share;
import org.springframework.security.core.AuthenticationException;
/**
 1118 - require constructor complaint
 * */
@SuppressWarnings({"java:S1118",""})
public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String message) {
        super(message);
    }
}