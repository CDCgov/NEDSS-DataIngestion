package gov.cdc.srtedataservice.service.interfaces;

import gov.cdc.srtedataservice.constant.LocalIdClass;
import gov.cdc.srtedataservice.exception.DataProcessingException;
import gov.cdc.srtedataservice.exception.RtiCacheException;
import gov.cdc.srtedataservice.model.dto.LocalUidModel;

public interface IOdseIdGeneratorWCacheService {
    LocalUidModel getValidLocalUid(LocalIdClass localIdClass, boolean gaApplied) throws DataProcessingException;
}
