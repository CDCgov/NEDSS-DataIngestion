package gov.cdc.dataprocessing.service.interfaces.cache;


import gov.cdc.dataprocessing.exception.RtiCacheException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.CodeValueGeneral;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ElrXref;
import gov.cdc.dataprocessing.repository.nbs.srte.model.StateCode;

import java.util.HashMap;
import java.util.List;


public interface ICatchingValueService {
    HashMap<String, String> getRaceCodes() throws RtiCacheException;
    String getCodeDescTxtForCd(String code, String codeSetNm) throws RtiCacheException;
    String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws RtiCacheException;
    String getCountyCdByDesc(String county, String stateCd) throws RtiCacheException;
    HashMap<String, String>  getAOELOINCCodes() throws RtiCacheException;
    HashMap<String, String> getCodedValues(String pType, String key) throws RtiCacheException;
    List<CodeValueGeneral> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code);
    StateCode findStateCodeByStateNm(String stateNm);
    HashMap<String, String> getAllJurisdictionCode() throws RtiCacheException;
    HashMap<String, String> getAllProgramAreaCodes() throws RtiCacheException;
    HashMap<String, Integer> getAllProgramAreaCodesWithNbsUid() throws RtiCacheException;
    HashMap<String, Integer> getAllJurisdictionCodeWithNbsUid() throws RtiCacheException;
    List<ElrXref> getAllElrXref() throws RtiCacheException;
    HashMap<String, String> getAllOnInfectionConditionCode() throws RtiCacheException;
    List<ConditionCode> getAllConditionCode() throws RtiCacheException;
    HashMap<String, String> getCodedValue(String code) throws RtiCacheException;
    HashMap<String, String> getCodedValuesCallRepos(String pType) throws RtiCacheException;
    HashMap<String, String> getLabResultDesc() throws RtiCacheException;
    HashMap<String, String> getAllSnomedCode() throws RtiCacheException;
    HashMap<String, String> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd() throws RtiCacheException;
    HashMap<String, String> getAllLoinCodeWithComponentName() throws RtiCacheException;
}