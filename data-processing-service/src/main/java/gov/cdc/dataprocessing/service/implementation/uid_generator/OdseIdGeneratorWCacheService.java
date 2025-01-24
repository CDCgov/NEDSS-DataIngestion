//package gov.cdc.dataprocessing.service.implementation.uid_generator;
//
//import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
//import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;
//import gov.cdc.dataprocessing.service.interfaces.cache.ICacheApiService;
//import gov.cdc.dataprocessing.service.interfaces.uid_generator.localUid.IOdseIdGeneratorWCacheService;
//import gov.cdc.dataprocessing.utilities.GsonUtil;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OdseIdGeneratorWCacheService implements IOdseIdGeneratorWCacheService {
//    private final ICacheApiService cacheApiService;
//
//    public OdseIdGeneratorWCacheService(ICacheApiService cacheApiService) {
//        this.cacheApiService = cacheApiService;
//    }
//
//    public LocalUidModel getValidLocalUid(LocalIdClass localIdClass, boolean gaApplied) {
//        var res = cacheApiService.getOdseLocalId(localIdClass.name(), gaApplied);
//        return GsonUtil.GSON.fromJson(res, LocalUidModel.class);
//    }
//}
