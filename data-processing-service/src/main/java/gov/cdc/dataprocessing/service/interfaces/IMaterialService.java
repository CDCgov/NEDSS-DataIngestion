package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.MaterialVO;

public interface IMaterialService {
    MaterialVO loadMaterialObject(Long materialUid);
    Long saveMaterial(MaterialVO materialVO) throws DataProcessingException;
}
