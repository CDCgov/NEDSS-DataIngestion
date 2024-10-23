package gov.cdc.dataprocessing.model.dto.uid;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186"})
public class LocalUidCacheModel {
    public static final Integer SEED_COUNTER = 1000;
    public static final ConcurrentMap<String, LocalUidModel> localUidMap = new ConcurrentHashMap<>();
}
