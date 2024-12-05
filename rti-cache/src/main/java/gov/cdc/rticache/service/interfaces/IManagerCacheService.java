package gov.cdc.rticache.service.interfaces;

import gov.cdc.rticache.constant.ObjectName;
import gov.cdc.rticache.exception.RtiCacheException;

public interface IManagerCacheService {
    String getCache(ObjectName objectName, String key) throws RtiCacheException;
    boolean containKey(ObjectName objectName, String key) throws RtiCacheException;
    Object getCacheObject(ObjectName objectName, String key);
}
