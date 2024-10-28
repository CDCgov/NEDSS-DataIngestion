package gov.cdc.dataingestion.share;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126"})
class CustomAuthenticationExceptionTest {
    @Test
    void test_exception_custom() {
        Exception exception = assertThrows(
                CustomAuthenticationException.class,
                () -> validateClientId("testId"));

        assertTrue(exception.getMessage().contains("not found"));
    }

    String validateClientId(String clientId) throws CustomAuthenticationException {
        throw new CustomAuthenticationException(clientId + " not found!");
    }
}