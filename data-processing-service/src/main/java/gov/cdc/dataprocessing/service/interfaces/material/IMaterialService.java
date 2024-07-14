package gov.cdc.dataprocessing.service.interfaces.material;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.model.MaterialContainer;

public interface IMaterialService {
    MaterialContainer loadMaterialObject(Long materialUid);

    Long saveMaterial(MaterialContainer materialContainer) throws DataProcessingException;
}
