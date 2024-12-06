package gov.cdc.dataprocessing.service.interfaces.cache;

public interface ICacheApiService {
    String getSrteCacheString(String objectName, String key);
    String getSrteCacheObject(String objectName, String key);
    Boolean getSrteCacheBool(String objectName, String key);
    String getOdseLocalId(String objectName, boolean geApplied);
}
