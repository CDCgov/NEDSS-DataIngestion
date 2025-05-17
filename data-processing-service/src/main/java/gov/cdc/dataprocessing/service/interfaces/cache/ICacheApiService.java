package gov.cdc.dataprocessing.service.interfaces.cache;

import gov.cdc.dataprocessing.exception.RtiCacheException;

public interface ICacheApiService {
    String getSrteCacheString(String objectName, String key) throws RtiCacheException;
    Object getSrteCacheObject(String objectName, String key);
    Boolean getSrteCacheBool(String objectName, String key) throws RtiCacheException;
    String getOdseLocalId(String objectName, boolean geApplied);
}
