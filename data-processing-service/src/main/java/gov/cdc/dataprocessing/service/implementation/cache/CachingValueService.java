package gov.cdc.dataprocessing.service.implementation.cache;


import gov.cdc.dataprocessing.cache.cache_model.SrteCache;
import gov.cdc.dataprocessing.constant.elr.ELRConstant;
import gov.cdc.dataprocessing.exception.RtiCacheException;
import gov.cdc.dataprocessing.repository.nbs.srte.model.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.*;
import gov.cdc.dataprocessing.repository.nbs.srte.repository.custom.SrteCustomRepository;
import gov.cdc.dataprocessing.service.interfaces.cache.ICatchingValueService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IJurisdictionService;
import gov.cdc.dataprocessing.service.interfaces.jurisdiction.IProgramAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
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
 6809 - Calling transactional method with This. complaint
 2139 - exception rethrow complain
 3740 - parametrized  type for generic complaint
 1149 - replacing HashTable complaint
 112 - throwing dedicate exception complaint
 107 - max parameter complaint
 1195 - duplicate complaint
 1135 - Todos complaint
 6201 - instanceof check
 1192 - duplicate literal
 135 - for loop
 117 - naming
 */
@SuppressWarnings({"java:S125", "java:S3776", "java:S6204", "java:S1141", "java:S1118", "java:S1186", "java:S6809", "java:S6541", "java:S2139", "java:S3740",
        "java:S1149", "java:S112", "java:S107", "java:S1195", "java:S1135", "java:S6201", "java:S1192", "java:S135", "java:S117"})
