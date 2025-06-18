package gov.cdc.dataprocessing.service.implementation.cache;


import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
import org.springframework.stereotype.Service;

@Service
public class CacheApiService implements ICacheApiService {
    private final ManagerCacheService managerCacheService;

    public CacheApiService( ManagerCacheService managerCacheService) {
        this.managerCacheService = managerCacheService;
    }

    public String getSrteCacheString(String objectName, String key) throws DataProcessingException {
        return managerCacheService.getCache(ObjectName.valueOf(objectName), key);
    }

    public Object getSrteCacheObject(String objectName, String key) {
        return managerCacheService.getCacheObject(ObjectName.valueOf(objectName), key);
    }

    public Boolean getSrteCacheBool(String objectName, String key) throws DataProcessingException {
        return managerCacheService.containKey(ObjectName.valueOf(objectName), key);
    }

}
