package gov.cdc.dataprocessing.service.interfaces.cache;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;

import java.util.HashMap;
import java.util.List;

public interface ICatchingValueService {
    String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm);
    String getCodeDescTxtForCd(String code, String codeSetNm);
    String getCountyCdByDesc(String county, String stateCd);
    StateCode findStateCodeByStateNm(String stateNm);
    String getCodedValue(String pType, String pKey);
    boolean checkCodedValue(String pType, String pKey);
    String getCodedValuesCallRepos(String pType);
    List<CodeValueGeneral> getGeneralCodedValue(String code);
    String getCodedValue(String code);
}
