package gov.cdc.dataprocessing.cache;

import java.util.concurrent.atomic.AtomicBoolean;

public class DpStatic {
    private static final AtomicBoolean uuidPoolInitialized = new AtomicBoolean(false);

    public static boolean isUuidPoolInitialized() {
        return uuidPoolInitialized.get();
    }

    public static void setUuidPoolInitialized(boolean value) {
        uuidPoolInitialized.set(value);
    }

    public static boolean compareAndSetUuidPoolInitialized(boolean expected, boolean newValue) {
        return uuidPoolInitialized.compareAndSet(expected, newValue);
    }
}
