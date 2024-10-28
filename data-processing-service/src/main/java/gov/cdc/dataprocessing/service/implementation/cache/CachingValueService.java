package gov.cdc.dataprocessing.service.implementation.cache;

import gov.cdc.dataprocessing.cache.SrteCache;
import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.exception.DataProcessingException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.custom.SrteCustomRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
/**
 125 - Comment complaint
 3776 - Complex complaint
 6204 - Forcing convert to stream to list complaint
 1141 - Nested complaint
  1118 - Private constructor complaint
 1186 - Add nested comment for empty constructor complaint
 6809 - Calling transactional method with This. complaint
 6541 - brain method complaint
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541"})
public class CachingValueService implements ICatchingValueService {
    private final JurisdictionCodeRepository jurisdictionCodeRepository;
    private final CodeValueGeneralRepository codeValueGeneralRepository;
    private final ElrXrefRepository elrXrefRepository;
    private final RaceCodeRepository raceCodeRepository;
    private final StateCountyCodeValueRepository stateCountyCodeValueRepository;
    private final StateCodeRepository stateCodeRepository;
    private final LOINCCodeRepository loincCodeRepository;
    private final CacheManager cacheManager;
    private final IProgramAreaService programAreaService;
    private final IJurisdictionService jurisdictionService;
    private final ConditionCodeRepository conditionCodeRepository;
    private final LabResultRepository labResultRepository;
    private final SnomedCodeRepository snomedCodeRepository;
    private final SrteCustomRepository srteCustomRepository;

    public CachingValueService(JurisdictionCodeRepository jurisdictionCodeRepository,
                               CodeValueGeneralRepository codeValueGeneralRepository,
                               ElrXrefRepository elrXrefRepository,
                               RaceCodeRepository raceCodeRepository,
                               StateCountyCodeValueRepository stateCountyCodeValueRepository,
                               StateCodeRepository stateCodeRepository,
                               LOINCCodeRepository loincCodeRepository,
                               CacheManager cacheManager,
                               IProgramAreaService programAreaService,
                               IJurisdictionService jurisdictionService,
                               ConditionCodeRepository conditionCodeRepository,
                               LabResultRepository labResultRepository,
                               SnomedCodeRepository snomedCodeRepository,
                               SrteCustomRepository srteCustomRepository) {
        this.jurisdictionCodeRepository = jurisdictionCodeRepository;
        this.codeValueGeneralRepository = codeValueGeneralRepository;
        this.elrXrefRepository = elrXrefRepository;
        this.raceCodeRepository = raceCodeRepository;
        this.stateCountyCodeValueRepository = stateCountyCodeValueRepository;
        this.stateCodeRepository = stateCodeRepository;
        this.loincCodeRepository = loincCodeRepository;
        this.cacheManager = cacheManager;
        this.programAreaService = programAreaService;
        this.jurisdictionService = jurisdictionService;
        this.conditionCodeRepository = conditionCodeRepository;
        this.labResultRepository = labResultRepository;
        this.snomedCodeRepository = snomedCodeRepository;
        this.srteCustomRepository = srteCustomRepository;
    }

    @Cacheable(cacheNames = "srte", key = "'loinCodeWithComponentName'")
    public HashMap<String, String> getAllLoinCodeWithComponentName() throws DataProcessingException {
        return loadCache(loincCodeRepository::findAll, LOINCCode::getLoincCode, LOINCCode::getComponentName);
    }

    @Cacheable(cacheNames = "srte", key = "'labResulDescWithOrgnismName'")
    public HashMap<String, String> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd() throws DataProcessingException {
        return loadCache(srteCustomRepository::getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd, LabResult::getLabResultCd, LabResult::getLabResultDescTxt);
    }

    @Cacheable(cacheNames = "srte", key = "'snomedCodeByDesc'")
    public HashMap<String, String> getAllSnomedCode() throws DataProcessingException {
        return loadCache(snomedCodeRepository::findAll, SnomedCode::getSnomedCd, SnomedCode::getSnomedDescTxt);
    }

    @Cacheable(cacheNames = "srte", key = "'labResulDesc'")
    public HashMap<String, String> getLabResultDesc() throws DataProcessingException {
        return loadCache(() -> labResultRepository.findLabResultByDefaultLabAndOrgNameN().orElse(Collections.emptyList()), LabResult::getLabResultCd, LabResult::getLabResultDescTxt);
    }

    @Cacheable(cacheNames = "srte", key = "'loincCodes'")
    public HashMap<String, String> getAOELOINCCodes() throws DataProcessingException {
        return loadCache(() -> loincCodeRepository.findLoincCodes().orElse(Collections.emptyList()), LOINCCode::getLoincCode, LOINCCode::getLoincCode);
    }

    @Cacheable(cacheNames = "srte", key = "'raceCodes'")
    public HashMap<String, String> getRaceCodes() throws DataProcessingException {
        return loadCache(() -> raceCodeRepository.findAllActiveRaceCodes().orElse(Collections.emptyList()), RaceCode::getCode, RaceCode::getCodeShortDescTxt);
    }

    @Cacheable(cacheNames = "srte", key = "'programAreaCodes'")
    public HashMap<String, String> getAllProgramAreaCodes() throws DataProcessingException {
        return loadCache(programAreaService::getAllProgramAreaCode, ProgramAreaCode::getProgAreaCd, ProgramAreaCode::getProgAreaDescTxt);
    }

    @Cacheable(cacheNames = "srte", key = "'programAreaCodesWithNbsUid'")
    public HashMap<String, Integer> getAllProgramAreaCodesWithNbsUid() throws DataProcessingException {
        return loadCache(programAreaService::getAllProgramAreaCode, ProgramAreaCode::getProgAreaCd, ProgramAreaCode::getNbsUid);
    }

    @Cacheable(cacheNames = "srte", key = "'jurisdictionCode'")
    public HashMap<String, String> getAllJurisdictionCode() throws DataProcessingException {
        return loadCache(jurisdictionService::getJurisdictionCode, JurisdictionCode::getCode, JurisdictionCode::getCodeDescTxt);
    }

    @Cacheable(cacheNames = "srte", key = "'jurisdictionCodeWithNbsUid'")
    public HashMap<String, Integer> getAllJurisdictionCodeWithNbsUid() throws DataProcessingException {
        return loadCache(jurisdictionService::getJurisdictionCode, JurisdictionCode::getCode, JurisdictionCode::getNbsUid);
    }

    @Cacheable(cacheNames = "srte", key = "'elrXref'")
    public List<ElrXref> getAllElrXref() throws DataProcessingException {
        try {
            return elrXrefRepository.findAll();
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    @Cacheable(cacheNames = "srte", key = "'coInfectionConditionCode'")
    public HashMap<String, String> getAllOnInfectionConditionCode() throws DataProcessingException {
        return loadCache(() -> conditionCodeRepository.findCoInfectionConditionCode().orElse(Collections.emptyList()), ConditionCode::getConditionCd, ConditionCode::getCoinfectionGrpCd);
    }

    @Cacheable(cacheNames = "srte", key = "'conditionCode'")
    public List<ConditionCode> getAllConditionCode() throws DataProcessingException {
        try {
            return conditionCodeRepository.findAllConditionCode().orElse(Collections.emptyList());
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("java:S2696")
    public HashMap<String, String> getCodedValues(String pType, String key) throws DataProcessingException {
        Cache cache = cacheManager.getCache("srte");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get("codedValues");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof HashMap) {
                    SrteCache.codedValuesMap = (HashMap<String, String>) cachedObject;
                }
            }
        }
        if (cache != null && (SrteCache.codedValuesMap.get(key) == null || SrteCache.codedValuesMap.get(key).isEmpty())) {
            SrteCache.codedValuesMap.putAll(getCodedValuesCallRepos(pType));
            cache.put("codedValues", SrteCache.codedValuesMap);
        }
        return SrteCache.codedValuesMap;
    }

    @SuppressWarnings("java:S2696")
    public String getCodeDescTxtForCd(String code, String codeSetNm) throws DataProcessingException {
        Cache cache = cacheManager.getCache("srte");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get("codeDescTxt");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof HashMap) {
                    SrteCache.codeDescTxtMap = (HashMap<String, String>) cachedObject;
                }
            }
        }
        if (cache != null && (SrteCache.codeDescTxtMap.get(code) == null || SrteCache.codeDescTxtMap.get(code).isEmpty())) {
            SrteCache.codeDescTxtMap.putAll(getCodedValuesCallRepos(codeSetNm));
            cache.put("codeDescTxt", SrteCache.codeDescTxtMap);
        }
        return SrteCache.codeDescTxtMap.get(code);
    }

    public String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws DataProcessingException {
        try {
            return elrXrefRepository.findToCodeByConditions(fromCodeSetNm, fromCode, toCodeSetNm).map(ElrXref::getToCode).orElse("");
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("java:S2696")
    public String getCountyCdByDesc(String county, String stateCd) throws DataProcessingException {
        if (county == null || stateCd == null) {
            return null;
        }
        String cnty = county.toUpperCase();
        if (!cnty.endsWith("COUNTY")) {
            cnty += " COUNTY";
        }

        Cache cache = cacheManager.getCache("srte");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get("countyCodeByDesc");
            if (valueWrapper != null) {
                Object cachedObject = valueWrapper.get();
                if (cachedObject instanceof HashMap) {
                    SrteCache.countyCodeByDescMap = (HashMap<String, String>) cachedObject;
                }
            }
        }

        if (cache != null && (SrteCache.countyCodeByDescMap.get(cnty) == null || SrteCache.countyCodeByDescMap.get(cnty).isEmpty())) {
            SrteCache.countyCodeByDescMap.putAll(getCountyCdByDescCallRepos(stateCd));
            cache.put("countyCodeByDesc", SrteCache.countyCodeByDescMap);
        }

        return SrteCache.countyCodeByDescMap.get(cnty);
    }

    public List<CodeValueGeneral> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code) {
        return codeValueGeneralRepository.findCodeValuesByCodeSetNmAndCode(codeSetNm, code).orElseGet(ArrayList::new);
    }

    public StateCode findStateCodeByStateNm(String stateNm) {
        return stateCodeRepository.findStateCdByStateName(stateNm).orElseGet(StateCode::new);
    }

    public List<CodeValueGeneral> getGeneralCodedValue(String code) {
        return codeValueGeneralRepository.findCodeValuesByCodeSetNm(code).orElseGet(ArrayList::new);
    }

    public HashMap<String, String> getCodedValuesCallRepos(String pType) throws DataProcessingException {
        if ("S_JURDIC_C".equals(pType)) {
            return getJurisdictionCode();
        } else {
            return getCodedValue(pType);
        }
    }

    public HashMap<String, String> getCodedValue(String code) throws DataProcessingException {
        HashMap<String, String> map = new HashMap<>();
        try {
            List<CodeValueGeneral> codeValueGeneralList;
            if (ELRConstant.ELR_LOG_PROCESS.equals(code)) {
                codeValueGeneralList = codeValueGeneralRepository.findCodeDescriptionsByCodeSetNm(code).orElse(Collections.emptyList());
                for (CodeValueGeneral obj : codeValueGeneralList) {
                    map.put(obj.getCode(), obj.getCodeDescTxt());
                }
            } else {
                codeValueGeneralList = codeValueGeneralRepository.findCodeValuesByCodeSetNm(code).orElse(Collections.emptyList());
                for (CodeValueGeneral obj : codeValueGeneralList) {
                    map.put(obj.getCode(), obj.getCodeShortDescTxt());
                }
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return map;
    }

    private HashMap<String, String> getJurisdictionCode() throws DataProcessingException {
        return loadCache(() -> jurisdictionCodeRepository.findJurisdictionCodeValues().orElse(Collections.emptyList()), JurisdictionCode::getCode, JurisdictionCode::getCodeDescTxt);
    }

    protected HashMap<String, String> getCountyCdByDescCallRepos(String stateCd) throws DataProcessingException {
        return loadCache(() -> {
            if (stateCd == null || stateCd.trim().isEmpty()) {
                return stateCountyCodeValueRepository.findByIndentLevelNbr().orElse(Collections.emptyList());
            } else {
                return stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(stateCd).orElse(Collections.emptyList());
            }
        }, stateCountyCodeValue -> stateCountyCodeValue.getCode() + " COUNTY", StateCountyCodeValue::getAssigningAuthorityDescTxt);
    }

    private <T, K, V> HashMap<K, V> loadCache(CacheLoader<T> loader, KeyExtractor<T, K> keyExtractor, ValueExtractor<T, V> valueExtractor) throws DataProcessingException {
        HashMap<K, V> map = new HashMap<>();
        try {
            List<T> result = loader.load();
            for (T obj : result) {
                map.put(keyExtractor.extract(obj), valueExtractor.extract(obj));
            }
        } catch (Exception e) {
            throw new DataProcessingException(e.getMessage(), e);
        }
        return map;
    }

    @FunctionalInterface
    private interface CacheLoader<T> {
        List<T> load() throws DataProcessingException;
    }

    @FunctionalInterface
    private interface KeyExtractor<T, K> {
        K extract(T obj);
    }

    @FunctionalInterface
    private interface ValueExtractor<T, V> {
        V extract(T obj);
    }
}
