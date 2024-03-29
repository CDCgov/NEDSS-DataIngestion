package gov.cdc.dataprocessing.service.interfaces.other;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;

import java.util.List;
import java.util.TreeMap;

public interface ICheckingValueService {
   // TreeMap<String, String> getCodedValues(String pType) throws DataProcessingException;
    TreeMap<String, String> getRaceCodes() throws DataProcessingException;
    String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException;
    String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException;
    String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException;
    TreeMap<String, String>  getAOELOINCCodes() throws DataProcessingException;
    TreeMap<String, String> getCodedValues(String pType, String key) throws DataProcessingException;
    List<CodeValueGeneral> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code);
    StateCode findStateCodeByStateNm(String stateNm);
}
