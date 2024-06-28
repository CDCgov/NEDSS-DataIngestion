package gov.cdc.dataprocessing.service.interfaces.cache;

import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;

import java.util.List;
import java.util.TreeMap;

public interface ICatchingValueService {
   // TreeMap<String, String> getCodedValues(String pType) throws DataProcessingException;
    TreeMap<String, String> getRaceCodes() throws DataProcessingException;
    String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException;
    String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException;
    String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException;
    TreeMap<String, String>  getAOELOINCCodes() throws DataProcessingException;
    TreeMap<String, String> getCodedValues(String pType, String key) throws DataProcessingException;
    List<CodeValueGeneral> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code);
    StateCode findStateCodeByStateNm(String stateNm);
    TreeMap<String, String> getAllJurisdictionCode() throws DataProcessingException;
    TreeMap<String, String> getAllProgramAreaCodes() throws DataProcessingException;
    TreeMap<String, Integer> getAllProgramAreaCodesWithNbsUid() throws DataProcessingException;
    TreeMap<String, Integer> getAllJurisdictionCodeWithNbsUid() throws DataProcessingException;
    List<ElrXref> getAllElrXref() throws DataProcessingException;
    TreeMap<String, String> getAllOnInfectionConditionCode() throws DataProcessingException;
    List<ConditionCode> getAllConditionCode() throws DataProcessingException;
    TreeMap<String, String> getCodedValue(String code) throws DataProcessingException;
    List<CodeValueGeneral> getGeneralCodedValue(String code);
    TreeMap<String, String> getCodedValuesCallRepos(String pType) throws DataProcessingException;
    TreeMap<String, String> getLabResultDesc() throws DataProcessingException;
    TreeMap<String, String> getAllSnomedCode() throws DataProcessingException;
    TreeMap<String, String> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd() throws DataProcessingException;
    TreeMap<String, String> getAllLoinCodeWithComponentName() throws DataProcessingException;
}
