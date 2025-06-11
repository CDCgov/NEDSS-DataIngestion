package gov.cdc.dataprocessing.service.implementation.cache;


import gov.cdc.dataprocessing.cache.cache_model.SrteCache;
import gov.cdc.dataprocessing.constant.enums.ObjectName;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.ConditionCode;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@SuppressWarnings("java:S2696")
@Component
public class ManagerCacheService{

    private static final Logger logger = LoggerFactory.getLogger(ManagerCacheService.class);

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

    public String getCache(ObjectName objectName, String key) throws DataProcessingException {
        return switch (objectName) {
            case PROGRAM_AREA_CODES -> SrteCache.programAreaCodesMap.get(key);
            case JURISDICTION_CODES -> SrteCache.jurisdictionCodeMap.get(key);
            case PROGRAM_AREA_CODES_WITH_NBS_UID -> toStringSafe(SrteCache.programAreaCodesMapWithNbsUid.get(key));
            case JURISDICTION_CODE_WITH_NBS_UID -> toStringSafe(SrteCache.jurisdictionCodeMapWithNbsUid.get(key));
            case LAB_RESULT_DESC -> SrteCache.labResultByDescMap.get(key);
            case SNOMED_CODE_BY_DESC -> SrteCache.snomedCodeByDescMap.get(key);
            case LAB_RESULT_DESC_WITH_ORGANISM_NAME -> SrteCache.labResultWithOrganismNameIndMap.get(key);
            case LOIN_CODE_WITH_COMPONENT_NAME -> SrteCache.loinCodeWithComponentNameMap.get(key);
            case INVESTIGATION_FORM_CONDITION_CODE -> SrteCache.investigationFormConditionCode.get(key);
            case FIND_TO_CODE -> {
                String[] parts = splitKey(key, 3);
                yield cachingValueService.findToCode(parts[0], parts[1], parts[2]);
            }
            case GET_CODE_DESC_TXT_FOR_CD -> {
                String[] parts = splitKey(key, 2);
                yield cachingValueService.getCodeDescTxtForCd(parts[0], parts[1]);
            }
            case GET_COUNTY_CD_BY_DESC -> {
                String[] parts = splitKey(key, 2);
                yield cachingValueService.getCountyCdByDesc(parts[0], parts[1]);
            }
            case CODED_VALUE -> {
                String[] parts = splitKey(key, 2);
                var res = cachingValueService.getCodedValues(parts[0], parts[1]);
                yield res.get(parts[1]);
            }
            case GET_CODED_VALUES_CALL_REPOS -> cachingValueService.getCodedValuesCallRepos(key).get(key);
            case GET_CODED_VALUE -> cachingValueService.getCodedValue(key).get(key);
            case JURISDICTION_CODE_MAP_WITH_NBS_UID_KEY_SET ->
                    String.join(", ", SrteCache.jurisdictionCodeMapWithNbsUid.keySet());
            default -> "";
        };
    }

    private String[] splitKey(String key, int expectedParts) {
        String[] parts = key.split("~", -1);
        String[] result = new String[expectedParts];
        for (int i = 0; i < expectedParts; i++) {
            result[i] = i < parts.length ? parts[i] : "";
        }
        return result;
    }

    private String toStringSafe(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    public boolean containKey(ObjectName objectName, String key) throws DataProcessingException {
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

    @SuppressWarnings("java:S2696")
    @PostConstruct
    public void loadCache() throws DataProcessingException {
        logger.info("Load Cache");
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
        logger.info("Load Cache Completed");

    }


}