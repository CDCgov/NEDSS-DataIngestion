package gov.cdc.dataprocessing.utilities;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueIdGenerator {
    private static final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    public static long generateUniqueId() {
        return counter.incrementAndGet();
    }

    public static String generateUniqueStringId() {
        long uniqueNumber = counter.incrementAndGet();
        return String.valueOf(uniqueNumber);
    }

}
