package gov.cdc.dataprocessing.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
    public static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        Throwable rootCause = getRootCause(throwable);
        rootCause.printStackTrace(printWriter);
        return stringWriter.toString(); // Converts the entire stack trace to a string
    }

    private static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        // Traverse the cause until the root is found
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

}
