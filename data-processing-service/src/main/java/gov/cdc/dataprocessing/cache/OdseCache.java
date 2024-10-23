package gov.cdc.dataprocessing.cache;

import java.util.Map;
import java.util.TreeMap;

/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
 1118 - Private constructor complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118"})
public class OdseCache {
    public static Map<Object,Object> fromPrePopFormMapping = new TreeMap<Object, Object>();
    public static Map<Object,Object> toPrePopFormMapping = new TreeMap<Object, Object>();
    public static Map<Object,Object> dmbMap = new TreeMap<Object, Object>();
    public static Map<Object,Object> map = new TreeMap<Object, Object>();


}
