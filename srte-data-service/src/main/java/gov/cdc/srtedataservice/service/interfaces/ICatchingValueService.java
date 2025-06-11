package gov.cdc.srtedataservice.service.interfaces;

import gov.cdc.srtedataservice.exception.DataProcessingException;
import gov.cdc.srtedataservice.exception.RtiCacheException;
import gov.cdc.srtedataservice.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.srtedataservice.repository.nbs.srte.model.ConditionCode;
import gov.cdc.srtedataservice.repository.nbs.srte.model.ElrXref;
import gov.cdc.srtedataservice.repository.nbs.srte.model.StateCode;

import java.util.HashMap;
import java.util.List;


public interface ICatchingValueService {
    HashMap<String, String> getRaceCodes() throws DataProcessingException;
    String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException;
    String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException;
    String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException;
    HashMap<String, String>  getAOELOINCCodes() throws DataProcessingException;
    HashMap<String, String> getCodedValues(String pType, String key) throws DataProcessingException;
    List<CodeValueGeneral> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code);
    StateCode findStateCodeByStateNm(String stateNm);
    HashMap<String, String> getAllJurisdictionCode() throws DataProcessingException;
    HashMap<String, String> getAllProgramAreaCodes() throws DataProcessingException;
    HashMap<String, Integer> getAllProgramAreaCodesWithNbsUid() throws DataProcessingException;
    HashMap<String, Integer> getAllJurisdictionCodeWithNbsUid() throws DataProcessingException;
    List<ElrXref> getAllElrXref() throws DataProcessingException, RtiCacheException;
    HashMap<String, String> getAllOnInfectionConditionCode() throws DataProcessingException;
    List<ConditionCode> getAllConditionCode() throws DataProcessingException;
    HashMap<String, String> getCodedValue(String code) throws DataProcessingException;
    HashMap<String, String> getCodedValuesCallRepos(String pType) throws DataProcessingException;
    HashMap<String, String> getLabResultDesc() throws DataProcessingException;
    HashMap<String, String> getAllSnomedCode() throws DataProcessingException;
    HashMap<String, String> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd() throws DataProcessingException;
    HashMap<String, String> getAllLoinCodeWithComponentName() throws DataProcessingException;
}
