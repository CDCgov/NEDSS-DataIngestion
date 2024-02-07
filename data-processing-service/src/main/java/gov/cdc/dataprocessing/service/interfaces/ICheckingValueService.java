package gov.cdc.dataprocessing.service.interfaces;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.LOINCCode;

import java.util.List;
import java.util.TreeMap;

public interface ICheckingValueService {
    TreeMap<String, String> getCodedValues(String pType) throws DataProcessingException;
    TreeMap<String, String> getRaceCodes() throws DataProcessingException;
    String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException;
    String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException;
    String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException;
    TreeMap<String, String>  getAOELOINCCodes() throws DataProcessingException;
}
