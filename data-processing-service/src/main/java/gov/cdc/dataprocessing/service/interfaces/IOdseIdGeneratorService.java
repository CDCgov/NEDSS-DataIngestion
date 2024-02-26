package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.repository.nbs.odse.model.LocalUidGenerator;

public interface IOdseIdGeneratorService {
    LocalUidGenerator getLocalIdAndUpdateSeed(LocalIdClass localIdClass);
}
