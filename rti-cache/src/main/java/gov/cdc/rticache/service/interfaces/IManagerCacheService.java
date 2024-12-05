package gov.cdc.rticache.service.interfaces;

import gov.cdc.rticache.constant.ObjectName;

public interface IManagerCacheService {
    String getCache(ObjectName objectName, String key);
    boolean containKey(ObjectName objectName, String key);
    Object getCacheObject(ObjectName objectName, String key);
}
