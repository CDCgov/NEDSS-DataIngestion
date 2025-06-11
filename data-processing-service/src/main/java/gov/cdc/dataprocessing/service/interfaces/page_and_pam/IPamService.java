package gov.cdc.dataprocessing.service.interfaces.page_and_pam;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.model.container.base.BasePamContainer;
import gov.cdc.dataprocessing.model.container.model.PamProxyContainer;
import gov.cdc.dataprocessing.model.container.model.PublicHealthCaseContainer;

public interface IPamService {

    Long setPamProxyWithAutoAssoc(PamProxyContainer pamProxyVO, Long observationUid, String observationTypeCd) throws DataProcessingException;
    void insertPamVO(BasePamContainer pamVO, PublicHealthCaseContainer publichHealthCaseVO) throws DataProcessingException;
}
