package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.classic_model_move_as_needed.vo.PublicHealthCaseVO;
import gov.cdc.dataprocessing.model.container.BasePamContainer;
import gov.cdc.dataprocessing.model.container.PamProxyContainer;

public interface IPamService {

    Long setPamProxyWithAutoAssoc(PamProxyContainer pamProxyVO, Long observationUid, String observationTypeCd) throws DataProcessingException;
    void insertPamVO(BasePamContainer pamVO, PublicHealthCaseVO publichHealthCaseVO) throws DataProcessingException;
}
