package gov.cdc.rticache.service.interfaces;

import gov.cdc.rticache.constant.LocalIdClass;
import gov.cdc.rticache.exception.RtiCacheException;
import gov.cdc.rticache.model.dto.LocalUidModel;

public interface IOdseIdGeneratorWCacheService {
    LocalUidModel getValidLocalUid(LocalIdClass localIdClass, boolean gaApplied) throws RtiCacheException;
}
