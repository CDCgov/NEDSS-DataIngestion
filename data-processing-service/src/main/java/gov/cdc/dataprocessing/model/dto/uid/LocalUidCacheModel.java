package gov.cdc.dataprocessing.model.dto.uid;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalUidCacheModel {
    public static final Integer SEED_COUNTER =  1000;

    public static ConcurrentMap<String, LocalUidModel> localUidMap = new ConcurrentHashMap<>();
}
