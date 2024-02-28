package gov.cdc.dataprocessing.service.interfaces.core;

import gov.cdc.dataprocessing.constant.enums.LocalIdClass;
import gov.cdc.dataprocessing.repository.nbs.odse.model.other_move_as_needed.LocalUidGenerator;

public interface IOdseIdGeneratorService {
    LocalUidGenerator getLocalIdAndUpdateSeed(LocalIdClass localIdClass);
}
