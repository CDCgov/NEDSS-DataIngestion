package gov.cdc.nbsDedup.service.interfaces.uid_generator;

import gov.cdc.nbsDedup.constant.enums.LocalIdClass;
import gov.cdc.nbsDedup.exception.DataProcessingException;
import gov.cdc.nbsDedup.nbs.odse.model.generic_helper.LocalUidGenerator;



public interface IOdseIdGeneratorService {
    LocalUidGenerator getLocalIdAndUpdateSeed(LocalIdClass localIdClass) throws DataProcessingException;
}
