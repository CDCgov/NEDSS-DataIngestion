package gov.cdc.dataprocessing.service.interfaces.cache;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;

import java.util.List;

public interface ICatchingValueDpService {
    String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException;
    String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException;
    String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException;
    StateCode findStateCodeByStateNm(String stateNm);
    String getCodedValue(String pType, String pKey) throws DataProcessingException;
    boolean checkCodedValue(String pType, String pKey) throws DataProcessingException;
    String getCodedValuesCallRepos(String pType) throws DataProcessingException;
    List<CodeValueGeneral> getGeneralCodedValue(String code);
    String getCodedValue(String code) throws DataProcessingException;
}
