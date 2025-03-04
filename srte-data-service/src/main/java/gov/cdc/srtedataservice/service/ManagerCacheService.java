package gov.cdc.srtedataservice.service;

import gov.cdc.srtedataservice.cache_model.SrteCache;
import gov.cdc.srtedataservice.constant.ObjectName;
import gov.cdc.srtedataservice.exception.RtiCacheException;
import gov.cdc.srtedataservice.repository.nbs.srte.model.ConditionCode;
import gov.cdc.srtedataservice.service.interfaces.ICatchingValueService;
import gov.cdc.srtedataservice.service.interfaces.IManagerCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ManagerCacheService implements IManagerCacheService {
    private final ICatchingValueService cachingValueService;

    public ManagerCacheService(ICatchingValueService cachingValueService) {
        this.cachingValueService = cachingValueService;
    }

    public Object getCacheObject(ObjectName objectName, String key) {
        if (objectName == ObjectName.CONDITION_CODE)
        {
            return SrteCache.findConditionCodeByDescription(key);
        }
        else if (objectName == ObjectName.ELR_XREF)
        {
            String[] parts = key.split("~");
            String fromCodeSetNm = parts.length > 0 ? parts[0] : "";
            String fromCode = parts.length > 1 ? parts[1] : "";
            String toCodeSetNm = parts.length > 2 ? parts[2] : "";
            return SrteCache.findRecordForElrXrefsList(fromCodeSetNm, fromCode, toCodeSetNm);
        }
        else if (objectName == ObjectName.FIND_STATE_CODE_BY_STATE_NM) {
            return cachingValueService.findStateCodeByStateNm(key);
        }
        return null;
    }
    public String getCache(ObjectName objectName, String key) throws RtiCacheException {
        if (objectName == ObjectName.PROGRAM_AREA_CODES) {
            return SrteCache.programAreaCodesMap.get(key);
        }
        else  if (objectName == ObjectName.JURISDICTION_CODES)
        {
            return SrteCache.jurisdictionCodeMap.get(key);
        }
        else  if (objectName == ObjectName.PROGRAM_AREA_CODES_WITH_NBS_UID)
        {
            return SrteCache.programAreaCodesMapWithNbsUid.get(key).toString();
        }
        else  if (objectName == ObjectName.JURISDICTION_CODE_WITH_NBS_UID)
        {
            return SrteCache.jurisdictionCodeMapWithNbsUid.get(key).toString();
        }
        else  if (objectName == ObjectName.LAB_RESULT_DESC)
        {
            return SrteCache.labResultByDescMap.get(key);
        }
        else  if (objectName == ObjectName.SNOMED_CODE_BY_DESC)
        {
            return SrteCache.snomedCodeByDescMap.get(key);
        }
        else  if (objectName == ObjectName.LAB_RESULT_DESC_WITH_ORGANISM_NAME)
        {
            return SrteCache.labResultWithOrganismNameIndMap.get(key);
        }
        else  if (objectName == ObjectName.LOIN_CODE_WITH_COMPONENT_NAME)
        {
            return SrteCache.loinCodeWithComponentNameMap.get(key);
        }
        else  if (objectName == ObjectName.INVESTIGATION_FORM_CONDITION_CODE)
        {
            return SrteCache.investigationFormConditionCode.get(key);
        }
        else if (objectName == ObjectName.FIND_TO_CODE) {
            String[] parts = key.split("~");
            String fromCodeSetNm = parts.length > 0 ? parts[0] : "";
            String fromCode = parts.length > 1 ? parts[1] : "";
            String toCodeSetNm = parts.length > 2 ? parts[2] : "";
            return cachingValueService.findToCode(fromCodeSetNm, fromCode, toCodeSetNm);
        }
        else if (objectName == ObjectName.GET_CODE_DESC_TXT_FOR_CD) {
            String[] parts = key.split("~");
            String code = parts.length > 0 ? parts[0] : "";
            String codeStNm = parts.length > 1 ? parts[1] : "";
            return cachingValueService.getCodeDescTxtForCd(code, codeStNm);
        }
        else if (objectName == ObjectName.GET_COUNTY_CD_BY_DESC) {
            String[] parts = key.split("~");
            String countyCode = parts.length > 0 ? parts[0] : "";
            String stateCode = parts.length > 1 ? parts[1] : "";
            return cachingValueService.getCountyCdByDesc(countyCode, stateCode);

        }
        else if (objectName == ObjectName.CODED_VALUE) {
            String[] parts = key.split("~");
            String pType = parts.length > 0 ? parts[0] : "";
            String pKey = parts.length > 1 ? parts[1] : "";
            var res = cachingValueService.getCodedValues(pType, pKey);
            return res.get(pKey);
        }
        else if (objectName == ObjectName.GET_CODED_VALUES_CALL_REPOS) {
            return cachingValueService.getCodedValuesCallRepos(key).get(key);
        }
        else if (objectName == ObjectName.GET_CODED_VALUE) {
            var res = cachingValueService.getCodedValue(key);
            return res.get(key);
        }
        else if (objectName == ObjectName.JURISDICTION_CODE_MAP_WITH_NBS_UID_KEY_SET) {
            return String.join(", ", SrteCache.jurisdictionCodeMapWithNbsUid.keySet());
        }
        return "";
    }

    public boolean containKey(ObjectName objectName, String key) throws RtiCacheException {
        if (objectName == ObjectName.LOINC_CODES)
        {
            return SrteCache.loincCodesMap.containsKey(key);
        }
        else  if (objectName == ObjectName.RACE_CODES)
        {
            return SrteCache.raceCodesMap.containsKey(key);
        }
        else  if (objectName == ObjectName.CO_INFECTION_CONDITION_CODE)
        {
            return SrteCache.coInfectionConditionCode.containsKey(key);
        }
        else  if (objectName == ObjectName.INVESTIGATION_FORM_CONDITION_CODE)
        {
            return SrteCache.investigationFormConditionCode.containsKey(key);
        }
        else if (objectName == ObjectName.CHECK_PAI_FOR_STD_OR_HIV) {
            return SrteCache.checkWhetherPAIsStdOrHiv(key);
        }
        else if (objectName == ObjectName.PROGRAM_AREA_CODES) {
            return SrteCache.programAreaCodesMap.containsKey(key);
        }
        else  if (objectName == ObjectName.JURISDICTION_CODES)
        {
            return SrteCache.jurisdictionCodeMap.containsKey(key);
        }
        else if (objectName == ObjectName.CODED_VALUE) {
            String[] parts = key.split("~");
            String pType = parts.length > 0 ? parts[0] : "";
            String pKey = parts.length > 1 ? parts[1] : "";
            var res = cachingValueService.getCodedValues(pType, pKey);
            return res.containsKey(pKey);
        }
        return false;
    }

    @PostConstruct
    public void loadCache() throws RtiCacheException {
        SrteCache.loincCodesMap = cachingValueService.getAOELOINCCodes();
        SrteCache.raceCodesMap = cachingValueService.getRaceCodes();
        SrteCache.programAreaCodesMap = cachingValueService.getAllProgramAreaCodes();
        SrteCache.jurisdictionCodeMap = cachingValueService.getAllJurisdictionCode();
        SrteCache.jurisdictionCodeMapWithNbsUid = cachingValueService.getAllJurisdictionCodeWithNbsUid();
        SrteCache.programAreaCodesMapWithNbsUid = cachingValueService.getAllProgramAreaCodesWithNbsUid();
        SrteCache.elrXrefsList = cachingValueService.getAllElrXref();
        SrteCache.coInfectionConditionCode = cachingValueService.getAllOnInfectionConditionCode();
        SrteCache.conditionCodes = cachingValueService.getAllConditionCode();
        for (ConditionCode obj : SrteCache.conditionCodes) {
            SrteCache.investigationFormConditionCode.put(obj.getConditionCd(), obj.getInvestigationFormCd());
        }
        SrteCache.labResultByDescMap = cachingValueService.getLabResultDesc();
        SrteCache.snomedCodeByDescMap = cachingValueService.getAllSnomedCode();
        SrteCache.labResultWithOrganismNameIndMap = cachingValueService.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();
        SrteCache.loinCodeWithComponentNameMap = cachingValueService.getAllLoinCodeWithComponentName();

//        saveCache();
    }

//
//    private void saveCache() {
//        saveToCacheHashMap( "loincCodes", SrteCache.loincCodesMap);
//        saveToCacheHashMap( "raceCodes", SrteCache.raceCodesMap);
//        saveToCacheHashMap( "programAreaCodes", SrteCache.programAreaCodesMap);
//        saveToCacheHashMap( "jurisdictionCode", SrteCache.jurisdictionCodeMap);
//        saveToCacheHashMap( "programAreaCodesWithNbsUid", SrteCache.programAreaCodesMapWithNbsUid);
//        saveToCacheHashMap( "jurisdictionCodeWithNbsUid", SrteCache.jurisdictionCodeMapWithNbsUid);
//        redisUtil.saveList( "elrXref", SrteCache.elrXrefsList);
//        saveToCacheHashMap( "coInfectionConditionCode", SrteCache.coInfectionConditionCode);
//        redisUtil.saveList( "conditionCode", SrteCache.conditionCodes);
//        saveToCacheHashMap( "labResulDesc", SrteCache.labResultByDescMap);
//        saveToCacheHashMap( "snomedCodeByDesc", SrteCache.snomedCodeByDescMap);
//        saveToCacheHashMap( "labResulDescWithOrgnismName", SrteCache.labResultWithOrganismNameIndMap);
//        saveToCacheHashMap( "loinCodeWithComponentName", SrteCache.loinCodeWithComponentNameMap);
//        saveToCacheHashMap( "investigationFormConditionCode", SrteCache.investigationFormConditionCode);
//
//    }

//    private void saveToCacheHashMap(String key,  HashMap<?, ?> target) {
//        redisUtil.saveHashMap(key, target);
//    }

}
