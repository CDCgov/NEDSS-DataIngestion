package gov.cdc.dataprocessing.service.interfaces.uid_generator;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.dto.uid.LocalUidModel;

public interface IOdseIdGeneratorWCacheService {
    LocalUidModel getValidLocalUid(LocalIdClass localIdClass, boolean gaApplied) throws DataProcessingException;
//    LocalUidModel getValidLocalUidByApi(LocalIdClass localIdClass, boolean gaApplied);
}