public class CachingValueService implements ICatchingValueService {
    private final JurisdictionCodeRepository jurisdictionCodeRepository;
    private final CodeValueGeneralRepository codeValueGeneralRepository;
    private final ElrXrefRepository elrXrefRepository;
    private final RaceCodeRepository raceCodeRepository;
    private final StateCountyCodeValueRepository stateCountyCodeValueRepository;
    private final StateCodeRepository stateCodeRepository;
    private final LOINCCodeRepository loincCodeRepository;
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
        this.programAreaService = programAreaService;
        this.jurisdictionService = jurisdictionService;
        this.conditionCodeRepository = conditionCodeRepository;
        this.labResultRepository = labResultRepository;
        this.snomedCodeRepository = snomedCodeRepository;
        this.srteCustomRepository = srteCustomRepository;
    }

    public HashMap<String, String> getAllLoinCodeWithComponentName() throws RtiCacheException {
        // return loadCache(loincCodeRepository::findAll, LOINCCode::getLoincCode, LOINCCode::getComponentName);
        HashMap<String, String> result = new HashMap<>();

        List<LOINCCode> loincCodes = loincCodeRepository.findAll();

        for (LOINCCode loincCode : loincCodes) {
            result.put(loincCode.getLoincCode(), loincCode.getComponentName());
        }

        return result;
    }

    public HashMap<String, String> getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd() throws RtiCacheException {
        //    return loadCache(srteCustomRepository::getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd, LabResult::getLabResultCd, LabResult::getLabResultDescTxt);
        HashMap<String, String> result = new HashMap<>();

        List<LabResult> labResults = srteCustomRepository.getAllLabResultJoinWithLabCodingSystemWithOrganismNameInd();

        for (LabResult labResult : labResults) {
            result.put(labResult.getLabResultCd(), labResult.getLabResultDescTxt());
        }

        return result;
    }

    public HashMap<String, String> getAllSnomedCode() throws RtiCacheException {
        // return loadCache(snomedCodeRepository::findAll, SnomedCode::getSnomedCd, SnomedCode::getSnomedDescTxt);
        HashMap<String, String> result = new HashMap<>();

        List<SnomedCode> snomedCodes = snomedCodeRepository.findAll();

        for (SnomedCode snomedCode : snomedCodes) {
            result.put(snomedCode.getSnomedCd(), snomedCode.getSnomedDescTxt());
        }

        return result;
    }

    public HashMap<String, String> getLabResultDesc() throws RtiCacheException {
        // return loadCache(() -> labResultRepository.findLabResultByDefaultLabAndOrgNameN().orElse(Collections.emptyList()), LabResult::getLabResultCd, LabResult::getLabResultDescTxt);
        HashMap<String, String> result = new HashMap<>();

        List<LabResult> labResults = labResultRepository.findLabResultByDefaultLabAndOrgNameN()
                .orElse(Collections.emptyList());

        for (LabResult labResult : labResults) {
            result.put(labResult.getLabResultCd(), labResult.getLabResultDescTxt());
        }

        return result;
    }

    public HashMap<String, String> getAOELOINCCodes() throws RtiCacheException {
        // return loadCache(() -> loincCodeRepository.findLoincCodes().orElse(Collections.emptyList()), LOINCCode::getLoincCode, LOINCCode::getLoincCode);
        HashMap<String, String> result = new HashMap<>();

        List<LOINCCode> loincCodes = loincCodeRepository.findLoincCodes()
                .orElse(Collections.emptyList());

        for (LOINCCode loincCode : loincCodes) {
            result.put(loincCode.getLoincCode(), loincCode.getLoincCode());
        }

        return result;
    }

    public HashMap<String, String> getRaceCodes() throws RtiCacheException {
        //  return loadCache(() -> raceCodeRepository.findAllActiveRaceCodes().orElse(Collections.emptyList()), RaceCode::getCode, RaceCode::getCodeShortDescTxt);
        HashMap<String, String> result = new HashMap<>();

        List<RaceCode> raceCodes = raceCodeRepository.findAllActiveRaceCodes()
                .orElse(Collections.emptyList());

        for (RaceCode raceCode : raceCodes) {
            result.put(raceCode.getCode(), raceCode.getCodeShortDescTxt());
        }

        return result;
    }

    public HashMap<String, String> getAllProgramAreaCodes() throws RtiCacheException {
        //    return loadCache(programAreaService::getAllProgramAreaCode, ProgramAreaCode::getProgAreaCd, ProgramAreaCode::getProgAreaDescTxt);
        HashMap<String, String> result = new HashMap<>();
        List<ProgramAreaCode> programAreaCodes = programAreaService.getAllProgramAreaCode();
        for (ProgramAreaCode programAreaCode : programAreaCodes) {
            result.put(programAreaCode.getProgAreaCd(), programAreaCode.getProgAreaDescTxt());
        }

        return result;
    }

    public HashMap<String, Integer> getAllProgramAreaCodesWithNbsUid() throws RtiCacheException {
        //   return loadCache(programAreaService::getAllProgramAreaCode, ProgramAreaCode::getProgAreaCd, ProgramAreaCode::getNbsUid);
        HashMap<String, Integer> result = new HashMap<>();
        List<ProgramAreaCode> programAreaCodes = programAreaService.getAllProgramAreaCode();
        for (ProgramAreaCode programAreaCode : programAreaCodes) {
            result.put(programAreaCode.getProgAreaCd(), programAreaCode.getNbsUid());
        }

        return result;
    }

    public HashMap<String, String> getAllJurisdictionCode() throws RtiCacheException {
        //return loadCache(jurisdictionService::getJurisdictionCode, JurisdictionCode::getCode, JurisdictionCode::getCodeDescTxt);
        HashMap<String, String> result = new HashMap<>();
        List<JurisdictionCode> jurisdictionCodes = jurisdictionService.getJurisdictionCode();
        for (JurisdictionCode jurisdictionCode : jurisdictionCodes) {
            String key = jurisdictionCode.getCode();         // Generate the key
            String value = jurisdictionCode.getCodeDescTxt(); // Generate the value
            result.put(key, value);
        }

        return result;
    }

    public HashMap<String, Integer> getAllJurisdictionCodeWithNbsUid() throws RtiCacheException {
        //return loadCache(jurisdictionService::getJurisdictionCode, JurisdictionCode::getCode, JurisdictionCode::getNbsUid);
        HashMap<String, Integer> result = new HashMap<>();
        List<JurisdictionCode> jurisdictionCodes = jurisdictionService.getJurisdictionCode();
        for (JurisdictionCode jurisdictionCode : jurisdictionCodes) {
            String key = jurisdictionCode.getCode();  // Generate the key
            Integer value = jurisdictionCode.getNbsUid(); // Generate the value
            result.put(key, value);
        }

        return result;
    }

    public List<ElrXref> getAllElrXref() throws RtiCacheException {
        try {
            return elrXrefRepository.findAll();
        } catch (Exception e) {
            throw new RtiCacheException(e.getMessage(), e);
        }
    }

    public HashMap<String, String> getAllOnInfectionConditionCode() throws RtiCacheException {
        //return loadCache(() -> conditionCodeRepository.findCoInfectionConditionCode().orElse(Collections.emptyList()), ConditionCode::getConditionCd, ConditionCode::getCoinfectionGrpCd);
        HashMap<String, String> result = new HashMap<>();
        List<ConditionCode> conditionCodes = conditionCodeRepository.findCoInfectionConditionCode().orElse(Collections.emptyList());
        for (ConditionCode conditionCode : conditionCodes) {
            String key = conditionCode.getConditionCd();      // Generate the key
            String value = conditionCode.getCoinfectionGrpCd(); // Generate the value
            result.put(key, value);
        }
        return result;
    }

    public List<ConditionCode> getAllConditionCode() throws RtiCacheException {
        try {
            return conditionCodeRepository.findAllConditionCode().orElse(Collections.emptyList());
        } catch (Exception e) {
            throw new RtiCacheException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("java:S2696")
    public HashMap<String, String> getCodedValues(String pType, String key) throws RtiCacheException {
        if (!SrteCache.codedValuesMap.containsKey(key) || SrteCache.codedValuesMap.get(key).isEmpty()) {
            SrteCache.codedValuesMap.putAll(getCodedValuesCallRepos(pType));
        }
        return SrteCache.codedValuesMap;
    }

    @SuppressWarnings("java:S2696")
    public String getCodeDescTxtForCd(String code, String codeSetNm) throws RtiCacheException {
        if ( SrteCache.codeDescTxtMap.get(code) == null || SrteCache.codeDescTxtMap.get(code).isEmpty()) {
            SrteCache.codeDescTxtMap.putAll(getCodedValuesCallRepos(codeSetNm));
        }
        return SrteCache.codeDescTxtMap.get(code);
    }



    public String findToCode(String fromCodeSetNm, String fromCode, String toCodeSetNm) throws RtiCacheException {
        try {
            return elrXrefRepository.findToCodeByConditions(fromCodeSetNm, fromCode, toCodeSetNm).map(ElrXref::getToCode).orElse("");
        } catch (Exception e) {
            throw new RtiCacheException(e.getMessage(), e);
        }
    }

    public String getCountyCdByDesc(String county, String stateCd) throws RtiCacheException {
        if (county == null || stateCd == null) {
            return null;
        }

        // Normalize county input
        String cnty = county.toUpperCase();
        if (!cnty.endsWith("COUNTY")) {
            cnty += " COUNTY";
        }

        // Check if the static map is initialized and contains the required value
        if (SrteCache.countyCodeByDescMap.get(cnty) == null || SrteCache.countyCodeByDescMap.get(cnty).isEmpty()) {
            SrteCache.countyCodeByDescMap.putAll(getCountyCdByDescCallRepos(stateCd));
        }

        // Return the county code
        return SrteCache.countyCodeByDescMap.get(cnty);
    }

    public List<CodeValueGeneral> findCodeValuesByCodeSetNmAndCode(String codeSetNm, String code) {
        return codeValueGeneralRepository.findCodeValuesByCodeSetNmAndCode(codeSetNm, code).orElseGet(ArrayList::new);
    }

    public StateCode findStateCodeByStateNm(String stateNm) {
        return stateCodeRepository.findStateCdByStateName(stateNm).orElseGet(StateCode::new);
    }



    public HashMap<String, String> getCodedValuesCallRepos(String pType) throws RtiCacheException {
        if ("S_JURDIC_C".equals(pType)) {
            return getJurisdictionCode();
        } else {
            return getCodedValue(pType);
        }
    }

    public HashMap<String, String> getCodedValue(String code) throws RtiCacheException {
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
            throw new RtiCacheException(e.getMessage(), e);
        }
        return map;
    }

    private HashMap<String, String> getJurisdictionCode() throws RtiCacheException {
//        return loadCache(() -> jurisdictionCodeRepository.findJurisdictionCodeValues().orElse(Collections.emptyList()), JurisdictionCode::getCode, JurisdictionCode::getCodeDescTxt);

        HashMap<String, String> result = new HashMap<>();
        List<JurisdictionCode> jurisdictionCodes = jurisdictionCodeRepository.findJurisdictionCodeValues()
                .orElse(Collections.emptyList());

        for (JurisdictionCode code : jurisdictionCodes) {
            String key = code.getCode();         // Generate the key
            String value = code.getCodeDescTxt(); // Generate the value
            result.put(key, value);
        }

        return result;
    }

    protected HashMap<String, String> getCountyCdByDescCallRepos(String stateCd) throws RtiCacheException {
//        return loadCache(() -> {
//            if (stateCd == null || stateCd.trim().isEmpty()) {
//                return stateCountyCodeValueRepository.findByIndentLevelNbr().orElse(Collections.emptyList());
//            } else {
//                return stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(stateCd).orElse(Collections.emptyList());
//            }
//        }, stateCountyCodeValue -> stateCountyCodeValue.getCode() + " COUNTY", StateCountyCodeValue::getAssigningAuthorityDescTxt);

        HashMap<String, String> result = new HashMap<>();

        List<StateCountyCodeValue> stateCountyCodeValues;
        if (stateCd == null || stateCd.trim().isEmpty()) {
            stateCountyCodeValues = stateCountyCodeValueRepository.findByIndentLevelNbr().orElse(Collections.emptyList());
        } else {
            stateCountyCodeValues = stateCountyCodeValueRepository.findByIndentLevelNbrAndParentIsCdOrderByCodeDescTxt(stateCd).orElse(Collections.emptyList());
        }

        // Populate the result map
        for (StateCountyCodeValue value : stateCountyCodeValues) {
            String key = value.getCode() + " COUNTY"; // Generate the key
            String assigningAuthorityDescTxt = value.getAssigningAuthorityDescTxt(); // Generate the value
            result.put(key, assigningAuthorityDescTxt);
        }

        return result;
    }

//    private <T, K, V> HashMap<K, V> loadCache(CacheLoader<T> loader, KeyExtractor<T, K> keyExtractor, ValueExtractor<T, V> valueExtractor) throws RtiCacheException {
//        HashMap<K, V> map = new HashMap<>();
//        try {
//            List<T> result = loader.load();
//            for (T obj : result) {
//                map.put(keyExtractor.extract(obj), valueExtractor.extract(obj));
//            }
//        } catch (Exception e) {
//            throw new RtiCacheException(e.getMessage(), e);
//        }
//        return map;
//    }

//    @FunctionalInterface
//    private interface CacheLoader<T> {
//        List<T> load() throws RtiCacheException;
//    }
//
//    @FunctionalInterface
//    private interface KeyExtractor<T, K> {
//        K extract(T obj);
//    }
//
//    @FunctionalInterface
//    private interface ValueExtractor<T, V> {
//        V extract(T obj);
//    }
}