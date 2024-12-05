package gov.cdc.rticache.cache_model;

import gov.cdc.rticache.model.dto.LocalUidModel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalUidCacheModel {
    public static final Integer SEED_COUNTER = 1000;
    public static final ConcurrentMap<String, LocalUidModel> localUidMap = new ConcurrentHashMap<>();
}
