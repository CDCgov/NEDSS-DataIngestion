package gov.cdc.srtedataservice.service.interfaces;

import gov.cdc.srtedataservice.constant.ObjectName;
import gov.cdc.srtedataservice.exception.RtiCacheException;

public interface IManagerCacheService {
    String getCache(ObjectName objectName, String key) throws RtiCacheException;
    boolean containKey(ObjectName objectName, String key) throws RtiCacheException;
    Object getCacheObject(ObjectName objectName, String key);
}
