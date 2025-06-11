package gov.cdc.dataprocessing.service.interfaces.cache;

import gov.cdc.dataprocessing.exception.DataProcessingException;

public interface ICacheApiService {
    String getSrteCacheString(String objectName, String key) throws DataProcessingException;
    Object getSrteCacheObject(String objectName, String key);
    Boolean getSrteCacheBool(String objectName, String key) throws DataProcessingException;
}
